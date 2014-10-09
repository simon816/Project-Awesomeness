package com.simon816.awesome.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import com.simon816.awesome.network.NetHandler;
import com.simon816.awesome.network.packet.PacketEstablish;
import com.simon816.awesome.visual.FXUI;

public class Main {

    protected static NetHandler netHandle;
    private static Thread connectThread;
    public static String compName;
    private static FXUI ui = null;

    public static void main(String[] args) {
        final List<String> argList = Arrays.asList(args);
        connectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ui != null) {
                        ui.setErrorColor();
                    }
                    String[] socket;
                    if (argList.contains("--ip")) {
                        socket = argList.get(argList.indexOf("--ip") + 1).split(":");
                    } else {
                        socket = getServerIP().split(":");
                    }
                    if (socket.length < 2) {
                        throw new MalformedURLException("Bad response from website");
                    }
                    System.out.println("Got ip as " + socket[0] + ":" + socket[1]);
                    netHandle = new NetHandler(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]));
                    if (ui != null) {
                        ui.setPrepareColor();
                    }
                    netHandle.connectForever();
                    if (ui != null) {
                        ui.setReadyColor();
                    }
                } catch (UnknownHostException | MalformedURLException e) {
                    System.err.println(e.getMessage());
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    run();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        connectThread.setName("Network-Connection");
        connectThread.setDaemon(true);
        try {
            compName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            compName = "";
        }
        if ("".equals(compName) || argList.contains("--askName")) {
            compName = JOptionPane.showInputDialog("Enter Computer Name", compName + "-00");
            if (compName == null || "".equals(compName)) {
                return;
            }
        }
        connectThread.start();
        FXUI.launch(FXUI.class, args);
    }

    public static void uiReady(final FXUI ui) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Main.ui = ui;
                ui.setPrepareColor();
                try {
                    connectThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                ui.setReadyColor();
                netHandle.sendPacket(new PacketEstablish());
                new Actions(ui, netHandle);
            }
        });
        t.setName("Network-Wait");
        t.setDaemon(true);
        t.start();
    }

    private static String getServerIP() throws IOException {
        URL url = new URL("http://simon816.hostzi.com/dev/awesome.php?action=getHostIP");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(true);
        InputStream input = connection.getInputStream();
        int len;
        try {
            len = Integer.parseInt(connection.getHeaderField("Content-Length"));
        } catch (NumberFormatException e) {
            len = 100;
        }
        byte[] buf = new byte[len];
        input.read(buf);
        connection.disconnect();
        input.close();
        return new String(buf);
    }
}
