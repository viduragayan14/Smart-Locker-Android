package com.example.smartlockerandroid.utils;

import android.util.Log;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class UdpHelper {

    private static final int SERVER_PORT = 9876;
    DatagramSocket clientSocket;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UdpHelper() {
//        try {
//            clientSocket = new DatagramSocket();
//            clientSocket.setReuseAddress(true);
//            clientSocket.setBroadcast(true);
//        } catch (Exception e) {
//            Log.e("Exception", e.getMessage());
//        }
    }


    public void sendMessage(final JSONObject jsonObject, InetAddress serverIp) {
        executor.submit(() -> sendMessageMethod(jsonObject, serverIp));
    }

    public void sendMessageToClient(final JSONObject message, final InetAddress clientIp) {
        executor.submit(() -> sendMessageToClientMethod(message, clientIp));
    }

    public void scanNetwork(final JSONObject message) {
        executor.submit(() -> scanNetworkMethod(message));
    }

    public Future<Boolean> isReachable(InetAddress address) {
        return executor.submit(() -> isReachableMethod(address));
    }

    public void sendMessageMethod(JSONObject jsonObject, InetAddress serverIp) {
        try {
            clientSocket = new DatagramSocket();
            clientSocket.setReuseAddress(true);
            clientSocket.setBroadcast(true);
            byte[] sendData = jsonObject.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIp, SERVER_PORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }



    public void sendMessageToClientMethod(JSONObject jsonObject, InetAddress clientIp) {
        try {
            Log.e("UDP Log", "Sending Message From Here");
            clientSocket = new DatagramSocket();
            clientSocket.setReuseAddress(true);
            clientSocket.setBroadcast(true);
            byte[] sendData = jsonObject.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIp, SERVER_PORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public void scanNetworkMethod(JSONObject jsonObject) {
        try {
            clientSocket = new DatagramSocket();
            clientSocket.setReuseAddress(true);
            clientSocket.setBroadcast(true);
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            byte[] sendData = jsonObject.toString().getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, SERVER_PORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public static boolean isReachableMethod(InetAddress address) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(2000);
            JSONObject pingJson = new JSONObject();
            pingJson.put("type", "ping");
            String jsonString = pingJson.toString();
            byte[] sendData = jsonString.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, SERVER_PORT);
            socket.send(sendPacket);

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket); // Wait for a response
            return true;
        } catch (Exception e) {
            // Handle exception, e.g., if the timeout is reached or the address is unreachable
            Log.e("Exception",""+e.getMessage());
            return false;
        }
    }
}
