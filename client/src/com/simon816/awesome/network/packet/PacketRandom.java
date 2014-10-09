package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketRandom extends Packet {

    private int triggerSide;

    public PacketRandom() {
    }

    public PacketRandom(int side) {
        triggerSide = side;
    }

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        dos.write(triggerSide);
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(NetHandler handler) {
        handler.randomStuff();
    }

}
