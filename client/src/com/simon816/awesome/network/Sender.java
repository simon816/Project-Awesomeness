package com.simon816.awesome.network;

import java.io.DataOutputStream;
import java.io.IOException;

import com.simon816.awesome.network.packet.Packet;

public class Sender implements Runnable {

    private DataOutputStream dos;

    public Sender(DataOutputStream dataOutputStream) {
        dos = dataOutputStream;
        resetQueue();
    }

    private Packet[] queue;
    private NetHandler transporter;
    private boolean alive;
    private int prevTick;

    private void resetQueue() {
        queue = new Packet[0];
    }

    @Override
    public void run() {
        alive = true;
        try {
            int i = 0;
            prevTick = 0;
            while (alive) {
                if (queue.length > 0) {
                    for (i = 0; i < queue.length; i++) {
                        writeToStream(queue[i]);
                    }
                    resetQueue();
                    dos.flush();
                } else {
                    Thread.sleep(50);
                    if (Reciever.lastRecieved() > 6000) {
                        prevTick += 1;
                    }
                }
                if (prevTick >= 150) {
                    prevTick = 0;
                    transporter.tickAlive();
                }
            }
        } catch (Exception e) {
            if (transporter != null) {
                transporter.sendFail(this);
            }
        }
    }

    private void writeToStream(Packet packet) throws IOException {
        if (packet == null) {
            return;
        }
        System.out.println("Outgoing " + packet);
        dos.write(packet.getPacketId());
        packet.writeData(dos);
    }

    public void sendPacket(Packet packet, NetHandler transporter) {
        this.transporter = transporter;
        Packet[] newQueue = new Packet[queue.length + 1];
        System.arraycopy(queue, 0, newQueue, 0, queue.length);
        queue = newQueue;
        queue[queue.length - 1] = packet;
    }

    public void kill(Thread hostingThread) {
        alive = false;
        try {
            System.out.println("join send thread");
            hostingThread.join(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (hostingThread.isAlive()) {
            hostingThread.interrupt();
        }
        System.out.println("killed");
    }
}