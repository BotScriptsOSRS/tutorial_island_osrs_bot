package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import utils.*;

public class MiningInstructorInteraction {
    private final Script script;
    private final DialogueHelper dialogueHelper;
    private final ObjectHandler objectHandler;
    private final WidgetHandler widgetHandler;

    private final Area miningInstructorArea = new Area(3072, 9494, 3094, 9526);
    private final Position nextPosition = new Position(3104, 9505, 0);

    private boolean talkedToMiningInstructorFirstTime = false;
    private boolean minedTin = false;
    private boolean minedCopper = false;
    private boolean talkedToMiningInstructorSecondTime = false;
    private boolean smeltedBar = false;
    private boolean smithedDagger = false;
    private boolean walkedToNextPosition = false;

    public MiningInstructorInteraction(Script script, DialogueHelper dialogueHelper, ObjectHandler objectHandler, WidgetHandler widgetHandler) {
        this.script = script;
        this.dialogueHelper = dialogueHelper;
        this.objectHandler = objectHandler;
        this.widgetHandler = widgetHandler;
    }

    public boolean performInteraction() {
        if (!miningInstructorArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToMiningInstructorFirstTime) {
            talkedToMiningInstructorFirstTime = talkToMiningInstructor();
        }
        if (talkedToMiningInstructorFirstTime && !minedTin) {
            minedTin = mineOre("Tin");
        }
        if (minedTin && !minedCopper) {
            minedCopper = mineOre("Copper");
        }
        if (minedCopper && !smeltedBar) {
            smeltedBar = smeltBar();
        }
        if (smeltedBar && !talkedToMiningInstructorSecondTime) {
            talkedToMiningInstructorSecondTime = talkToMiningInstructor();
        }
        if (talkedToMiningInstructorSecondTime && !smithedDagger){
            smithedDagger = smithDagger();
        }
        if (smithedDagger && !walkedToNextPosition) {
            walkedToNextPosition = webWalkToNextPosition();
        }

        return walkedToNextPosition;
    }

    private boolean talkToMiningInstructor() {
        script.log("Talking to Mining Instructor");
        return dialogueHelper.continueThroughDialogue("Mining Instructor"); // Replace null with actual dialogue options if needed
    }

    private boolean mineOre(String oreType) {
        script.log("Mining " + oreType);
        return objectHandler.interactWithClosestObject(oreType + " rocks", "Mine", () -> script.getInventory().contains(oreType + " ore"));
    }
    private boolean smeltBar(){
        script.log("Smelting bar");
        return objectHandler.interactWithClosestObject("Furnace", "Use", () -> script.getInventory().contains("Bronze bar"));
    }

    private boolean smithDagger() {
        script.log("Smithing Bronze dagger");
        if (objectHandler.interactWithClosestObject("Anvil", "Smith", () -> widgetHandler.isWidgetVisible("Smith", false))) {
            widgetHandler.waitForWidget("Smith", false);
            widgetHandler.clickWidgetWithSpellName("Smith", "<col=ff9040>Bronze dagger</col>");
            return Sleep.until(() -> script.getInventory().contains("Bronze dagger"));
        }
        return false;
    }

    private boolean webWalkToNextPosition() {
        script.log("Webwalking to next position");
        return script.getWalking().webWalk(nextPosition);
    }
}
