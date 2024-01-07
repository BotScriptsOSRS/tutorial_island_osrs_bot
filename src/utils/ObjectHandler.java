package utils;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.script.Script;

import java.util.Optional;
import java.util.function.BooleanSupplier;

public class ObjectHandler {
    private final Script script;

    public ObjectHandler(Script script) {
        this.script = script;
    }

    public boolean interactWithClosestObject(String objectName, String action, BooleanSupplier condition) {
        RS2Object object = script.getObjects().closest(obj -> obj.getName().equals(objectName));
        script.log("Trying to find the object: " + objectName);
        if (object != null && object.interact(action)) {
            script.log(action + " " + objectName);
            if (condition != null) {
                Sleep.until(condition, 10000);
            }
            return true;
        }
        return false;
    }

    public void moveAwayFromObject() {
        RS2Object fireObject = script.getObjects().closest(obj -> obj.getName().equals("Fire") && obj.getPosition().equals(script.myPlayer().getPosition()));

        script.log("Checking for objects below me");

        while (fireObject != null) {
            script.log("Object below me, searching for a new position");
            Optional<Position> newPositionOptional = findNewPosition(1);

            if (newPositionOptional.isPresent()) {
                Position newPosition = newPositionOptional.get();
                script.log("Moving to new position: " + newPosition);

                WalkingEvent walkEvent = new WalkingEvent(newPosition);
                walkEvent.setMinDistanceThreshold(0); // Set the minimum distance threshold
                script.execute(walkEvent);

                Sleep.until(() -> script.myPlayer().getPosition().equals(newPosition));
            } else {
                script.log("Couldn't find a free position, waiting before retrying");
                Sleep.randomSleep(500, 1000); // Wait a bit before retrying
            }

            // Update object for the next iteration
            fireObject = script.getObjects().closest(obj -> obj.getPosition().equals(script.myPlayer().getPosition()));
        }
        script.log("No objects below me, position updated");
    }

    private boolean isPositionFreeOfObjects(Position position) {
        return script.getObjects().getAll().stream()
                .noneMatch(obj -> obj.getPosition().equals(position));
    }

    private Optional<Position> findNewPosition(int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                if (x == 0 && y == 0) continue; // Skip current position

                Position newPosition = script.myPlayer().getPosition().translate(x, y);
                if (script.getMap().canReach(newPosition) && isPositionFreeOfObjects(newPosition)) {
                    return Optional.of(newPosition);
                }
            }
        }
        return Optional.empty();
    }
}
