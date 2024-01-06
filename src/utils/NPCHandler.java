package utils;

import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.Script;
import java.util.function.BooleanSupplier;


public class NPCHandler {
    private final Script script;

    public NPCHandler(Script script) {
        this.script = script;
    }

    public boolean interactWithClosestNPCWithAction(String npcAction, BooleanSupplier condition) {
        NPC npc = script.getNpcs().closest(n -> n.hasAction(npcAction));
        if (npc != null && npc.interact(npcAction)) {
            script.log("Interacted with NPC using action: " + npcAction);
            if (condition != null) {
                Sleep.until(condition,10000);
            }
            return true;
        }
        return false;
    }
}
