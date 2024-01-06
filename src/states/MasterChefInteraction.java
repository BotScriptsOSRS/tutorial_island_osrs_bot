package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import utils.*;


public class MasterChefInteraction {
    private final Script script;
    private final ObjectHandler objectHandler;
    private final DialogueHelper dialogueHelper;
    private final InventoryHandler inventoryHandler;

    private final Area chefArea = new Area(3073, 3083, 3078, 3091);
    private final Area questArea = new Area(3084, 3120, 3088, 3124);

    private boolean talkedToMasterChef = false;
    private boolean createdBreadDough = false;
    private boolean cookedBread = false;
    private boolean walkedToQuestArea = false;

    public MasterChefInteraction(Script script, ObjectHandler objectHandler, DialogueHelper dialogueHelper, InventoryHandler inventoryHandler) {
        this.script = script;
        this.objectHandler = objectHandler;
        this.dialogueHelper = dialogueHelper;
        this.inventoryHandler = inventoryHandler;
    }

    public boolean performInteraction() {
        if (!chefArea.contains(script.myPlayer().getPosition())) {
            script.log("Skipping stage, already completed");
            return true;
        }

        if (!talkedToMasterChef) {
            talkedToMasterChef = talkToMasterChef();
        }
        if (talkedToMasterChef && !createdBreadDough) {
            createdBreadDough = useIngredients();
        }
        if (createdBreadDough && !cookedBread) {
            cookedBread = cookBread();
        }
        if (cookedBread && !walkedToQuestArea) {
            walkedToQuestArea = webWalkToQuestArea();
        }

        return walkedToQuestArea;
    }

    private boolean talkToMasterChef() {
        return dialogueHelper.continueThroughDialogue("Master Chef");
    }

    private boolean useIngredients() {
        return inventoryHandler.useItemOnAnother("Pot of flour", "Bucket of water", () -> script.getInventory().contains("Bread dough"));
    }

    private boolean cookBread() {
        return objectHandler.interactWithClosestObject("Range", "Cook", () -> script.getInventory().contains("Bread"));
    }

    private boolean webWalkToQuestArea() {
        return script.getWalking().webWalk(questArea);
    }
}
