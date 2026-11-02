# API AgroTrace ESG virtualizada (WireMock) - usada pelos testes automatizados na porta 8080
FROM wiremock/wiremock:3.3.1

# Mapeamentos (endpoints e regras ESG mockadas) embutidos na imagem
COPY wiremock/ /home/wiremock/

EXPOSE 8080

# Argumentos repassados ao WireMock pelo entrypoint da imagem oficial
CMD ["--global-response-templating", "--verbose"]
