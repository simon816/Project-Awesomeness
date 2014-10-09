package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketNeighbourUpdate extends Packet {

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    private int neighbours;

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        neighbours = dis.read();
    }

    @Override
    public void process(NetHandler handler) {
        handler.updateNeighbours(neighbours);
    }

}
