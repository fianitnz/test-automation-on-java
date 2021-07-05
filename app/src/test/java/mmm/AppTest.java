package mmm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.json.JSONObject;

class AppTest {

    String testWord = "Cабока";
    String testEqv = "Собака";

    @Test
    @DisplayName("Spell checker test")
    void testSpeller(){
        System.out.println("testSpell");
        App objGetSpell = new App();
        
        // Unirest вариант 1
        JSONObject retJObj = objGetSpell.getSpellUnirestJson(this.testWord);
        assertEquals(retJObj.get("status"), 200, "Код ответа http не 200");
        assertEquals(retJObj.get("word"), testWord, "Слово с ошибкой, в ответе отличается от отправленного");

        JSONArray retJArr = (JSONArray) retJObj.get("s");
        assertNotEquals(retJArr.get(0), testWord, "Исправленное слово равно слову с шибкой");
        assertEquals(retJArr.get(0), testEqv, "Исправленное слово не равно правильному слову");
        
        // Unirest вариант 2
        DataWrap retDWObj = objGetSpell.getSpellUnirestGsonToDW(this.testWord);
        assertEquals(retDWObj.status, 200, "Код ответа http не 200");
        assertEquals(retDWObj.word, testWord, "Слово с ошибкой, в ответе отличается от отправленного");

        assertNotEquals(retDWObj.s[0], testWord, "Исправленное слово равно слову с шибкой");
        assertEquals(retDWObj.s[0], testEqv, "Исправленное слово не равно правильному слову");

        // REST-Assured
        // без проверки кода ответа
        DataWrap[] retDWObjRA = objGetSpell.getSpellRestAssured(testWord);
        assertEquals(retDWObjRA[0].word, testWord, "Слово с ошибкой, в ответе отличается от отправленного");

        assertNotEquals(retDWObjRA[0].s[0], testWord, "Исправленное слово равно слову с шибкой");
        assertEquals(retDWObjRA[0].s[0], testEqv, "Исправленное слово не равно правильному слову");

        }
}
