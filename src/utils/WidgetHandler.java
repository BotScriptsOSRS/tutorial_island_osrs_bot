package utils;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

import java.util.List;
import java.util.Random;

public class WidgetHandler {

    private final Script script;

    public WidgetHandler(Script script) {
        this.script = script;
    }

    public boolean isDisplayNameWidgetVisible() {
        return getWidget("Set display name", true) != null;
    }

    public boolean isSetNameWidgetAvailable() {
        return getWidget("Set name", false) != null;
    }

    public RS2Widget getRandomSetNameWidget() {
        List<RS2Widget> setNameWidgets = script.getWidgets().filter(
                script.getWidgets().getAll(),
                widget -> widget.isVisible() && widget.hasAction("Set name")
        );
        return !setNameWidgets.isEmpty() ? setNameWidgets.get(new Random().nextInt(setNameWidgets.size())) : null;
    }

    public boolean isNameWidgetWorking() {
        RS2Widget nameWidget = getNameWidget();
        return nameWidget != null && nameWidget.isVisible();
    }

    public RS2Widget getNameWidget() {
        return script.getWidgets().singleFilter(
                script.getWidgets().getAll(),
                widget -> widget.isVisible() && widget.getMessage().contains("*"));
    }

    private RS2Widget getWidget(String actionOrMessage, boolean isMessage) {
        return script.getWidgets().singleFilter(
                script.getWidgets().getAll(),
                widget -> widget.isVisible() && (isMessage ? widget.getMessage().contains(actionOrMessage) : widget.hasAction(actionOrMessage))
        );
    }

    public boolean isGreatWidgetVisible() {
        return script.getWidgets().singleFilter(
                script.getWidgets().getAll(),
                widget -> widget.isVisible() && widget.getMessage().contains("Great!")) != null;
    }
    public boolean waitForConfirmWidget() {
        return Sleep.sleepUntil(() -> getWidget("Confirm", false) != null, 15000);
    }

    public void interactWithFemaleRandomly() {
        if (new Random().nextBoolean()) {
            RS2Widget femaleWidget = getWidget("Female", false);
            if (femaleWidget != null) {
                femaleWidget.interact();
            }
        }
    }

    public void interactWithSelectWidgets() throws InterruptedException {
        Random random = new Random();
        List<RS2Widget> selectWidgets = script.getWidgets().filter(
                script.getWidgets().getAll(),
                widget -> widget.isVisible() && widget.hasAction("Select")
        );

        for (RS2Widget selectWidget : selectWidgets) {
            int interactions = random.nextInt(4); // Random number between 0 and 3
            for (int i = 0; i < interactions; i++) {
                if (selectWidget.interact()) {
                    MethodProvider.sleep(random.nextInt(1000) + 1000); // Sleep for 1-2 seconds after each interaction
                }
            }
        }
    }

    public void clickConfirmWidget() {
        RS2Widget confirmWidget = getWidget("Confirm", false);
        if (confirmWidget != null) {
            confirmWidget.interact();
        }
    }
}
