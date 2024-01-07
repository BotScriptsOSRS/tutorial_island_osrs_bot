package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import utils.*;

import java.awt.event.KeyEvent;

public class AccountGuideInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final DialogueHelper dialogueHelper;
    private final ObjectHandler objectHandler;

    private final Area combatInstructorArea = new Area(3117, 3118, 3131, 3126);
    private final Position finalPosition = new Position(3125, 3124, 0);
    private final Area finalArea = new Area(3123, 3106, 3126, 3108);

    private boolean openedAndUsedBank = false;
    private boolean usedPollBooth = false;
    private boolean walkedToPosition = false;
    private boolean talkedToAccountGuideFirstTime = false;
    private boolean openedAccountManagementTab = false;
    private boolean talkedToAccountGuideSecondTime = false;
    private boolean walkedToFinalArea = false;

    public AccountGuideInteraction(Script script, WidgetHandler widgetHandler, DialogueHelper dialogueHelper, ObjectHandler objectHandler) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.dialogueHelper = dialogueHelper;
        this.objectHandler = objectHandler;
    }

    public boolean performInteraction() {
        if (!combatInstructorArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!openedAndUsedBank) {
            openedAndUsedBank = openAndUseBank();
        }
        if (openedAndUsedBank && !usedPollBooth) {
            usedPollBooth = usePollBooth();
        }
        if (usedPollBooth && !walkedToPosition) {
            walkedToPosition = webWalkToPosition(finalPosition);
        }
        if (walkedToPosition && !talkedToAccountGuideFirstTime) {
            talkedToAccountGuideFirstTime = talkToAccountGuide();
        }
        if (talkedToAccountGuideFirstTime && !openedAccountManagementTab) {
            openedAccountManagementTab = openAccountManagementTab();
        }
        if (openedAccountManagementTab && !talkedToAccountGuideSecondTime) {
            talkedToAccountGuideSecondTime = talkToAccountGuide();
        }
        if (talkedToAccountGuideSecondTime) {
            walkedToFinalArea = webWalkToArea(finalArea);
        }

        return walkedToFinalArea;
    }

    private boolean openAndUseBank() {
        if (objectHandler.interactWithClosestObject("Bank booth", "Use", () -> widgetHandler.isWidgetVisible("Not now", false ))) {
            if (widgetHandler.isWidgetVisible("Not now", false )){
                widgetHandler.clickWidget("Not now");
                widgetHandler.waitForWidgetToDisappear("Not now", false);
            }
            script.getKeyboard().pressKey((char) KeyEvent.VK_ESCAPE);
            return script.getBank().isOpen() && !widgetHandler.isWidgetVisible("Not now", false );
        }
        return false;
    }

    private boolean usePollBooth() {
        if (objectHandler.interactWithClosestObject("Poll booth", "Use", () -> widgetHandler.isWidgetVisible("Click here to continue", true))) {
            dialogueHelper.continueUntilDialogueEnds();
            script.getKeyboard().pressKey((char) KeyEvent.VK_ESCAPE);
            return true; // Assuming successful interaction with the poll booth
        }
        return false;
    }

    private boolean webWalkToPosition(Position position) {
        return script.getWalking().webWalk(position);
    }

    private boolean talkToAccountGuide() {
        return dialogueHelper.continueThroughDialogue("Account Guide");
    }

    private boolean openAccountManagementTab() {
        return widgetHandler.openTab("Account Management");
    }

    private boolean webWalkToArea(Area area) {
        return script.getWalking().webWalk(area);
    }
}
