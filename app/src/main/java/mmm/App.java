package mmm;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.module.jsv.JsonSchemaValidator;

import org.json.JSONObject;
import com.google.gson.Gson;


public class App {

    public JSONObject getSpellUnirestJson(String text) {
        String url = "http://speller.yandex.net/services/spellservice.json/checkText";
        // HttpResponse<JsonNode> response = null;
        HttpResponse<JsonNode> response = null;

        try {
            // Не получается использовать asObject(DataWrap) возможно из за ответа в []
            response = Unirest.post(url)
                .header("Accept", "application/json")
                .field("text", text)
                .field("lang", "ru")
                .field("options", "0")
                .field("format", "plain")
                // .asObject(JsonNode.class);???
                .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        
        // Почему то не рабочий вариант, возможно из за возвращаемого массива, проверить на тестовых данных
        // https://stackoverflow.com/questions/23630681/how-to-parse-json-results-from-unirest-call
 
        // Вариант1 возвращаем JSONObject от java и работаем c ним как со словарем?
        
        // Не обрабатывается возврат нескольких массивов
        JSONObject retJObj = response.getBody().getArray().getJSONObject(0);
        retJObj.put("status", response.getStatus());
        return retJObj;
        
    }
    
    
    public DataWrap getSpellUnirestGsonToDW(String text) {
        // String url = "http://192.168.122.181:8000/services/spellservice.json/checkText";
        String url = "https://speller.yandex.net/services/spellservice.json/checkText";
        HttpResponse<JsonNode> response = null;
        Gson gson = new Gson();

        try {
            response = Unirest.post(url)
                .header("Accept", "application/json")
                .field("text", text)
                .field("lang", "ru")
                .field("options", "0")
                .field("format", "plain")
                // .asObject(JsonNode.class);???
                .asJson();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        
        // Вариант2 возвращаем объект DataWrap и работаем с ним как с объектом
        // Что будет если не будет ответа?
        String respObj = response.getBody().getArray().get(0).toString();
        DataWrap data = gson.fromJson(respObj, DataWrap.class);
        data.status = response.getStatus();
        return data;
    }

    public DataWrap[] getSpellRestAssured(String text) {
        // RestAssured.baseURI = "http://192.168.122.181:8000";
        RestAssured.baseURI = "http://speller.yandex.net";
        RequestSpecification request = RestAssured.given();
            request.header("Accept", "application/json");
            request.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            request.param("text", text).param("lang", "ru").param("options", "0").param("format", "plain");
            DataWrap[] respObj = request.post("/services/spellservice.json/checkText").as(DataWrap[].class);
            // Что то не понятно как вылавливать код ответа если сразу в "как" объект
            // только через два запроса подряд?
            
            // зато библиотека умеет проверку схемы... 
            // хотя наверное проще сделать её на каком нибудь внешнем валидаторе?
            // и надо вынести в отдельный запрос
            // понятия не имею как составлять схему\ сделал в онлайн генераторе по ответу сервера
            request.post("/services/spellservice.json/checkText")
                .then()
                .assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema.json"));
            return respObj;
    }
}
