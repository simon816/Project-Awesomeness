package com.simon816.awesome.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.simon816.awesome.core.Actions;
import com.simon816.awesome.network.packet.Packet;
import com.simon816.awesome.network.packet.PacketIsAlive;
import com.simon816.awesome.network.packet.PacketLostConnection;
import com.simon816.awesome.network.packet.PacketNeighbourUpdate;
import com.simon816.awesome.network.packet.PacketPong;

public class NetHandler {
    private InetSocketAddress address;
    private Socket socket;
    private Sender sender;
    private Reciever reciever;
    private Actions actions;
    private Thread sendThread;
    private Thread recvThread;
    private boolean ready;
    private int waitTicks;
    private int neighbours;

    public NetHandler(InetAddress host, int port) {
        address = new InetSocketAddress(host, port);
    }

    public void connectForever() {
        while (socket == null) {
            System.out.println("Null socket");
            try {
                socket = new Socket(address.getAddress(), address.getPort());
            } catch (IOException e) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                }
            }
        }
        try {
            startRecieverThread(socket.getInputStream());
        } catch (IOException e) {
            kill();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
            }
            connectForever();
            return;
        }
        try {
            startSenderThread(socket.getOutputStream());
        } catch (IOException e) {
            kill();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
            }
            connectForever();
            return;
        }
    }

    private void kill() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
    }

    private void failed() {
        kill();
        sender.kill(sendThread);
        reciever.kill(recvThread);
        actions.notifyClosed();
    }

    public void recvFail(Reciever r) {
        System.out.println("recvFail");
        failed();
    }

    public void recvFail(PacketLostConnection p) {
        recvFail(reciever);
    }

    public void sendFail(Sender s) {
        System.out.println("sendFail");
        failed();
    }

    private void startSenderThread(OutputStream outputStream) {
        sendThread = new Thread(sender = new Sender(new DataOutputStream(outputStream)));
        sendThread.setDaemon(true);
        sendThread.setName("Network-Send-Thread");
        sendThread.start();
    }

    private void startRecieverThread(InputStream inputStream) {
        recvThread = new Thread(reciever = new Reciever(new DataInputStream(inputStream)));
        reciever.setHandler(this);
        recvThread.setDaemon(true);
        recvThread.setName("Network-Recieve-Thread");
        recvThread.start();
    }

    public void sendPacket(Packet packet) {
        sender.sendPacket(packet, this);
    }

    public void setActionHandler(Actions actions) {
        this.actions = actions;
    }

    public void tickAlive() {
        if (!ready) {
            waitTicks++;
            if (waitTicks > 3) {
                waitTicks = 0;
                failed();
                return;
            }
        }
        sender.sendPacket(new PacketIsAlive(neighbours), this);
        actions.onReady();
    }

    public void ready() {
        ready = true;
        actions.onReady();
    }

    public void updateNeighbours(int neighbours) {
        this.neighbours = neighbours;
        System.out.println("Update: " + neighbours);
        if ((neighbours & PacketNeighbourUpdate.LEFT) == PacketNeighbourUpdate.LEFT) {
            actions.leftConnect(true);
        } else {
            actions.leftConnect(false);
        }
        if ((neighbours & PacketNeighbourUpdate.RIGHT) == PacketNeighbourUpdate.RIGHT) {
            actions.rightConnect(true);
        } else {
            actions.rightConnect(false);
        }
    }

    public void showText(String text) {
        actions.setText(text);
    }

    public void hideText() {
        actions.setText(null);
    }

    public void setColor(int r, int g, int b) {
        actions.setColor(r, g, b);
    }

    public void removeColor() {
        actions.hideColor();
    }

    public void showCircle() {
        actions.doCircle();
    }

    public void randomStuff() {
        actions.doRandom();
    }

    public void setupPong(int flags) {
        actions.setupPong((flags & PacketPong.LEFT) == PacketPong.LEFT, (flags & PacketPong.RIGHT) == PacketPong.RIGHT);
    }

    public void spawnPongBall(int posX, int posY, int velX, int velY) {
        actions.spawnPongBall(posX, posY, velX, velY);
    }

    public void removePong() {
        actions.removePong();
    }
}
