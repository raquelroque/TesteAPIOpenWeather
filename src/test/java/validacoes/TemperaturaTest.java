package validacoes;

import configuracoes.Configuracoes;
import factory.LocalizacaoDataFactory;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;
import pojo.Local;

import static io.restassured.RestAssured.* ;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class TemperaturaTest {

    private String token;
    private String tokenInvalido= "xpto";
    Local local = LocalizacaoDataFactory.criarLocalizacao();

    @Before
    public void SetUp(){
        Configuracoes configuracoes = ConfigFactory.create(Configuracoes.class);
        baseURI = configuracoes.baseURI();
        basePath = configuracoes.basePath();
        token = configuracoes.token();
    }

    @Test
    public void testRetornaTemperaturaPorCidade(){

       given()
            .param("q", local.getCidade())
            .param("appid",token)
            .contentType(ContentType.JSON)
       .when()
            .get("/weather")
       .then()
            .assertThat()
                .statusCode(200)
                .body("name", equalToIgnoringCase(local.getCidade()))
                .body("main.temp", notNullValue());
    }

    @Test
    public void testRetornaTemperaturaPorCoordenadas (){
        given()
            .param("lat", local.getLatitude())
            .param("lon",local.getLongitude())
            .param("appid",token)
            .contentType(ContentType.JSON)
        .when()
            .get("/weather")
        .then()
            .assertThat()
                .statusCode(200)
                .body("name", equalToIgnoringCase(local.getCidade()))
                .body("main.temp", notNullValue())
                .body(matchesJsonSchemaInClasspath("schemas/getTemperaturaCoordenadas.json"));

    }


    @Test
    public void testRetornaTemperaturaPorCodigoPostal (){
        given()
            .param("zip", local.getCep() +","+local.getPais())
            .param("appid",token)
            .contentType(ContentType.JSON)
        .when()
            .get("/weather")
        .then()
            .assertThat()
                .statusCode(200)
                .body("name", equalToIgnoringCase(local.getCidade()))
                .body("main.temp", notNullValue());

    }

    @Test
    public void testExcecaoChamadaKeyInvalida(){

        given()
            .param("q", local.getCidade())
            .param("appid",tokenInvalido)
            .contentType(ContentType.JSON)
        .when()
            .get("/weather")
        .then()
            .assertThat()
                .statusCode(401)
                .body("message", containsString("Invalid API key."));
    }


    @Test
    public void testExcecaoChamadaInvalidaCEP(){
        given()
            .param("zip", local.getCep())
            .param("appid",token)
            .contentType(ContentType.JSON)
        .when()
            .get("/weather")
        .then()
            .assertThat()
                .statusCode(404)
                .body("message", equalToIgnoringCase("city not found"));
    }

}
