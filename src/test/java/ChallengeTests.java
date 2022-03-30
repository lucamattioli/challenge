import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.openqa.selenium.By;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import static org.junit.Assert.assertTrue;

/**
 * Created by A147882 on 28/03/2022.
 */
public class ChallengeTests {

    @Test
    public void test_one() throws Exception {

        Core.startBrowser();

        Core.goToTheFork();

        Core.doLogin();

        Core.goToPersonalInfo();

        Thread.sleep(5000);
        assertTrue(Core.readDataProperty("first.name").equalsIgnoreCase(Core.getDriver().findElement(By.xpath("//input[@name='firstName']")).getAttribute("value")));
        assertTrue(Core.readDataProperty("last.name").equalsIgnoreCase(Core.getDriver().findElement(By.xpath("//input[@name='lastName']")).getAttribute("value")));
        assertTrue(Core.readDataProperty("phone").equalsIgnoreCase(Core.getDriver().findElement(By.xpath("//input[@data-testid='phone-input-number']")).getAttribute("value")));

        Core.teardownBrowser();
    }

    @Test
    public void test_two() {

        try {
            File fileResults = new File("results.txt");
            if(!fileResults.exists()) {
                fileResults.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(fileResults.getName(),true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWritter);

            String stringUrl = "https://pokeapi.co/api/v2/pokemon?limit=30";


            String res = Core.urlCall(stringUrl);

            JsonArray pokemonList = new JsonParser().parse(res).getAsJsonObject().getAsJsonArray(("results"));


            for (JsonElement pokemon : pokemonList) {
                JsonArray pokemonTypes = new JsonParser().parse(Core.urlCall(((JsonObject) pokemon).get("url").getAsString())).getAsJsonObject().getAsJsonArray("types");
                for (JsonElement type : pokemonTypes) {
                    if ("normal".equalsIgnoreCase(((JsonObject) type).getAsJsonObject("type").get("name").getAsString())) {
                        String pokemonName = ((JsonObject) pokemon).get("name").getAsString();
//                        System.out.println(pokemonName);
                        bufferedWriter.write(pokemonName + "\n");
                        break;
                    }
                }
            }

            bufferedWriter.close();

        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

}
