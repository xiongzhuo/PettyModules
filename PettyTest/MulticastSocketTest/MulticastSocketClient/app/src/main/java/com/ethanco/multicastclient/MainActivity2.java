package com.ethanco.multicastclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainActivity2 extends AppCompatActivity {

    private static final String TAG = "Z-Client";
    private static final String SERVICE_IP_2 = "224.0.0.1";
    private static int SERVICE_PORT_2 = 7305;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "========== start ============");
        new Thread() {
            @Override
            public void run() {
                try {
                    sendMultiBroadcast();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void sendMultiBroadcast() throws IOException {
        Log.i(TAG, "sendMultiBroadcast...");
        /*
         * 实现多点广播时，MulticastSocket类是实现这一功能的关键，当MulticastSocket把一个DatagramPacket发送到多点广播IP地址，
         * 该数据报将被自动广播到加入该地址的所有MulticastSocket。MulticastSocket类既可以将数据报发送到多点广播地址，
         * 也可以接收其他主机的广播信息
         */
       /* MulticastSocket socket = new MulticastSocket(8600);
        //IP协议为多点广播提供了这批特殊的IP地址，这些IP地址的范围是224.0.0.0至239.255.255.255
        InetAddress address = InetAddress.getByName("224.0.0.1");*/

        MulticastSocket socket = new MulticastSocket(SERVICE_PORT_2);
        InetAddress address = InetAddress.getByName(SERVICE_IP_2);
        /*
         * 创建一个MulticastSocket对象后，还需要将该MulticastSocket加入到指定的多点广播地址，
         * MulticastSocket使用jionGroup()方法来加入指定组；使用leaveGroup()方法脱离一个组。
         */
        socket.joinGroup(address);

        /*
         * 在某些系统中，可能有多个网络接口。这可能会对多点广播带来问题，这时候程序需要在一个指定的网络接口上监听，
         * 通过调用setInterface可选择MulticastSocket所使用的网络接口；
         * 也可以使用getInterface方法查询MulticastSocket监听的网络接口。
         */
        //socket.setInterface(address);

        DatagramPacket packet;
        //发送数据包
        Log.i(TAG, "send packet");
        //byte[] buf = "{\"gwID\":\"50294D10332D\",\"gwIP\":\"192.168.1.110\",\"gwPort\":\"7080\",\"cmd\":\"wlinkwulian\"}".getBytes();
        byte[] buf = "{\"key\":\"wlinkwulian\",\"gwID\":\"50294D10332D\",\"gwIP\":\"192.168.1.110\",\"gwPort\":7080}".getBytes();
        //packet = new DatagramPacket(buf, buf.length, address, 8601);
        packet = new DatagramPacket(buf, buf.length, address, SERVICE_PORT_2);
        socket.send(packet);

        //接收数据
        Log.i(TAG, "receiver packet");
        byte[] rev = new byte[512];
        packet = new DatagramPacket(rev, rev.length);
        socket.receive(packet);
        Log.i(TAG, "get data = " + new String(packet.getData()).trim());    //不加trim，则会打印出512个byte，后面是乱码

        //退出组播
        socket.leaveGroup(address);
        socket.close();
    }
}
