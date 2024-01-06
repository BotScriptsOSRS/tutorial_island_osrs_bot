package utils;

import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;

import java.util.List;
import java.util.Queue;
import java.util.LinkedList;


public class DialogueHelper {
    private final Script script;
    private final WidgetHandler widgetHandler;


    public DialogueHelper(Script script, WidgetHandler widgetHandler) {
        this.script = script;
        this.widgetHandler = widgetHandler;
    }

    public boolean talkToNPC(String npcName, List<String> options) {
        script.log("Talking to " + npcName);
        NPC npc = script.getNpcs().closest(npcName);
        if (npc != null && npc.interact("Talk-to")) {
            waitForDialogue();
            completeDialogue(new LinkedList<>(options)); // Convert list to queue for ordered processing
            return true;
        }
        return false;
    }

    public boolean continueThroughDialogue(String npcName) {
        script.log("Continuing through dialogue with " + npcName);
        NPC npc = script.getNpcs().closest(npcName);
        if (npc != null && npc.interact("Talk-to")) {
            waitForDialogue();
            continueUntilDialogueEnds();
            return true;
        }
        return false;
    }

    private void continueUntilDialogueEnds() {
        script.log("Clicking through dialogue");
        while ( widgetHandler.isWidgetVisible("Click here to continue", true) &&
                widgetHandler.clickWidgetWithMessage("Click here to continue")) {
            script.log("Clicking 'Click here to continue'");
            Sleep.randomSleep(500, 1000);
        }
    }

    private void waitForDialogue() {
        script.log("Waiting for the dialogue to open");
        Sleep.until(() -> widgetHandler.isWidgetVisible("Click here to continue", true));
    }

    private void completeDialogue(Queue<String> options) {
        script.log("Completing the dialogue");

        while (isDialogueContinuing(options)) {
            handleDialogueOption(options);
            handleClickHereToContinue();
            Sleep.randomSleep(500, 1000);
        }
    }

    private boolean isDialogueContinuing(Queue<String> options) {
        return !options.isEmpty() || widgetHandler.isWidgetVisible("Click here to continue", true);
    }

    private void handleDialogueOption(Queue<String> options) {
        if (!options.isEmpty() && widgetHandler.isWidgetVisible(options.peek(), true)) {
            String currentOption = options.poll();
            script.log("Selecting '" + currentOption + "'");
            widgetHandler.clickWidgetWithMessage(currentOption);
        }
    }

    private void handleClickHereToContinue() {
        if (widgetHandler.isWidgetVisible("Click here to continue", true) &&
            widgetHandler.clickWidgetWithMessage("Click here to continue")) {
            script.log("Clicking 'Click here to continue'");
        }
    }
}
