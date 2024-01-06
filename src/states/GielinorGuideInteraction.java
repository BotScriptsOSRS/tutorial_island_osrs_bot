package states;

import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import utils.DialogueHelper;
import utils.ObjectHandler;
import utils.WidgetHandler;

import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;

public class GielinorGuideInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final ObjectHandler objectHandler;
    private final Position outsideDoor = new Position(3098, 3107, 0);
    private final Area houseOfGielinorGuide = new Area(3087, 3100, 3097, 3112);
    private final DialogueHelper dialogueHelper;
    private boolean talkedToGielinorGuideFirstTime = false;
    private boolean gameSettingsConfigured = false;
    private boolean talkedToGielinorGuideSecondTime = false;
    private boolean leftHouse = false;

    public GielinorGuideInteraction(Script script, WidgetHandler widgetHandler, ObjectHandler objectHandler, DialogueHelper dialogueHelper) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.objectHandler = objectHandler;
        this.dialogueHelper = dialogueHelper;
    }

    public boolean performInteraction() {
        if (!houseOfGielinorGuide.contains(script.myPlayer().getPosition())) {
            script.log("Skipping state, already completed");
            return true;
        }

        if (!talkedToGielinorGuideFirstTime) {
            talkedToGielinorGuideFirstTime = talkToGielinorGuideFirstTime();
        }
        if (talkedToGielinorGuideFirstTime && !gameSettingsConfigured) {
            gameSettingsConfigured = configureGameSettings();
        }
        if (gameSettingsConfigured && !talkedToGielinorGuideSecondTime) {
            talkedToGielinorGuideSecondTime = talkToGielinorGuideSecondTime();
        }
        if (talkedToGielinorGuideSecondTime && !leftHouse) {
            leftHouse = leaveHouse();
        }

        return leftHouse;
    }

    private boolean talkToGielinorGuideFirstTime() {
        List<String> dialogueOptions = Collections.singletonList("I am an experienced player.");
        return dialogueHelper.talkToNPC("Gielinor Guide", dialogueOptions);
    }

    private boolean talkToGielinorGuideSecondTime() {
        return dialogueHelper.continueThroughDialogue("Gielinor Guide");
    }

    private boolean configureGameSettings() {
        script.log("Configure game settings");
        if (!script.getSettings().areRoofsEnabled() && widgetHandler.openTab("Settings")){
            enableHideRoofs();
            enableShiftClickDrop();
            closeSettings();
            adjustAudioSettings();
            return true;
        }
        return false;
    }

    private void enableHideRoofs() {
        script.log("Enabling hide roofs");
        if (!script.getSettings().areRoofsEnabled()){
            script.getSettings().setSetting(Settings.AllSettingsTab.DISPLAY, "Hide roofs", true);
        }
    }

    private void enableShiftClickDrop() {
        script.log("Opening controls settings");
        if (!script.getSettings().isShiftDropActive()){
            script.getSettings().setSetting(Settings.AllSettingsTab.CONTROLS, "Shift click to drop items", true);
        }
    }

    private void closeSettings() {
        script.log("Closing settings");
        script.getKeyboard().typeKey((char) KeyEvent.VK_ESCAPE);
        widgetHandler.waitForWidgetToDisappear("Hide roofs", true);
    }

    private void adjustAudioSettings() {
        script.log("Opening audio settings");
        widgetHandler.clickWidget("Audio");
        widgetHandler.waitForWidget("Mute", false);
        script.log("Muting all audio");
        widgetHandler.clickAllWidgetsWithAction("Mute");
    }

    private boolean leaveHouse() {
        if (objectHandler.interactWithClosestObject("Door", "Open", () -> script.myPlayer().getPosition().equals(outsideDoor))) {
            script.log("Opening door");
            return true;
        }
        return false;
    }
}
