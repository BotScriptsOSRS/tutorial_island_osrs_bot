import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import states.CharacterCreation;
import states.GielinorGuideInteraction;
import states.ScriptState;

@ScriptManifest(name = "Tutorial Island", author = "BotScriptsOSRS", version = 1.0, info = "", logo = "")
public class MainScript extends Script {
    private ScriptState currentState;
    private CharacterCreation characterCreation;
    private GielinorGuideInteraction gielinorGuideInteraction;

    @Override
    public void onStart() {
        characterCreation = new CharacterCreation(this);
        gielinorGuideInteraction = new GielinorGuideInteraction(this);

        currentState = ScriptState.CHARACTER_CREATION;
    }

    @Override
    public int onLoop() throws InterruptedException {
        switch (currentState) {
            case CHARACTER_CREATION:
                log("Create character State");
                if (characterCreation.performCreation()) {
                    currentState = ScriptState.GIELINOR_GUIDE;
                }
                break;
            case GIELINOR_GUIDE:
                log("Gielinor Guide State");
                if (gielinorGuideInteraction.performInteraction()) {
                    currentState = ScriptState.NEXT_STAGE;
                }
                break;
            // Add other cases for additional states
        }
        return random(200, 300);
    }

    @Override
    public void onExit() {
        stop();
    }
}

