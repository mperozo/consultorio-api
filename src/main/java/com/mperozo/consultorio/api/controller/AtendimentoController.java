package com.mperozo.consultorio.api.controller;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mperozo.consultorio.api.assembler.AtendimentoDTOAssembler;
import com.mperozo.consultorio.api.dto.AtendimentoDTO;
import com.mperozo.consultorio.api.dto.AtualizaStatusAtendimentoDTO;
import com.mperozo.consultorio.api.dto.StatusAtendimentoDTO;
import com.mperozo.consultorio.exception.BusinessException;
import com.mperozo.consultorio.model.entity.Atendimento;
import com.mperozo.consultorio.model.enums.StatusAtendimentoEnum;
import com.mperozo.consultorio.service.AtendimentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

	@Autowired
	private final AtendimentoService atendimentoService;

	@Autowired
	private final AtendimentoDTOAssembler atendimentoDTOAssembler;

	@PostMapping("/salvar")
	public ResponseEntity salvarAtendimento(@RequestBody AtendimentoDTO atendimentoDTO) {
		
		try {
			Atendimento atendimento = atendimentoDTOAssembler.toEntity(atendimentoDTO);
			Atendimento atendimentoAgendado = atendimentoService.salvarAtendimento(atendimento);
			
			return new ResponseEntity(atendimentoAgendado, HttpStatus.CREATED); 
		} catch(BusinessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("{id}")
	public ResponseEntity atualizarAtendimento( @PathVariable("id") Long id, @RequestBody AtendimentoDTO atendimentoDTO ) {
		try {
			Atendimento atendimentoComNovosDados = atendimentoDTOAssembler.toEntity(atendimentoDTO);
			Atendimento atendimentoAutalizado = atendimentoService.atualizarAtendimento(atendimentoComNovosDados);
			return ResponseEntity.ok(atendimentoAutalizado); 
		} catch(BusinessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletarAtendimento( @PathVariable("id") Long id) {
		
		try {
			atendimentoService.excluir(id);
			return new ResponseEntity(HttpStatus.NO_CONTENT); 
		} catch(BusinessException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping
	public ResponseEntity buscar( 
			@RequestParam(value = "idMedico", required = false) Long idMedico,
			@RequestParam(value = "idPaciente", required = false) Long idPaciente,
			@RequestParam(value = "dataAtendimento", required = false) LocalDate dataAtendimento,
			@RequestParam(value = "statusAtendimento", required = false) StatusAtendimentoEnum statusAtendimento
			) {
		
		AtendimentoDTO atendimentoDTOFilter = AtendimentoDTO.builder()
				.idMedico(idMedico)
				.idPaciente(idPaciente)
				.dataAtendimento(dataAtendimento)
				.statusAtendimento(statusAtendimento).build();
		
		Atendimento atendimentoFiltro = atendimentoDTOAssembler.toEntity(atendimentoDTOFilter);
		
		List<Atendimento> atendimentos = atendimentoService.buscar(atendimentoFiltro);
		
		List<AtendimentoDTO> atendimentosDTOList = atendimentoDTOAssembler.toDTOList(atendimentos);
		
		return ResponseEntity.ok(atendimentosDTOList);
	}
	
	@GetMapping("{id}")
	public ResponseEntity buscar(@PathVariable("id") Long id) {
		return atendimentoService.buscarPorId(id)
				.map( atendimento -> new ResponseEntity( atendimentoDTOAssembler.toDTO(atendimento) , HttpStatus.OK) )
				.orElseGet( () -> new ResponseEntity(HttpStatus.NOT_FOUND) );
	}
	
	@GetMapping("/status-disponiveis")
	public ResponseEntity listarStatusDisponiveis() {
		
		List<StatusAtendimentoDTO> statusAtendimentoDTOList = new LinkedList<StatusAtendimentoDTO>();
		statusAtendimentoDTOList.add(new StatusAtendimentoDTO("...", ""));
		
		for (StatusAtendimentoEnum status : StatusAtendimentoEnum.values()) {
			statusAtendimentoDTOList.add(new StatusAtendimentoDTO(status.getLabel(), status.getValue()));
		}
		
		return ResponseEntity.ok(statusAtendimentoDTOList);
	}
	
	@PutMapping("{id}/atualizar-status")
	public ResponseEntity atualizarStatusAtendimento(@PathVariable("id") Long id, @RequestBody AtualizaStatusAtendimentoDTO dto) {
		try {
			Atendimento atendimentoAtualizado = atendimentoService.atualizarStatusAtendimento(id, dto.getStatus());
			return ResponseEntity.ok(atendimentoAtualizado);
		} catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage()); 
		}
	}
	
}
