package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketPong extends Packet {

    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    private int type;
    private int barFlags;
    private int posX, posY, velX, velY;

    public PacketPong() {
    }

    public PacketPong(boolean gameEnd) {
        if (gameEnd)
            type = 0;
        else
            throw new IllegalArgumentException("Wrong constructor for PacketPong");
    }

    public PacketPong(int directionFlag, int posY, int velX, int velY) {
        type = 3;
        barFlags = directionFlag;
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
    }

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        dos.write(type);
        if (type == 3) {
            dos.write(barFlags);
            dos.writeInt(posY);
            dos.writeInt(velX);
            dos.writeInt(velY);
        }
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        type = dis.read();
        if (type == 1) {
            barFlags = dis.read();
        } else if (type == 2) {
            posX = dis.readInt();
            posY = dis.readInt();
            velX = dis.readInt();
            velY = dis.readInt();
        }
    }

    @Override
    public void process(NetHandler handler) {
        if (type == 1) {
            handler.setupPong(barFlags);
        } else if (type == 2) {
            handler.spawnPongBall(posX, posY, velX, velY);
        } else if (type == 0) {
            handler.removePong();
        }
    }

}
