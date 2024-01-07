package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import utils.*;

public class BrotherBraceInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final DialogueHelper dialogueHelper;

    private final Area brotherBraceArea = new Area(3120, 3103, 3128, 3110);
    private final Area finalArea = new Area(3140, 3085, 3143, 3088);

    private boolean talkedToBrotherBraceFirstTime = false;
    private boolean openedPrayerTab = false;
    private boolean talkedToBrotherBraceSecondTime = false;
    private boolean openedFriendsListTab = false;
    private boolean talkedToBrotherBraceThirdTime = false;
    private boolean walkedToFinalArea = false;

    public BrotherBraceInteraction(Script script, WidgetHandler widgetHandler, DialogueHelper dialogueHelper) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.dialogueHelper = dialogueHelper;
    }

    public boolean performInteraction() {
        if (!brotherBraceArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToBrotherBraceFirstTime) {
            talkedToBrotherBraceFirstTime = talkToBrotherBrace();
        }
        if (talkedToBrotherBraceFirstTime && !openedPrayerTab) {
            openedPrayerTab = openPrayerTab();
        }
        if (openedPrayerTab && !talkedToBrotherBraceSecondTime) {
            talkedToBrotherBraceSecondTime = talkToBrotherBrace();
        }
        if (talkedToBrotherBraceSecondTime && !openedFriendsListTab) {
            openedFriendsListTab = openFriendsListTab();
        }
        if (openedFriendsListTab && !talkedToBrotherBraceThirdTime) {
            talkedToBrotherBraceThirdTime = talkToBrotherBrace();
        }
        if (talkedToBrotherBraceThirdTime) {
            walkedToFinalArea = webWalkToArea(finalArea);
        }

        return walkedToFinalArea;
    }

    private boolean talkToBrotherBrace() {
        return dialogueHelper.continueThroughDialogue("Brother Brace");
    }

    private boolean openPrayerTab() {
        return widgetHandler.openTab("Prayer");
    }

    private boolean openFriendsListTab() {
        return widgetHandler.openTab("Friends List");
    }

    private boolean webWalkToArea(Area area) {
        return script.getWalking().webWalk(area);
    }
}
