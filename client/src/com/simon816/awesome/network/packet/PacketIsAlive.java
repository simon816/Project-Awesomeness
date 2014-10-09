package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketIsAlive extends Packet {

    private int neighbours;

    public PacketIsAlive() {
    }

    public PacketIsAlive(int actualNeighbours) {
        neighbours = actualNeighbours;
    }

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        dos.write(neighbours);
    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(NetHandler handler) {
        // TODO Auto-generated method stub

    }

}
