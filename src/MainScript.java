import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import states.*;
import utils.*;

@ScriptManifest(name = "Tutorial Island", author = "BotScriptsOSRS", version = 1.0, info = "", logo = "")
public class MainScript extends Script {
    private ScriptState currentState;
    private CharacterCreation characterCreation;
    private GielinorGuideInteraction gielinorGuideInteraction;
    private SurvivalExpertInteraction survivalExpertInteraction;
    private MasterChefInteraction masterChefInteraction;
    private QuestGuideInteraction questGuideInteraction;
    private MiningInstructorInteraction miningInstructorInteraction;
    private CombatInstructorInteraction combatInstructorInteraction;
    private AccountGuideInteraction accountGuideInteraction;
    private BrotherBraceInteraction brotherBraceInteraction;
    private MagicInstructorInteraction magicInstructorInteraction;

    @Override
    public void onStart() {
        WidgetHandler widgetHandler = new WidgetHandler(this);
        ObjectHandler objectHandler = new ObjectHandler(this);
        DialogueHelper dialogueHelper = new DialogueHelper(this, widgetHandler);
        NPCHandler npcHandler = new NPCHandler(this);
        InventoryHandler inventoryHandler = new InventoryHandler(this);

        characterCreation = new CharacterCreation(this, widgetHandler);
        gielinorGuideInteraction = new GielinorGuideInteraction(this, widgetHandler, objectHandler, dialogueHelper);
        survivalExpertInteraction = new SurvivalExpertInteraction(this, widgetHandler, objectHandler, dialogueHelper, npcHandler, inventoryHandler);
        masterChefInteraction = new MasterChefInteraction(this, objectHandler, dialogueHelper, inventoryHandler);
        questGuideInteraction = new QuestGuideInteraction(this, widgetHandler, dialogueHelper);
        miningInstructorInteraction = new MiningInstructorInteraction(this, dialogueHelper, objectHandler, widgetHandler);
        combatInstructorInteraction = new CombatInstructorInteraction(this, widgetHandler, dialogueHelper, objectHandler);
        accountGuideInteraction = new AccountGuideInteraction(this, widgetHandler, dialogueHelper, objectHandler);
        brotherBraceInteraction = new BrotherBraceInteraction(this, widgetHandler, dialogueHelper);
        magicInstructorInteraction = new MagicInstructorInteraction(this, widgetHandler, dialogueHelper);

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
                    currentState = ScriptState.SURVIVAL_EXPERT;
                }
                break;
            case SURVIVAL_EXPERT:
                log("Survival Expert State");
                if (survivalExpertInteraction.performInteraction()) {
                    currentState = ScriptState.MASTER_CHEF;
                }
                break;
            case MASTER_CHEF:
                log("Master Chef State");
                if (masterChefInteraction.performInteraction()) {
                    currentState = ScriptState.QUEST_GUIDE;
                }
                break;
            case QUEST_GUIDE:
                log("Quest Guide State");
                if (questGuideInteraction.performInteraction()) {
                    currentState = ScriptState.MINING_INSTRUCTOR;
                }
                break;
            case MINING_INSTRUCTOR:
                log("Mining Instructor State");
                if (miningInstructorInteraction.performInteraction()) {
                    currentState = ScriptState.COMBAT_INSTRUCTOR;
                }
                break;
            case COMBAT_INSTRUCTOR:
                log("Combat Instructor State");
                if (combatInstructorInteraction.performInteraction()) {
                    currentState = ScriptState.ACCOUNT_GUIDE;
                }
                break;
            case ACCOUNT_GUIDE:
                log("Account Guide State");
                if (accountGuideInteraction.performInteraction()) {
                    currentState = ScriptState.BROTHER_BRACE;
                }
                break;
            case BROTHER_BRACE:
                log("Brother Brace State");
                if (brotherBraceInteraction.performInteraction()) {
                    currentState = ScriptState.MAGIC_INSTRUCTOR;
                }
                break;
            case MAGIC_INSTRUCTOR:
                log("Magic Instructor State");
                if (magicInstructorInteraction .performInteraction()) {
                    getLogoutTab().logOut();
                    stop();
                }
                break;
        }
        return random(200, 300);
    }

    @Override
    public void onExit() {
        stop();
    }
}

