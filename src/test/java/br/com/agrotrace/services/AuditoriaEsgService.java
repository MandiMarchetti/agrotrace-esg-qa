package br.com.agrotrace.services;

import br.com.agrotrace.model.LoteAuditoriaModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

import java.io.InputStream;

import static io.restassured.RestAssured.given;

public class AuditoriaEsgService {

    private final LoteAuditoriaModel loteModel = new LoteAuditoriaModel();
    public final Gson gson = new GsonBuilder().serializeNulls().create();
    public Response response;
    private final String baseUrl = System.getProperty("api.base.url", "http://localhost:8080");
    private final String endpointAuditoria = "/api/v1/lotes/auditoria";

    public void configurarCampoLote(String campo, String valor) {
        String valorFormatado = (valor == null) ? "" : valor.trim();
        switch (campo) {
            case "codigoCar" -> loteModel.setCodigoCar(valorFormatado);
            case "cultura" -> loteModel.setCultura(valorFormatado);
            case "pesoToneladas" -> loteModel.setPesoToneladas(Double.parseDouble(valorFormatado));
            case "focosQueimadaRecentes" -> loteModel.setFocosQueimadaRecentes(Integer.parseInt(valorFormatado));
            case "regularidadeTrabalhista" -> loteModel.setRegularidadeTrabalhista(Boolean.parseBoolean(valorFormatado));
            default -> throw new IllegalArgumentException("Campo não mapeado no modelo ESG: " + campo);
        }
    }

    public void enviarRequisicaoPostAuditoria() {
        String payloadJson = gson.toJson(loteModel);

        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payloadJson)
                .log().all()
                .when()
                .post(endpointAuditoria)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public void consultarAuditoriaPorId(String idAuditoria) {
        response = given()
                .baseUri(baseUrl)
                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get(endpointAuditoria + "/" + idAuditoria)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public void validarContratoJsonSchema(String nomeArquivoSchema) {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/" + nomeArquivoSchema);

        if (schemaStream == null) {
            throw new RuntimeException("Arquivo de schema não encontrado em resources/schemas/" + nomeArquivoSchema);
        }

        response.then().assertThat().body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }
}