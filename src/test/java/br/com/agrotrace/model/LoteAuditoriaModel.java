package br.com.agrotrace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoteAuditoriaModel {
    private String codigoCar;
    private String cultura;
    private Double pesoToneladas;
    private Integer focosQueimadaRecentes;
    private Boolean regularidadeTrabalhista;
}