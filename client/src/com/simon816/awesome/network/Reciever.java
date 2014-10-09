package com.simon816.awesome.network;

import java.io.DataInputStream;
import java.io.IOException;
import com.simon816.awesome.network.packet.Packet;

public class Reciever implements Runnable {

    private DataInputStream dis;
    private Packet[] stack;
    private NetHandler handler;
    private boolean alive;
    private static long lastRecv;

    public Reciever(DataInputStream dataInputStream) {
        dis = dataInputStream;
        resetStack();
    }

    public void processNext(NetHandler handler) {
        if (stack.length == 0) {
            return;
        }
        for (int i = 0; i < stack.length; i++) {
            if (stack[i] != null) {
                System.out.println("Proccessing " + stack[i]);
                stack[i].process(handler);
                stack[i] = null;
            }
        }
        resetStack();
    }

    private void resetStack() {
        stack = new Packet[0];
    }

    @Override
    public void run() {
        alive = true;
        try {
            while (alive) {
                recieveStream();
                lastRecv = System.currentTimeMillis();
                processNext(handler);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            handler.recvFail(this);
        }
    }

    private void recieveStream() throws IOException {
        int size;
        try {
            size = dis.read() & 0xFF;
        } catch (IOException e) {
            throw e;
        }
        Packet[] newStack = new Packet[stack.length + size];
        System.arraycopy(stack, 0, newStack, 0, stack.length);
        Packet p;
        for (int i = 0; i < size; i++) {
            p = Packet.getNewPacket(new Integer(dis.read() & 0xFF));
            System.out.println("Incomming " + p);
            p.readData(dis);
            newStack[stack.length + i] = p;
        }
        stack = newStack;
    }

    public void setHandler(NetHandler netHandler) {
        handler = netHandler;
    }

    public void kill(Thread hostingThread) {
        alive = false;
        hostingThread.interrupt();
        System.out.println("interrupt recv thread");
        System.out.println("killed");
    }

    public static int lastRecieved() {
        return (int) (System.currentTimeMillis() - lastRecv);
    }

}
