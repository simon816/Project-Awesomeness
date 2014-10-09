package com.simon816.awesome.core;

import java.util.Hashtable;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

import com.simon816.awesome.network.NetHandler;
import com.simon816.awesome.network.packet.PacketEstablish;
import com.simon816.awesome.network.packet.PacketPong;
import com.simon816.awesome.network.packet.PacketRandom;
import com.simon816.awesome.network.packet.PacketSendCircle;
import com.simon816.awesome.visual.FXUI;
import com.sun.javafx.scene.traversal.Direction;

public class Actions {

    private NetHandler net;
    private FXUI ui;

    public Actions(FXUI ui, NetHandler netHandler) {
        netHandler.setActionHandler(this);
        net = netHandler;
        this.ui = ui;
    }

    public void notifyClosed() {
        ui.setErrorColor();
        ui.disconnectLeft();
        ui.disconnectRight();
        ui.hideLetter();
        ui.setNoColor();
        ui.removePong();
        net.connectForever();
        ui.setPrepareColor();
        net.sendPacket(new PacketEstablish());
    }

    public void onReady() {
        ui.setReadyColor();
    }

    public void leftConnect(boolean connected) {
        if (connected)
            ui.connectLeft();
        else
            ui.disconnectLeft();
    }

    public void rightConnect(boolean connected) {
        if (connected)
            ui.connectRight();
        else
            ui.disconnectRight();
    }

    public void setText(String text) {
        if (text != null && !"".equals(text)) {
            ui.showLetter(text);
        } else {
            ui.hideLetter();
        }
    }

    public void setColor(int r, int g, int b) {
        ui.setColor(r, g, b);
    }

    public void hideColor() {
        ui.setNoColor();
    }

    public void doCircle() {
        ui.sendCircle(this);
    }

    public void finishedCircle() {
        net.sendPacket(new PacketSendCircle());
    }

    public void doRandom() {
        ui.showRandom(new EventHandler<Event>() {
            @Override
            public void handle(Event arg0) {
                net.sendPacket(new PacketRandom(new Random().nextInt(2) + 1));
            }
        });
    }

    public void setupPong(boolean left, boolean right) {
        ui.setPongGame(left, right, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                @SuppressWarnings("unchecked")
                Hashtable<String, Object> data = (Hashtable<String, Object>) event.getSource();
                if (data.containsKey("endGame") && data.get("endGame") == Boolean.TRUE) {
                    net.sendPacket(new PacketPong(true));
                    return;
                }
                if (data.containsKey("direction")) {
                    int directionFlag = data.get("direction") == Direction.LEFT ? 1 : 2;
                    net.sendPacket(new PacketPong(directionFlag, ((Double) data.get("yPos")).intValue(), ((Double) data.get("velX")).intValue(), ((Double) data
                            .get("velY")).intValue()));
                    ui.setPongBall(0, 0, 0, 0);
                }
            }
        });
    }

    public void spawnPongBall(int xPos, int yPos, int xVel, int yVel) {
        ui.setPongBall(xPos, yPos, xVel, yVel);
    }

    public void removePong() {
        ui.removePong();
    }

}
