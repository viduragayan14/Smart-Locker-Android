package com.example.smartlockerandroid.utils;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClientThread extends Thread {

    private static final int SERVER_PORT = 9876;
    private String serverIp;
    private String message;
    DatagramSocket clientSocket;
    InetAddress serverAddress;

    public UdpClientThread(String serverIp) {
        this.serverIp = serverIp;
    }

    @Override
    public void run() {
        try {
            clientSocket = new DatagramSocket();

            serverAddress = InetAddress.getByName(serverIp);
            String message = " Sending Hello from UDP client!";


            byte[] sendData = message.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());

            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            clientSocket = new DatagramSocket();

            serverAddress = InetAddress.getByName(serverIp);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);

            Log.e("Sending JSON",""+jsonObject.toString());

//            byte[] sendData = jsonObject.toString().getBytes();
            byte[] sendData = message.toString().getBytes();


            Log.e("Send Data Length",""+sendData.length);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
        } catch (Exception e) {
            Log.e("Hey", "" + e.getMessage());
        }
    }

}
