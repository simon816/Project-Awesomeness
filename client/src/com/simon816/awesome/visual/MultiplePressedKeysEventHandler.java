package com.simon816.awesome.visual;

import java.util.EnumSet;
import java.util.Set;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 
 * @author Paul
 */
class MultiplePressedKeysEventHandler implements EventHandler<KeyEvent> {

    private final Set<KeyCode> buffer = EnumSet.noneOf(KeyCode.class);
    private final MultiKeyEvent multiKeyEvent = new MultiKeyEvent();

    private final MultiKeyEventHandler multiKeyEventHandler;

    public MultiplePressedKeysEventHandler(final MultiKeyEventHandler handler) {
        this.multiKeyEventHandler = handler;
    }

    @Override
    public void handle(final KeyEvent event) {
        final KeyCode code = event.getCode();

        if (KeyEvent.KEY_PRESSED.equals(event.getEventType())) {
            buffer.add(code);
            multiKeyEventHandler.handle(multiKeyEvent);
        } else if (KeyEvent.KEY_RELEASED.equals(event.getEventType())) {
            buffer.remove(code);
        }
        event.consume();
    }

    interface MultiKeyEventHandler {
        void handle(final MultiKeyEvent event);
    }

    class MultiKeyEvent {
        public boolean isPressed(final KeyCode key) {
            return buffer.contains(key);
        }
    }
}