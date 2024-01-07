package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Spells;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;
import utils.*;

import java.util.Arrays;
import java.util.List;

public class MagicInstructorInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final DialogueHelper dialogueHelper;

    private final Area magicInstructorArea = new Area(3137, 3082, 3143, 3091);
    private final Area finalArea = new Area(3220, 3216, 3224, 3220);
    private final Area mainlandArea = new Area(3218, 3212, 3243, 3240);
    private final Position windCastPosition = new Position(3140, 3090, 0);
    private boolean talkedToMagicInstructorFirstTime = false;
    private boolean openedMagicTab = false;
    private boolean talkedToMagicInstructorSecondTime = false;
    private boolean castWindStrike = false;
    private boolean talkedToMagicInstructorThirdTime = false;
    private boolean onMainland = false;
    private boolean walkedToFinalArea = false;

    public MagicInstructorInteraction(Script script, WidgetHandler widgetHandler, DialogueHelper dialogueHelper) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.dialogueHelper = dialogueHelper;
    }

    public boolean performInteraction() throws InterruptedException {
        if (!magicInstructorArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToMagicInstructorFirstTime) {
            talkedToMagicInstructorFirstTime = talkToMagicInstructor();
        }
        if (talkedToMagicInstructorFirstTime && !openedMagicTab) {
            openedMagicTab = openMagicTab();
        }
        if (openedMagicTab && !talkedToMagicInstructorSecondTime) {
            talkedToMagicInstructorSecondTime = talkToMagicInstructor();
        }
        if (talkedToMagicInstructorSecondTime && !castWindStrike) {
            castWindStrike = castWindStrikeOnChicken();
        }
        if (castWindStrike && !talkedToMagicInstructorThirdTime) {
            talkedToMagicInstructorThirdTime = talkToMagicInstructorWithOptions();
        }
        if (talkedToMagicInstructorThirdTime && !onMainland) {
            onMainland = waitForMainland();
        }
        if (onMainland && !walkedToFinalArea) {
            walkedToFinalArea = webWalkToFinalArea();
        }

        return walkedToFinalArea;
    }

    private boolean talkToMagicInstructor() {
        return dialogueHelper.continueThroughDialogue("Magic Instructor");
    }

    private boolean openMagicTab() {
        return widgetHandler.openTab("Magic");
    }

    private boolean castWindStrikeOnChicken() throws InterruptedException {
        WalkingEvent windCastWalkEvent = new WalkingEvent(windCastPosition);
        windCastWalkEvent.setMinDistanceThreshold(0);
        script.execute(windCastWalkEvent);
        NPC chicken = script.getNpcs().closest("Chicken");
        if (chicken != null && script.magic.canCast(Spells.NormalSpells.WIND_STRIKE)) {
            Sleep.until(()-> !script.myPlayer().isAnimating());
            script.log("Casting Wind Strike on Chicken");
            script.magic.castSpellOnEntity(Spells.NormalSpells.WIND_STRIKE, chicken);
            return Sleep.until(chicken::isHitBarVisible);
        }
        return false;
    }

    private boolean talkToMagicInstructorWithOptions() {
        List<String> dialogueOptions = Arrays.asList("Yes.", "No, I'm not planning to do that.",  "Yes, send me to the mainland");
        return dialogueHelper.continueDynamicDialogue("Magic Instructor", dialogueOptions);
    }

    private boolean waitForMainland() {
        return Sleep.until(() -> mainlandArea.contains(script.myPlayer()), 15000); // Wait for up to 15 seconds to be on the mainland
    }

    private boolean webWalkToFinalArea() {
        return script.getWalking().webWalk(finalArea);
    }

}
