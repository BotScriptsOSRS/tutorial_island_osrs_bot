package utils;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.script.MethodProvider;
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

    public boolean waitForWidget(String actionOrMessage, boolean isMessage) {
        return Sleep.sleepUntil(() -> isWidgetVisible(actionOrMessage, isMessage), 5000);
    }

    public boolean waitForWidgetToDisappear(String actionOrMessage, boolean isMessage) {
        return Sleep.sleepUntil(() -> !isWidgetVisible(actionOrMessage, isMessage), 5000);
    }

    public void interactWithWidgetRandomly(String action) {
        getRandomWidgetWithAction(action).ifPresent(widget -> {
            if (random.nextBoolean()) {
                widget.interact();
            }
        });
    }

    public void interactWithAllSelectWidgets() throws InterruptedException {
        List<RS2Widget> selectWidgets = script.getWidgets().getAll().stream()
                .filter(widget -> widget.isVisible() && widget.hasAction("Select"))
                .collect(Collectors.toList());
        for (RS2Widget widget : selectWidgets) {
            int interactions = random.nextInt(4);
            for (int i = 0; i < interactions; i++) {
                if (widget.interact()) {
                    MethodProvider.sleep(random.nextInt(300) + 200);
                }
            }
        }
    }

    public void clickWidget(String action) {
        findWidget(action, false).ifPresent(RS2Widget::interact);
    }

    public boolean clickWidgetWithMessage(String message) {
        Optional<RS2Widget> widgetOptional = findWidget(message, true);
        return widgetOptional.map(RS2Widget::interact).orElse(false);
    }

    public void clickAllWidgetsWithAction(String action) throws InterruptedException {
        List<RS2Widget> widgets = script.getWidgets().getAll().stream()
                .filter(widget -> widget.isVisible() && widget.hasAction(action))
                .collect(Collectors.toList());

        for (RS2Widget widget : widgets) {
            if (widget.interact()) {
                MethodProvider.sleep(random.nextInt(300) + 200); // Sleep between interactions
            }
        }
    }
}
