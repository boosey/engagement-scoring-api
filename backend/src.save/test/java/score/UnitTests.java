package score;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import java.net.URI;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class UnitTests {

  final String templateName1 = "Template1";
  final String sectionName1 = "Section1";
  final String templateName2 = "Template2";
  final String sectionName2 = "Section2";
  final String templateName3 = "Template3";
  final String sectionName3 = "Section3";
  final String response1 = "Response 1";
  final String response2 = "Response 2";
  final String response3 = "Response 3";

  final String template = "template";
  final String section = "section";
  final String possibleResponse = "possible-response";
  final String apiBaseUri = "/api";
  final String templateUri = apiBaseUri + "/" + template;

  String exampleTemplate1Json() {
    return new JSONObject().put("name", templateName1).toString();
  }

  String exampleSection1Json() {
    return new JSONObject().put("name", sectionName1).toString();
  }

  String exampleTemplate2Json() {
    return new JSONObject().put("name", templateName2).toString();
  }

  String exampleSection2Json() {
    return new JSONObject().put("name", sectionName2).put("weight", 1).toString();
  }

  String exampleTemplate3Json() {
    return new JSONObject().put("name", templateName3).toString();
  }

  String exampleSection3Json() {
    return new JSONObject().put("name", sectionName3).put("weight", 1).toString();
  }

  String examplePossibleResponse1Json() {
    return new JSONObject().put("text", response2).put("value", 100).toString();
  }

  String examplePossibleResponse2Json() {
    return new JSONObject().put("text", response2).put("value", 100).toString();
  }

  String examplePossibleResponse3Json() {
    return new JSONObject().put("text", response3).put("value", 100).toString();
  }

  @Test
  public void testEndpoints() {
    given().when().get(templateUri).then().statusCode(200);
  }

  @Test
  void addTemplates() {
    String template1URI = given().contentType(ContentType.JSON).body(exampleTemplate1Json()).when().post(templateUri)
        .then().statusCode(201).extract().header("location");

    given().contentType(ContentType.JSON).body(exampleTemplate2Json()).when().post(templateUri).then().statusCode(201)
        .extract().response().asString();

    given().contentType(ContentType.JSON).body(exampleTemplate3Json()).when().post(templateUri).then().statusCode(201)
        .extract().response().asString();

    Log.info(apiBaseUri + URI.create(template1URI).getPath() + "/" + section);

    String section1URI = given().contentType(ContentType.JSON).body(exampleSection1Json()).when()
        .post(apiBaseUri + URI.create(template1URI).getPath() + "/" + section).then().statusCode(201).extract()
        .header("location");

    String section1PossibleAnswersURI = apiBaseUri + URI.create(section1URI).getPath() + "/" + possibleResponse;
    Log.info(section1PossibleAnswersURI);

    given().contentType(ContentType.JSON).body(examplePossibleResponse1Json()).when().post(section1PossibleAnswersURI)
        .then().statusCode(201).extract().response().asString();

    given().contentType(ContentType.JSON).body(examplePossibleResponse2Json()).when().post(section1PossibleAnswersURI)
        .then().statusCode(201).extract().response().asString();

    given().contentType(ContentType.JSON).body(examplePossibleResponse3Json()).when().post(section1PossibleAnswersURI)
        .then().statusCode(201).extract().response().asString();

  }

}
