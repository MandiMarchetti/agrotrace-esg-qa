# language: pt
@regressivo @esg
Funcionalidade: Auditoria e Rastreabilidade Socioambiental de Lotes Agrícolas (ESG)
  Como um auditor de conformidade do agronegócio
  Eu quero submeter e consultar lotes de produção agrícola na API AgroTrace
  Para garantir que apenas produtos livres de queimadas, desmatamento e trabalho irregular recebam o Selo Verde

  @smoke @caminhoFeliz @contrato
  Cenário: Registrar lote agrícola em total conformidade ESG com sucesso e validar contrato
    Dado que possuo os dados de um lote agrícola para auditoria:
      | campo                   | valor                                               |
      | codigoCar               | MT-5107925-78945612301234567890123456789012         |
      | cultura                 | SOJA                                                |
      | pesoToneladas           | 150.5                                               |
      | focosQueimadaRecentes   | 0                                                   |
      | regularidadeTrabalhista | true                                                |
    Quando eu enviar a requisição de auditoria ESG do lote
    Então o status code da resposta deve ser 201
    E o corpo da resposta deve conter o status de conformidade "APROVADO_EXPORTACAO" e selo verde "true"
    E a resposta deve respeitar o contrato JSON Schema "auditoria-sucesso-schema.json"

  @negativo @ambiental
  Cenário: Reprovar auditoria de lote oriundo de propriedade com focos de queimada recentes
    Dado que possuo os dados de um lote agrícola para auditoria:
      | campo                   | valor                                               |
      | codigoCar               | PA-1504208-99988877766655544433322211100099         |
      | cultura                 | SOJA                                                |
      | pesoToneladas           | 300.0                                               |
      | focosQueimadaRecentes   | 4                                                   |
      | regularidadeTrabalhista | true                                                |
    Quando eu enviar a requisição de auditoria ESG do lote
    Então o status code da resposta deve ser 422
    E o corpo da resposta deve retornar o erro "VIOLACAO_AMBIENTAL" e mensagem "Lote reprovado: Detectados focos de queimada recentes na propriedade."
    E a resposta deve respeitar o contrato JSON Schema "auditoria-erro-schema.json"

  @negativo @governanca
  Cenário: Impedir registro de auditoria sem código do Cadastro Ambiental Rural (CAR)
    Dado que possuo os dados de um lote agrícola para auditoria:
      | campo                   | valor                                               |
      | codigoCar               |                                                     |
      | cultura                 | CAFE                                                |
      | pesoToneladas           | 80.0                                                |
      | focosQueimadaRecentes   | 0                                                   |
      | regularidadeTrabalhista | true                                                |
    Quando eu enviar a requisição de auditoria ESG do lote
    Então o status code da resposta deve ser 400
    E o corpo da resposta deve retornar o erro "DADOS_INVALIDOS" e mensagem "O código do Cadastro Ambiental Rural (CAR) é obrigatório para governança."
    E a resposta deve respeitar o contrato JSON Schema "auditoria-erro-schema.json"

  @smoke @auditoriaConsulta
  Cenário: Consultar certificado de auditoria ESG previamente aprovado por ID
    Dado que existe uma auditoria aprovada com o ID "AUD-2026-99812"
    Quando eu realizar a consulta dessa auditoria pelo ID
    Então o status code da resposta deve ser 200
    E o corpo da resposta deve conter o id de auditoria "AUD-2026-99812"
    E o corpo da resposta deve conter o status de conformidade "APROVADO_EXPORTACAO" e selo verde "true"
    E a resposta deve respeitar o contrato JSON Schema "auditoria-sucesso-schema.json"

  @negativo @social
  Cenário: Reprovar auditoria de lote oriundo de propriedade sem regularidade trabalhista
    Dado que possuo os dados de um lote agrícola para auditoria:
      | campo                   | valor                                               |
      | codigoCar               | MG-3106200-11122233344455566677788899900011         |
      | cultura                 | CAFE                                                |
      | pesoToneladas           | 120.0                                               |
      | focosQueimadaRecentes   | 0                                                   |
      | regularidadeTrabalhista | false                                               |
    Quando eu enviar a requisição de auditoria ESG do lote
    Então o status code da resposta deve ser 422
    E o corpo da resposta deve retornar o erro "VIOLACAO_SOCIAL" e mensagem "Lote reprovado: Propriedade sem regularidade trabalhista comprovada."
    E a resposta deve respeitar o contrato JSON Schema "auditoria-erro-schema.json"

  @negativo @auditoriaConsulta
  Cenário: Consultar auditoria ESG com ID inexistente
    Dado que existe uma auditoria aprovada com o ID "AUD-0000-00000"
    Quando eu realizar a consulta dessa auditoria pelo ID
    Então o status code da resposta deve ser 404
    E o corpo da resposta deve retornar o erro "AUDITORIA_NAO_ENCONTRADA" e mensagem "Nenhuma auditoria encontrada para o ID informado."
    E a resposta deve respeitar o contrato JSON Schema "auditoria-erro-schema.json"
