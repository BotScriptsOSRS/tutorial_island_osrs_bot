package states;

import org.osbot.rs07.api.Settings;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import utils.Sleep;
import utils.WidgetHandler;

import java.awt.event.KeyEvent;
import java.util.Random;

public class GielinorGuideInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final Position outsideDoor = new Position(3098, 3107, 0);
    private final Random random = new Random();

    private final Area houseOfGielinorGuide = new Area(3087, 3100, 3097, 3112);

    public GielinorGuideInteraction(Script script) {
        this.script = script;
        this.widgetHandler = new WidgetHandler(script);
    }

    public boolean performInteraction() throws InterruptedException {
        if (!houseOfGielinorGuide.contains(script.myPlayer().getPosition())) {
            script.log("Skipping state, already completed");
            return true;
        } else if (!widgetHandler.isWidgetVisible("Settings", false) && talkToGielinorGuide()){
            widgetHandler.waitForWidget("Settings", false);
        } else if (!widgetHandler.isWidgetVisible("Unmute", false) && configureGameSettings()){
            widgetHandler.waitForWidgetToDisappear("Mute", false);
        } else {
            return talkToGielinorGuide() && interactWithClosestDoor();
        }
        return false;
    }

    private boolean talkToGielinorGuide() throws InterruptedException {
        script.log("Talking to the Gielinor Guide");
        NPC gielinorGuide = script.getNpcs().closest("Gielinor Guide");
        if (gielinorGuide != null && gielinorGuide.interact("Talk-to")) {
            waitForDialogue(script);
            completeDialogue(script);
            return true;
        }
        return false;
    }

    private void waitForDialogue(Script script) {
        script.log("Waiting for the dialogue to open");
        Sleep.sleepUntil(() -> widgetHandler.isWidgetVisible("Click here to continue", true), 5000);
    }

    private void completeDialogue(Script script) throws InterruptedException {
        script.log("Completing the dialogue with the Gielinor Guide");

        boolean selectedExperiencedPlayer = false;
        while (widgetHandler.isWidgetVisible("I am an experienced player.", true) ||
                widgetHandler.isWidgetVisible("Click here to continue", true)) {

            if (!selectedExperiencedPlayer && widgetHandler.isWidgetVisible("I am an experienced player.", true)) {
                script.log("Selecting 'I am an experienced player.'");
                widgetHandler.clickWidgetWithMessage("I am an experienced player.");
                selectedExperiencedPlayer = true;
            } else if (widgetHandler.isWidgetVisible("Click here to continue", true)) {
                script.log("Clicking 'Click here to continue'");
                widgetHandler.clickWidgetWithMessage("Click here to continue");
            }

            MethodProvider.sleep(random.nextInt(501) + 500);
        }
    }

    private boolean configureGameSettings() throws InterruptedException {
        script.log("Configure game settings");
        if (!script.getSettings().areRoofsEnabled() && openAndVerifySettings()){
            enableHideRoofs();
            enableShiftClickDrop();
            closeSettings();
            adjustAudioSettings();
            return true;
        }
        return false;
    }

    private boolean openAndVerifySettings() throws InterruptedException {
        while (!widgetHandler.isWidgetVisible("All Settings", false)) {
            script.log("Trying to open settings");
            if (widgetHandler.isWidgetVisible("Settings", false)) {
                script.log("Opening settings tab");
                widgetHandler.clickWidget("Settings");
                return Sleep.sleepUntil(() -> widgetHandler.isWidgetVisible("All Settings", false), 5000);
            } else {
                MethodProvider.sleep(random.nextInt(501) + 1000);
            }
        }
        return widgetHandler.isWidgetVisible("All Settings", false);
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

    private void adjustAudioSettings() throws InterruptedException {
        script.log("Opening audio settings");
        widgetHandler.clickWidget("Audio");
        widgetHandler.waitForWidget("Mute", false);
        script.log("Muting all audio");
        widgetHandler.clickAllWidgetsWithAction("Mute");
    }

    private boolean interactWithClosestDoor() {
        RS2Object door = script.getObjects().closest(obj -> obj.getName().equals("Door"));
        script.log("Trying to find the door");
        if (door != null && door.interact("Open")) {
            script.log("Opening door");
            Sleep.sleepUntil(() -> script.myPlayer().getPosition().equals(outsideDoor), 5000); // Wait until moved away from the door
            return true;
        }
        return false;
    }

}
