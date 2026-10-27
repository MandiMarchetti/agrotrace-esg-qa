package br.com.agrotrace.runner;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.Socket;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = "br.com.agrotrace.steps",
        tags = "@regressivo",
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber.json"
        },
        monochrome = true,
        snippets = CucumberOptions.SnippetType.CAMELCASE
)
public class TestRunner {

    private static WireMockServer wireMockServer;

    @BeforeClass
    public static void iniciarApiCasoDockerNaoEstejaAtivo() {
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println(">>> API AgroTrace ESG detectada na porta 8080 (Container Docker ativo)!");
        } catch (IOException e) {
            System.out.println(">>> Subindo API AgroTrace ESG localmente via WireMock Standalone na porta 8080...");
            wireMockServer = new WireMockServer(
                    WireMockConfiguration.options()
                            .port(8080)
                            .usingFilesUnderDirectory("wiremock")
                            .globalTemplating(true)
            );
            wireMockServer.start();
            System.out.println(">>> API AgroTrace ESG iniciada com sucesso em http://localhost:8080 !");
        }
    }

    @AfterClass
    public static void encerrarApiLocal() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
            System.out.println(">>> API AgroTrace ESG encerrada com sucesso.");
        }
    }
}