package br.com.agrotrace.steps;

import br.com.agrotrace.services.AuditoriaEsgService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class AuditoriaEsgSteps {

    private final AuditoriaEsgService auditoriaService = new AuditoriaEsgService();
    private String idAuditoriaConsulta;

    @Dado("que possuo os dados de um lote agrícola para auditoria:")
    public void quePossuoOsDadosDeUmLoteAgricolaParaAuditoria(DataTable dataTable) {
        List<Map<String, String>> linhas = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> linha : linhas) {
            auditoriaService.configurarCampoLote(linha.get("campo"), linha.get("valor"));
        }
    }

    @Quando("eu enviar a requisição de auditoria ESG do lote")
    public void euEnviarARequisicaoDeAuditoriaEsgDoLote() {
        auditoriaService.enviarRequisicaoPostAuditoria();
    }

    @Então("o status code da resposta deve ser {int}")
    public void oStatusCodeDaRespostaDeveSer(int statusCodeEsperado) {
        Assert.assertEquals("Status Code HTTP incorreto!",
                statusCodeEsperado, auditoriaService.response.statusCode());
    }

    @E("o corpo da resposta deve conter o status de conformidade {string} e selo verde {string}")
    public void oCorpoDaRespostaDeveConterOStatusDeConformidadeESeloVerde(String statusEsperado, String seloVerdeEsperado) {
        String statusAtual = auditoriaService.response.jsonPath().getString("statusConformidade");
        Boolean seloAtual = auditoriaService.response.jsonPath().getBoolean("seloVerde");

        Assert.assertEquals(statusEsperado, statusAtual);
        Assert.assertEquals(Boolean.parseBoolean(seloVerdeEsperado), seloAtual);
    }

    @E("a resposta deve respeitar o contrato JSON Schema {string}")
    public void aRespostaDeveRespeitarOContratoJsonSchema(String schemaFileName) {
        auditoriaService.validarContratoJsonSchema(schemaFileName);
    }

    @E("o corpo da resposta deve retornar o erro {string} e mensagem {string}")
    public void oCorpoDaRespostaDeveRetornarOErroEMensagem(String erroEsperado, String mensagemEsperada) {
        String erroAtual = auditoriaService.response.jsonPath().getString("erro");
        String mensagemAtual = auditoriaService.response.jsonPath().getString("mensagem");

        Assert.assertEquals(erroEsperado, erroAtual);
        Assert.assertEquals(mensagemEsperada, mensagemAtual);
    }

    @Dado("que existe uma auditoria aprovada com o ID {string}")
    public void queExisteUmaAuditoriaAprovadaComOId(String idAuditoria) {
        this.idAuditoriaConsulta = idAuditoria;
    }

    @Quando("eu realizar a consulta dessa auditoria pelo ID")
    public void euRealizarAConsultaDessaAuditoriaPeloId() {
        auditoriaService.consultarAuditoriaPorId(this.idAuditoriaConsulta);
    }
}