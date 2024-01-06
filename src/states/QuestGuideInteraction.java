package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.script.Script;
import utils.*;

public class QuestGuideInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final DialogueHelper dialogueHelper;
    private final Area questGuideArea = new Area(3083, 3119, 3089, 3125);
    private final Position miningPosition = new Position(3081, 9504, 0);

    private boolean talkedToQuestGuideFirstTime = false;
    private boolean openedQuestTab = false;
    private boolean talkedToQuestGuideSecondTime = false;
    private boolean walkedToMiningPosition = false;

    public QuestGuideInteraction(Script script, WidgetHandler widgetHandler, DialogueHelper dialogueHelper) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.dialogueHelper = dialogueHelper;
    }

    public boolean performInteraction() {
        if (!questGuideArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToQuestGuideFirstTime) {
            talkedToQuestGuideFirstTime = talkToQuestGuide();
        }
        if (talkedToQuestGuideFirstTime && !openedQuestTab) {
            openedQuestTab = openQuestTab();
        }
        if (openedQuestTab && !talkedToQuestGuideSecondTime) {
            talkedToQuestGuideSecondTime = talkToQuestGuide();
        }
        if (talkedToQuestGuideSecondTime && !walkedToMiningPosition) {
            walkedToMiningPosition = webWalkToMiningPosition();
        }
        return walkedToMiningPosition;
    }

    private boolean talkToQuestGuide() {
        script.log("Talking to Quest Guide");
        return dialogueHelper.continueThroughDialogue("Quest Guide");
    }

    private boolean openQuestTab() {
        script.log("Opening Quest Tab");
        return widgetHandler.openTab("Quest List");
    }

    private boolean webWalkToMiningPosition() {
        script.log("Webwalking to mining position");
        return script.getWalking().webWalk(miningPosition);
    }
}
