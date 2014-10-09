package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.NetHandler;

public class PacketSetText extends Packet {

    private boolean showText;
    private String text;

    @Override
    public void writeData(DataOutputStream dos) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void readData(DataInputStream dis) throws IOException {
        showText = dis.readBoolean();
        if (showText) {
            text = dis.readUTF();
        }
    }

    @Override
    public void process(NetHandler handler) {
        if (showText) {
            handler.showText(text);
        } else {
            handler.hideText();
        }
    }

}
