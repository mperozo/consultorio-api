package com.mperozo.consultorio.model.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PLANO_SAUDE", schema = "CONSULTORIO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanoSaude {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "NUMERO_PLANO_SAUDE")
	private Integer numeroPlanoSaude;
	
	@ManyToOne
	@JoinColumn(name = "ID_PACIENTE")
	private Paciente paciente;
	
	@ManyToOne
	@JoinColumn(name = "ID_OPERADORA_PLANO_SAUDE")
	private OperadoraPlanoSaude operadoraPlanoSaude;
	
	@Column(name = "DATA_HORA_INCLUSAO")
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	@NotNull(message = "Data de inclusão é obrigatória.")
	private LocalDateTime dataHoraInclusao;
	
	@Column(name = "DATA_HORA_ALTERACAO")
	@Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
	private LocalDateTime dataHoraAlteracao;
}
