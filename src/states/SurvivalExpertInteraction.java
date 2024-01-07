package states;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.script.Script;
import utils.*;

public class SurvivalExpertInteraction {
    private static final Area SURVIVAL_EXPERT_WALK_TO_AREA = new Area(3101, 3095, 3104, 3098);
    private static final Area COOKS_HOUSE_WALK_TO_AREA = new Area(3073, 3084, 3075, 3086);

    private static final Area COOKS_HOUSE_AREA = new Area(
            new int[][]{
                    {3073, 3083},
                    {3078, 3083},
                    {3078, 3086},
                    {3077, 3086},
                    {3077, 3091},
                    {3073, 3091},
                    {3073, 3089},
                    {3074, 3089},
                    {3074, 3087},
                    {3075, 3087},
                    {3075, 3088},
                    {3074, 3088},
                    {3074, 3086},
                    {3073, 3086}
            }
    );

    private static final Area SURVIVAL_EXPERT_AREA = new Area(
            new int[][]{
                    {3079, 3082},
                    {3107, 3082},
                    {3107, 3114},
                    {3083, 3114},
                    {3083, 3098},
                    {3079, 3098}
            }
    );

    private final Script script;
    private final WidgetHandler widgetHandler;
    private final ObjectHandler objectHandler;
    private final DialogueHelper dialogueHelper;
    private final NPCHandler npcHandler;
    private final InventoryHandler inventoryHandler;

    private boolean walkedToSurvivalExpert = false;
    private boolean handledInventory = false;
    private boolean fishedShrimps = false;
    private boolean openedSkillsTab = false;
    private boolean choppedTree = false;
    private boolean burnedLogs = false;
    private boolean cookedShrimps = false;
    private boolean walkedToCooksHouse = false;


    public SurvivalExpertInteraction(Script script, WidgetHandler widgetHandler, ObjectHandler objectHandler, DialogueHelper dialogueHelper, NPCHandler npcHandler, InventoryHandler inventoryHandler) {
        this.script = script;
        this.widgetHandler = widgetHandler;
        this.objectHandler = objectHandler;
        this.dialogueHelper = dialogueHelper;
        this.npcHandler = npcHandler;
        this.inventoryHandler = inventoryHandler;
    }

    public boolean performInteraction() {
        if (!isInSurvivalExpertArea()) {
            script.log("Skipping state, already completed");
            return true;
        }

        if (!walkedToSurvivalExpert) {
            walkToSurvivalExpert();
        }
        if (walkedToSurvivalExpert && !handledInventory) {
            handleInventoryInteractions();
        }
        if (handledInventory && !fishedShrimps) {
            fishShrimps();
        }
        if (fishedShrimps && !openedSkillsTab) {
            openSkillsTab();
        }
        if (openedSkillsTab && !choppedTree) {
            chopDownTree();
        }
        if (choppedTree && !burnedLogs) {
            burnLogs();
        }
        if (burnedLogs && !cookedShrimps) {
            cookShrimps();
        }
        if (cookedShrimps && !walkedToCooksHouse) {
            walkToCooksHouse();
        }

        return false;
    }

    private boolean isInSurvivalExpertArea() {
        return SURVIVAL_EXPERT_AREA.contains(script.myPlayer().getPosition());
    }

    private void walkToSurvivalExpert() {
        if (!SURVIVAL_EXPERT_WALK_TO_AREA.contains(script.myPlayer()) && script.getWalking().walk(SURVIVAL_EXPERT_WALK_TO_AREA)) {
            script.log("Walking to Survival Expert");
            Sleep.until(() -> SURVIVAL_EXPERT_WALK_TO_AREA.contains(script.myPlayer()));
            walkedToSurvivalExpert = true;
        }
    }

    private void handleInventoryInteractions() {
        if (dialogueHelper.continueThroughDialogue("Survival Expert")) {
            script.log("Talking to Survival Expert");
            widgetHandler.waitForWidget("Inventory", false);
        }
        if (widgetHandler.openTab("Inventory")) {
            script.log("Opening inventory");
            handledInventory= true;
        }
    }

    private void fishShrimps() {
        if (npcHandler.interactWithClosestNPCWithAction("Net", () -> script.getInventory().contains("Raw shrimps"))) {
            script.log("Fishing raw shrimps");
            fishedShrimps = true;
        }
    }

    private void openSkillsTab() {
        if (widgetHandler.isWidgetVisible("Skills", false) && widgetHandler.openTab("Skills")) {
            script.log("Opening skills tab");
            openedSkillsTab = true;
        }
    }

    private void chopDownTree() {
        if (dialogueHelper.continueThroughDialogue("Survival Expert") &&
                objectHandler.interactWithClosestObject("Tree", "Chop down", () -> script.getInventory().contains("Logs"))) {
            script.log("Chopping down tree");
            choppedTree = true;
        }
    }

    private void burnLogs(){
        objectHandler.moveAwayFromObject();
        if (inventoryHandler.useItemOnAnother("Tinderbox", "Logs", () -> !script.myPlayer().isAnimating())){
            burnedLogs = true;
        }
    }

    private void cookShrimps() {
        if (script.getInventory().interactWithNameThatContains("Use", "Raw shrimps") &&
                objectHandler.interactWithClosestObject("Fire", "Use", () -> script.getInventory().contains("Shrimps"))) {
            cookedShrimps = script.getInventory().contains("Shrimps");
        }
    }

    private void walkToCooksHouse() {
        if (script.getInventory().contains("Shrimps") && !COOKS_HOUSE_AREA.contains(script.myPlayer())) {
            script.log("Walking to Cooking Guide's house");
            script.getWalking().webWalk(COOKS_HOUSE_WALK_TO_AREA);
            walkedToCooksHouse = true;
        }
    }
}