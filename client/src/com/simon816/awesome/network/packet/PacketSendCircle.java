package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketSendCircle extends Packet {

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void process(NetHandler handler) {
        handler.showCircle();
    }

}
