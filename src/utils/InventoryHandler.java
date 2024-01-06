package utils;

import org.osbot.rs07.script.Script;

import java.util.function.BooleanSupplier;

public class InventoryHandler {
    private final Script script;

    public InventoryHandler(Script script) {
        this.script = script;
    }

    public boolean useItemOnAnother(String item1, String item2, BooleanSupplier condition) {
        if (script.getInventory().contains(item1) && script.getInventory().contains(item2)) {
            script.log("Using " + item1 + " on " + item2);
            if (script.getInventory().interact("Use", item1)) {
                Sleep.randomSleep(500, 1000);
                script.getInventory().getItem(item2).interact();
                Sleep.randomSleep(1000, 1500);
                Sleep.until(condition);
                return true;
            }
        } else {
            script.log("Required items not found in inventory.");
        }
        return false;
    }
}
