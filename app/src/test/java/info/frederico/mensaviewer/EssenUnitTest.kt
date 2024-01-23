package info.frederico.mensaviewer

import com.beust.klaxon.Klaxon
import info.frederico.mensaviewer.helper.Essen
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class EssenUnitTest {
    @Test
    fun parseEssenListTest(){
        val testJson = "[\n" +
                "    {\n" +
                "        \"date\": \"2019-02-08\",\n" +
                "        \"dish\": \"Vegane Carbonara mit Tofuwürfel, Conchiglie\",\n" +
                "        \"vegetarian\": false,\n" +
                "        \"vegan\": true,\n" +
                "        \"price\": \"2.20\",\n" +
                "        \"price_staff\": \"3.50\",\n" +
                "        \"canteen\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"date\": \"2019-02-08\",\n" +
                "        \"dish\": \"Krakauer Bratwurst, Erbsen-Karotten Gemüse, Zwiebelsoße\",\n" +
                "        \"vegetarian\": false,\n" +
                "        \"vegan\": false,\n" +
                "        \"price\": \"2.20\",\n" +
                "        \"price_staff\": \"3.35\",\n" +
                "        \"canteen\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"date\": \"2019-02-08\",\n" +
                "        \"dish\": \"Gebratenes Kabeljaufilet, Orangenhollandaise\",\n" +
                "        \"vegetarian\": false,\n" +
                "        \"vegan\": false,\n" +
                "        \"price\": \"3.95\",\n" +
                "        \"price_staff\": \"4.65\",\n" +
                "        \"canteen\": 1\n" +
                "    },\n" +
                "    {\n" +
                "        \"date\": \"2019-02-08\",\n" +
                "        \"dish\": \"Wir kochen was Sie lieben..., Hähnchenkeule, Reis, Salat mit Balsamico Dressing, Geflügelrahmsoße\",\n" +
                "        \"vegetarian\": false,\n" +
                "        \"vegan\": false,\n" +
                "        \"price\": \"2.90\",\n" +
                "        \"price_staff\": \"4.20\",\n" +
                "        \"canteen\": 1\n" +
                "    }\n" +
                "]"
        val testList = Klaxon().parseArray<Essen>(testJson)
        val expectedList: List<Essen> = listOf(Essen("Vegane Carbonara mit Tofuwürfel, Conchiglie", "2.20", "3.50", "2019-02-08", false, true),
            Essen("Krakauer Bratwurst, Erbsen-Karotten Gemüse, Zwiebelsoße", "2.20", "3.35", "2019-02-08", false, false),
                Essen("Gebratenes Kabeljaufilet, Orangenhollandaise", "3.95", "4.65", "2019-02-08", false, false),
                Essen("Wir kochen was Sie lieben..., Hähnchenkeule, Reis, Salat mit Balsamico Dressing, Geflügelrahmsoße", "2.90", "4.20", "2019-02-08", false, false))
        print(testList)
        assertEquals(expectedList, testList)
    }
}
