package com.simon816.awesome.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import com.simon816.awesome.network.NetHandler;

public abstract class Packet {
    private static final Hashtable<Integer, Class<? extends Packet>> IdToClass = new Hashtable<Integer, Class<? extends Packet>>();
    private static final Hashtable<Class<? extends Packet>, Integer> ClassToId = new Hashtable<Class<? extends Packet>, Integer>();

    private static void registerPacket(Class<? extends Packet> p, int id) {
        IdToClass.put(new Integer(id), p);
        ClassToId.put(p, new Integer(id));
    }

    public abstract void writeData(DataOutputStream dos) throws IOException;

    public abstract void readData(DataInputStream dis) throws IOException;

    public abstract void process(NetHandler handler);

    public static Packet getNewPacket(Integer id) throws IOException {
        try {
            if (!IdToClass.containsKey(id)) {
                throw new IOException("Unknown Packet ID " + id);
            }
            return IdToClass.get(id).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toString() {
        Class<? extends Packet> cls = this.getClass();
        return "[" + cls.getSimpleName() + "] ID: " + ClassToId.get(cls);
    }

    public final int getPacketId() {
        return ClassToId.get(this.getClass()).intValue();
    }

    static {
        registerPacket(PacketEstablish.class, 1);
        registerPacket(PacketNeighbourUpdate.class, 2);
        registerPacket(PacketSetText.class, 3);
        registerPacket(PacketShowColor.class, 4);
        registerPacket(PacketSendCircle.class, 5);
        registerPacket(PacketRandom.class, 6);
        registerPacket(PacketPong.class, 7);
        registerPacket(PacketIsAlive.class, 254);
        registerPacket(PacketLostConnection.class, 255);
    }
}
