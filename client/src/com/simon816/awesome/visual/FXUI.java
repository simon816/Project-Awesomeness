package com.simon816.awesome.visual;

import java.util.Hashtable;
import java.util.Map;

import com.simon816.awesome.core.Actions;
import com.simon816.awesome.core.Main;
import com.simon816.awesome.visual.MultiplePressedKeysEventHandler.MultiKeyEvent;
import com.sun.javafx.scene.traversal.Direction;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FXUI extends Application {

    private static final int PONG_BAR_WIDTH = 20;
    private static final int PONG_BAR_HEIGHT = 400;

    private Path path;
    private Timeline connectLeftAni;
    private Path leftPath;
    private Timeline connectRightAni;
    private Path rightPath;
    private Circle circle;
    private Text text;
    private Group group1;
    private Group group2;
    private int width;
    private int height;
    private Group group3;
    private Rectangle colorRect;
    private Group group4;
    private Circle flyingCircle;
    private Timeline flyCircle;
    private Group group5;
    private Group group6;
    private Circle pongBall;
    private Timeline pongLoop;
    private Rectangle rightBar;
    private Rectangle leftBar;
    private Rectangle bottomBar;
    private Rectangle topBar;
    private boolean useLeftPong;
    private boolean useRightPong;
    private double pongDeltaX;
    private double pongDeltaY;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Project Awesomeness");
        // primaryStage.setFullScreen(true);
        Group root = new Group();
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        Map<String, String> params = getParameters().getNamed();
        width = (int) bounds.getWidth();
        height = (int) (bounds.getHeight() - 100);
        if (params.containsKey("width")) {
            width = Integer.parseInt(params.get("width"));
        }
        if (params.containsKey("height")) {
            height = Integer.parseInt(params.get("height"));
        }
        Scene scene = new Scene(root, width, height, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.toFront();

        group1 = new Group();
        group2 = new Group();
        group3 = new Group();
        group4 = new Group();
        group5 = new Group();
        group6 = new Group();

        connectLeftAni = addPath(0);
        leftPath = path;
        connectRightAni = addPath(width);
        rightPath = path;

        circle = new Circle(width / 2, height / 2, 40, Color.DARKRED);
        circle.setEffect(new BoxBlur(20, 20, 1));
        group1.getChildren().add(circle);

        text = new Text("");
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Arial", 800));
        int h = (int) com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().getFontMetrics(text.getFont()).getLineHeight();
        text.setLayoutY((height / 2) + (h / 4));
        group2.getChildren().add(text);
        group2.setVisible(false);

        colorRect = new Rectangle(width, height);
        group3.getChildren().add(colorRect);
        group3.setVisible(false);

        flyingCircle = new Circle();
        group4.getChildren().add(flyingCircle);
        group4.setVisible(false);
        addFlyingTimeline();

        Rectangle r = new Rectangle();
        r.setLayoutX(width / 2 - 100);
        r.setLayoutY(height / 2 - 50);
        r.setWidth(200);
        r.setHeight(100);
        r.setFill(Color.RED);
        group5.getChildren().addAll(r);
        group5.setVisible(false);

        topBar = new Rectangle();
        topBar.setWidth(PONG_BAR_HEIGHT);
        topBar.setHeight(PONG_BAR_WIDTH);
        topBar.setFill(Color.GRAY);

        bottomBar = new Rectangle();
        bottomBar.setWidth(PONG_BAR_HEIGHT);
        bottomBar.setHeight(PONG_BAR_WIDTH);
        bottomBar.setFill(Color.GRAY);

        leftBar = new Rectangle();
        leftBar.setFill(Color.GRAY);
        leftBar.setWidth(PONG_BAR_WIDTH);
        leftBar.setHeight(PONG_BAR_HEIGHT);

        rightBar = new Rectangle();
        rightBar.setFill(Color.GRAY);
        rightBar.setWidth(PONG_BAR_WIDTH);
        rightBar.setHeight(PONG_BAR_HEIGHT);

        pongBall = new Circle(15, Color.LIGHTGRAY);
        group6.getChildren().addAll(topBar, bottomBar, leftBar, rightBar, pongBall);
        pongLoop = getPongTimeline(pongBall);
        pongLoop.setCycleCount(Timeline.INDEFINITE);
        group6.setVisible(false);

        group1.setVisible(true);
        root.getChildren().addAll(group1, group2, group3, group4, group5, group6);

        primaryStage.show();
        MultiplePressedKeysEventHandler pongKeyHandler = new MultiplePressedKeysEventHandler(new MultiplePressedKeysEventHandler.MultiKeyEventHandler() {

            private static final int SHIFT = 14;

            @Override
            public void handle(MultiKeyEvent event) {
                if (event.isPressed(KeyCode.RIGHT)) {
                    topBar.setLayoutX(Math.min(topBar.getLayoutX() + SHIFT, width - PONG_BAR_HEIGHT));
                    bottomBar.setLayoutX(Math.min(bottomBar.getLayoutX() + SHIFT, width - PONG_BAR_HEIGHT));
                }
                if (event.isPressed(KeyCode.LEFT)) {
                    topBar.setLayoutX(Math.max(topBar.getLayoutX() - SHIFT, 0));
                    bottomBar.setLayoutX(Math.max(bottomBar.getLayoutX() - SHIFT, 0));
                }
                if (event.isPressed(KeyCode.UP)) {
                    leftBar.setLayoutY(Math.max(leftBar.getLayoutY() - SHIFT, 0));
                    rightBar.setLayoutY(Math.max(rightBar.getLayoutY() - SHIFT, 0));
                }
                if (event.isPressed(KeyCode.DOWN)) {
                    leftBar.setLayoutY(Math.min(leftBar.getLayoutY() + SHIFT, height - PONG_BAR_HEIGHT));
                    rightBar.setLayoutY(Math.min(rightBar.getLayoutY() + SHIFT, height - PONG_BAR_HEIGHT));
                }
            }

        });
        primaryStage.addEventHandler(KeyEvent.ANY, pongKeyHandler);

        try {
            Thread.sleep(100); // Slow launch a bit
        } catch (InterruptedException e) {
        }
        Main.uiReady(this);
    }

    public void showLetter(String l) {
        group1.setVisible(false);
        group2.setVisible(true);
        int w = (int) com.sun.javafx.tk.Toolkit.getToolkit().getFontLoader().computeStringWidth(l, text.getFont());
        text.setLayoutX((width / 2) - w / 2);
        text.setText(l);
    }

    public void hideLetter() {
        group2.setVisible(false);
        group1.setVisible(true);
        text.setText("");
    }

    public void setPrepareColor() {
        circle.setFill(Color.color(0.7, 0.5, 0.09607843));
    }

    public void setErrorColor() {
        if (circle.getFill() != Color.DARKRED)
            circle.setFill(Color.DARKRED);
    }

    public void setReadyColor() {
        if (circle.getFill() != Color.GREEN)
            circle.setFill(Color.GREEN);
    }

    public void connectLeft() {
        connectLeftAni.play();
        leftPath.setVisible(true);
    }

    public void connectRight() {
        connectRightAni.play();
        rightPath.setVisible(true);
    }

    public void disconnectLeft() {
        leftPath.setVisible(false);
        connectLeftAni.stop();
    }

    public void disconnectRight() {
        rightPath.setVisible(false);
        connectRightAni.stop();
    }

    private Timeline addPath(int dest) {
        path = new Path();
        path.setFill(Color.BLUE);
        MoveTo moveTo = new MoveTo();
        moveTo.setX(width / 2);
        moveTo.setY(height / 2);
        QuadCurveTo quadCurveTo = new QuadCurveTo();
        quadCurveTo.setX(dest);
        quadCurveTo.setY(height / 2);
        quadCurveTo.setControlX(dest);
        quadCurveTo.setControlY((height / 2) + 20);
        path.getElements().addAll(moveTo, quadCurveTo);
        path.setEffect(new BoxBlur(5, 5, 2));
        path.setVisible(false);
        group1.getChildren().add(path);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames()
                .addAll(new KeyFrame(Duration.ZERO, new KeyValue(quadCurveTo.controlXProperty(), dest), new KeyValue(quadCurveTo.controlYProperty(),
                        (height / 2) + 20)),
                        new KeyFrame(new Duration(2000), new KeyValue(quadCurveTo.controlXProperty(), Math.abs(dest - width / 4)), new KeyValue(quadCurveTo
                                .controlYProperty(), (height / 2) + 10)),
                        new KeyFrame(new Duration(4000), new KeyValue(quadCurveTo.controlXProperty(), dest), new KeyValue(quadCurveTo.controlYProperty(),
                                (height / 2) + 20)));
        timeline.setCycleCount(-1);
        return timeline;
    }

    public void setColor(int r, int g, int b) {
        colorRect.setFill(Color.rgb(r, g, b));
        group1.setVisible(false);
        group3.setVisible(true);
    }

    public void setNoColor() {
        group3.setVisible(false);
        group1.setVisible(true);
    }

    private void addFlyingTimeline() {
        flyCircle = new Timeline();
        flyCircle.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(flyingCircle.layoutXProperty(), width)),
                new KeyFrame(new Duration(700), new KeyValue(flyingCircle.layoutXProperty(), 0)));
    }

    public void sendCircle(final Actions actionHandler) {
        group1.setVisible(false);
        group4.setVisible(true);
        flyingCircle.setRadius(20);
        flyingCircle.setFill(Color.WHITE);
        flyingCircle.setLayoutX(width);
        flyingCircle.setLayoutY(height / 2);
        flyCircle.play();
        flyCircle.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                actionHandler.finishedCircle();
                group4.setVisible(false);
                group1.setVisible(true);
            }
        });
    }

    public void showRandom(final EventHandler<Event> onFinish) {
        group1.setVisible(false);
        group5.setVisible(true);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                group5.setVisible(false);
                group1.setVisible(true);
                onFinish.handle(null);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private Timeline getPongTimeline(final Circle ball) {
        return new Timeline(new KeyFrame(Duration.millis(50), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                ball.setLayoutX(ball.getLayoutX() + pongDeltaX);
                ball.setLayoutY(ball.getLayoutY() + pongDeltaY);
                Bounds bounds = ball.getBoundsInParent();
                boolean stop = false;
                Direction cause = null;
                if (bounds.getMaxY() > height - 10 || bounds.getMaxY() < 10)
                    stop = true;
                if (bounds.getMaxX() < 10 && pongDeltaX < 0) {
                    stop = true;
                    if (!useLeftPong)
                        cause = Direction.LEFT;
                }
                if (bounds.getMaxX() > width + 20 && pongDeltaX > 0) {
                    stop = true;
                    if (!useRightPong)
                        cause = Direction.RIGHT;
                }
                if (stop) {
                    pongLoop.stop();
                    pongLoop.getOnFinished().handle(new ActionEvent(cause, event.getTarget()));
                    return;
                }
                if (bounds.intersects(bottomBar.getBoundsInParent()) || bounds.intersects(topBar.getBoundsInParent()))
                    pongDeltaY *= -1;
                boolean intersectLeft = bounds.intersects(leftBar.getBoundsInParent());
                boolean intersectRight = bounds.intersects(rightBar.getBoundsInParent());
                if ((useLeftPong && intersectLeft) || (useRightPong && intersectRight))
                    pongDeltaX *= -1;
            }

        }));
    }

    public void setPongGame(boolean left, boolean right, final EventHandler<ActionEvent> onOutcome) {
        useLeftPong = left;
        useRightPong = right;
        leftBar.setVisible(left);
        rightBar.setVisible(right);
        topBar.setLayoutX((width / 2) - (PONG_BAR_HEIGHT / 2));
        bottomBar.setLayoutX((width / 2) - (PONG_BAR_HEIGHT / 2));
        bottomBar.setLayoutY(height - PONG_BAR_WIDTH);
        leftBar.setLayoutY((height / 2) - (PONG_BAR_HEIGHT / 2));
        rightBar.setLayoutY((height / 2) - (PONG_BAR_HEIGHT / 2));
        rightBar.setLayoutX(width - PONG_BAR_WIDTH);
        if (!left)
            leftBar.resize(0, 0);
        else
            leftBar.resize(PONG_BAR_WIDTH, PONG_BAR_HEIGHT);
        if (!right)
            rightBar.resize(0, 0);
        else
            rightBar.resize(PONG_BAR_WIDTH, PONG_BAR_HEIGHT);

        group1.setVisible(false);
        pongBall.setVisible(false);
        group6.setVisible(true);
        pongLoop.setOnFinished(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Hashtable<String, Object> data = new Hashtable<String, Object>();
                data.put("yPos", pongBall.getLayoutY());
                data.put("velX", pongDeltaX);
                data.put("velY", pongDeltaY);
                if (event.getSource() == Direction.LEFT && !useLeftPong) {
                    data.put("direction", Direction.LEFT);
                } else if (event.getSource() == Direction.RIGHT && !useRightPong) {
                    data.put("direction", Direction.RIGHT);
                } else {
                    data.put("endGame", true);
                }
                onOutcome.handle(new ActionEvent(data, event.getTarget()));
            }
        });
    }

    public void setPongBall(double xPos, double yPos, double xVel, double yVel) {
        if (xVel * yVel == 0) {
            // Remove
            pongBall.setVisible(false);
            return;
        }
        if (xPos == -1)
            xPos = width;
        pongBall.relocate(xPos, yPos);
        pongBall.setVisible(true);
        pongDeltaX = xVel;
        pongDeltaY = yVel;
        if (pongLoop.getStatus() != Status.RUNNING)
            pongLoop.play();
    }

    public void removePong() {
        if (pongLoop.getStatus() != Status.STOPPED)
            pongLoop.stop();
        group6.setVisible(false);
        group1.setVisible(true);
    }
}