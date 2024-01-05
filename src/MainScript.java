import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;


@ScriptManifest(
        name = "Tutorial Island",
        author = "BotScriptsOSRS",
        version = 1.0,
        info = "",
        logo = ""
)
public class MainScript extends Script {

    @Override
    public int onLoop() {

        return random(200, 300);
    }


}