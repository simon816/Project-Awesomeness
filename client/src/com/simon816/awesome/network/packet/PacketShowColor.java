package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketShowColor extends Packet {

    private boolean setColor;
    private int r;
    private int g;
    private int b;

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        setColor = dis.readBoolean();
        if (setColor) {
            r = dis.read() & 0xFF;
            g = dis.read() & 0xFF;
            b = dis.read() & 0xFF;
        }
    }

    @Override
    public void process(NetHandler handler) {
        if (setColor)
            handler.setColor(r, g, b);
        else
            handler.removeColor();
    }

}
