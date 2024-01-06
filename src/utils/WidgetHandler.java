package utils;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.Script;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WidgetHandler {

    private final Script script;
    private final Random random = new Random();

    public WidgetHandler(Script script) {
        this.script = script;
    }

    public boolean isWidgetVisible(String actionOrMessage, boolean isMessage) {
        return findWidget(actionOrMessage, isMessage).isPresent();
    }

    public Optional<RS2Widget> getRandomWidgetWithAction(String action) {
        List<RS2Widget> widgets = script.getWidgets().getAll().stream()
                .filter(widget -> widget.isVisible() && widget.hasAction(action))
                .collect(Collectors.toList());
        return widgets.isEmpty() ? Optional.empty() : Optional.of(widgets.get(random.nextInt(widgets.size())));
    }

    public Optional<RS2Widget> findWidget(String actionOrMessage, boolean isMessage) {
        Predicate<RS2Widget> predicate = widget -> widget.isVisible() &&
                (isMessage ? widget.getMessage().contains(actionOrMessage) : widget.hasAction(actionOrMessage));
        return script.getWidgets().getAll().stream().filter(predicate).findFirst();
    }

    public boolean clickWidgetWithSpellName(String action, String spellName) {
        Optional<RS2Widget> widgetOptional = findWidgetWithSpellName(action, spellName);
        return widgetOptional.map(RS2Widget::interact).orElse(false);
    }

    public Optional<RS2Widget> findWidgetWithSpellName(String action, String spellName) {
        Predicate<RS2Widget> predicate = widget ->
                widget.isVisible() &&
                        widget.hasAction(action) &&
                        (spellName == null || widget.getSpellName().equals(spellName));

        return script.getWidgets().getAll().stream().filter(predicate).findFirst();
    }


    public boolean waitForWidget(String actionOrMessage, boolean isMessage) {
        return Sleep.until(() -> isWidgetVisible(actionOrMessage, isMessage));
    }

    public boolean waitForWidgetToDisappear(String actionOrMessage, boolean isMessage) {
        return Sleep.until(() -> !isWidgetVisible(actionOrMessage, isMessage));
    }

    public void interactWithWidgetRandomly(String action) {
        getRandomWidgetWithAction(action).ifPresent(widget -> {
            if (random.nextBoolean()) {
                widget.interact();
            }
        });
    }

    public void interactWithAllSelectWidgets() {
        List<RS2Widget> selectWidgets = script.getWidgets().getAll().stream()
                .filter(widget -> widget.isVisible() && widget.hasAction("Select"))
                .collect(Collectors.toList());
        for (RS2Widget widget : selectWidgets) {
            int interactions = random.nextInt(4);
            for (int i = 0; i < interactions; i++) {
                if (widget.interact()) {
                    Sleep.randomSleep(200, 300);
                }
            }
        }
    }

    public boolean clickWidget(String action) {
        Optional<RS2Widget> widgetOptional = findWidget(action, false);
        return widgetOptional.map(RS2Widget::interact).orElse(false);
    }

    public boolean clickWidgetWithMessage(String message) {
        Optional<RS2Widget> widgetOptional = findWidget(message, true);
        return widgetOptional.map(RS2Widget::interact).orElse(false);
    }

    public void clickAllWidgetsWithAction(String action) {
        List<RS2Widget> widgets = script.getWidgets().getAll().stream()
                .filter(widget -> widget.isVisible() && widget.hasAction(action))
                .collect(Collectors.toList());

        for (RS2Widget widget : widgets) {
            if (widget.interact()) {
                Sleep.randomSleep(200, 300);
            }
        }
    }

    public boolean openTab(String tab) {
        if (isWidgetVisible(tab, false)) {
            clickWidget(tab);
            Sleep.randomSleep(200, 300);
            return true;
        }
        return false;
    }
}
