package br.com.ehgm.cobranca.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.ehgm.cobranca.model.StatusTitulo;
import br.com.ehgm.cobranca.model.Titulo;
import br.com.ehgm.cobranca.repository.filter.TituloFilter;
import br.com.ehgm.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {

	private static final String CADASTRO_VIEW = "CadastroTitulo";
	
	@Autowired
	private CadastroTituloService cadastroTituloService;

	@RequestMapping(value = "/novo")
	public ModelAndView novo() {
		ModelAndView modelAndView = new ModelAndView("CadastroTitulo");
		modelAndView.addObject(new Titulo());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes attributes) {
		if(errors.hasErrors()){
			return "CadastroTitulo";
		}
		try{
			cadastroTituloService.salvar(titulo);
			attributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");
			return "redirect:/titulos/novo";
		}catch (IllegalArgumentException e) {
			errors.rejectValue("dataVencimento", null, e.getMessage());
			return CADASTRO_VIEW;
		}
	}
	
	@RequestMapping
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro){
		List<Titulo> todosTitulos = cadastroTituloService.filtrar(filtro);
		ModelAndView modelAndView = new ModelAndView("PesquisaTitulos");
		modelAndView.addObject("titulos", todosTitulos);
		return modelAndView;
	}
	
	@RequestMapping("{codigo}")
	public ModelAndView edicao(@PathVariable("codigo") Titulo titulo){
		ModelAndView modelAndView = new ModelAndView(CADASTRO_VIEW);
		modelAndView.addObject(titulo);
		return modelAndView;
	}

	@RequestMapping(value="{codigo}", method=RequestMethod.DELETE)
	public String excluir(@PathVariable Long codigo, RedirectAttributes attributes){
		cadastroTituloService.excluir(codigo);
		attributes.addFlashAttribute("mensagem", "Título excluido com sucesso!");
		return "redirect:/titulos";
	}
	
	@RequestMapping(value="/{codigo}/receber", method=RequestMethod.PUT)
	public @ResponseBody String receber(@PathVariable Long codigo){
		return cadastroTituloService.receber(codigo);
	}
	
	@ModelAttribute("todosStatusTitulo")
	public List<StatusTitulo> todosStatusTitulo() {
		return Arrays.asList(StatusTitulo.values());
	}
}