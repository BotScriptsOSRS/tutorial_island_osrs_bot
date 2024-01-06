package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.Script;
import utils.*;

import java.awt.event.KeyEvent;

public class CombatInstructorInteraction {
    private final Script script;
    private final WidgetHandler widgetHandler;
    private final DialogueHelper dialogueHelper;
    private final NPCHandler npcHandler;

    private final Area combatInstructorArea = new Area(3095, 9495, 3115, 9532);
    private final Area combatInstructorWalkToArea = new Area(3103, 9506, 3112, 9509);
    private final Position ratCombatPosition = new Position(3107, 9518, 0);
    private final Position instructorPosition = new Position(3106, 9510, 0);
    private final Area finalArea = new Area(3120, 3121, 3123, 3124);

    private boolean talkedToCombatInstructorFirstTime = false;
    private boolean equippedDagger = false;
    private boolean talkedToCombatInstructorSecondTime = false;
    private boolean equippedSwordAndShield = false;
    private boolean completedMeleeCombat = false;
    private boolean talkedToCombatInstructorThirdTime = false;
    private boolean equippedBowAndArrows = false;
    private boolean completedRangedCombat = false;
    private boolean walkedToFinalArea = false;

    public CombatInstructorInteraction(Script script, WidgetHandler widgetHandler, DialogueHelper dialogueHelper, NPCHandler npcHandler) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.dialogueHelper = dialogueHelper;
        this.npcHandler = npcHandler;
    }

    public boolean performInteraction() {
        if (!combatInstructorArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToCombatInstructorFirstTime) {
            talkedToCombatInstructorFirstTime = talkToCombatInstructor();
        }
        if (talkedToCombatInstructorFirstTime && !equippedDagger) {
            equippedDagger = equipDagger();
        }
        if (equippedDagger && !talkedToCombatInstructorSecondTime) {
            talkedToCombatInstructorSecondTime = talkToCombatInstructor();
        }
        if (talkedToCombatInstructorSecondTime && !equippedSwordAndShield) {
            equippedSwordAndShield = equipSwordAndShield();
        }
        if (equippedSwordAndShield && !completedMeleeCombat) {
            completedMeleeCombat = completeMeleeCombat();
        }
        if (completedMeleeCombat && !talkedToCombatInstructorThirdTime) {
            talkedToCombatInstructorThirdTime = talkToCombatInstructor();
        }
        if (talkedToCombatInstructorThirdTime && !equippedBowAndArrows) {
            equippedBowAndArrows = equipBowAndArrows();
        }
        if (equippedBowAndArrows && !completedRangedCombat) {
            completedRangedCombat = completeRangedCombat();
        }
        if (completedRangedCombat && !walkedToFinalArea) {
            walkedToFinalArea = webWalkToFinalArea();
        }

        return walkedToFinalArea;
    }

    private boolean talkToCombatInstructor() {
        script.log("Talking to Combat Instructor");
        if(widgetHandler.openTab("Combat Options")){
            if (!combatInstructorWalkToArea.contains(script.myPlayer())){
                script.getWalking().webWalk(instructorPosition);
            }
            return dialogueHelper.continueThroughDialogue("Combat Instructor");
        }
        return false;
    }

    private boolean equipDagger() {
        script.log("Equipping Bronze dagger");
        if (widgetHandler.openTab("Worn Equipment")) {
            widgetHandler.waitForWidget("View equipment stats", false);
            widgetHandler.clickWidget("View equipment stats");
            if (widgetHandler.waitForWidget("Equip Your Character...", true)) {
                script.getEquipment().equipForNameThatContains(EquipmentSlot.WEAPON, "Bronze dagger");
                Sleep.until(()-> script.getEquipment().isWearingItem(EquipmentSlot.WEAPON,"Bronze dagger"));
                script.getKeyboard().pressKey((char) KeyEvent.VK_ESCAPE);
                return widgetHandler.waitForWidgetToDisappear("Equip Your Character...", false);
            }
        }
        return false;
    }

    private boolean equipSwordAndShield() {
        script.log("Equipping Bronze sword and Wooden shield");
        script.getEquipment().equipForNameThatContains(EquipmentSlot.WEAPON, "Bronze sword");
        Sleep.until(()-> script.getEquipment().isWearingItem(EquipmentSlot.WEAPON,"Bronze sword"));
        script.getEquipment().equipForNameThatContains(EquipmentSlot.SHIELD, "Wooden shield");
        Sleep.until(()-> script.getEquipment().isWearingItem(EquipmentSlot.SHIELD, "Bronze sword"));
        return script.getEquipment().isWearingItem(EquipmentSlot.SHIELD, "Wooden shield") &&
                script.getEquipment().isWearingItem(EquipmentSlot.WEAPON,"Bronze sword");
    }

    private boolean completeMeleeCombat() {
        script.log("Completing melee combat with Giant rat");
        script.getWalking().webWalk(ratCombatPosition);
        return attackGiantRat();
    }

    private boolean equipBowAndArrows() {
        script.log("Equipping Shortbow and Bronze arrows");
        script.getEquipment().equipForNameThatContains(EquipmentSlot.WEAPON, "Shortbow");
        Sleep.until(() -> script.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Shortbow"));
        script.getEquipment().equipForNameThatContains(EquipmentSlot.ARROWS, "Bronze arrows");
        Sleep.until(() -> script.getEquipment().isWearingItem(EquipmentSlot.ARROWS, "Bronze arrows"));
        return script.getEquipment().isWearingItem(EquipmentSlot.WEAPON, "Shortbow") &&
                script.getEquipment().isWearingItem(EquipmentSlot.ARROWS, "Bronze arrows");
    }


    private boolean completeRangedCombat() {
        script.log("Completing ranged combat with Giant rat");
        return attackGiantRat();
    }

    private boolean webWalkToFinalArea() {
        script.log("Webwalking to final area");
        return script.getWalking().webWalk(finalArea);
    }

    private boolean attackGiantRat() {
        script.log("Searching for a Giant rat to attack");

        boolean attacked = npcHandler.interactWithClosestNPCWithAction("Attack", () ->
                script.getNpcs().closest(npc -> npc.getName().equals("Giant rat") &&
                        !npc.isUnderAttack() &&
                        !npc.isInteracting(script.myPlayer())) != null);

        if (attacked) {
            script.log("Attacking Giant rat");
            boolean combatEnded = Sleep.until(() -> {
                NPC targetedRat = script.getNpcs().closest(npc -> npc.getName().equals("Giant rat") && npc.isInteracting(script.myPlayer()));
                return targetedRat == null || !targetedRat.exists() || !script.myPlayer().isAnimating();
            }, 10000);

            if (combatEnded) {
                script.log("Combat ended, rat is likely dead");
                return true; // Indicates the rat was killed
            } else {
                script.log("Combat did not end as expected");
                return false; // Indicates the rat was not killed within the expected time
            }
        } else {
            script.log("No suitable Giant rat found or unable to attack");
            return false; // Indicates no rat was attacked
        }
    }
}
