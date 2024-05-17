package com.example.smartlockerandroid.utils;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class NetworkScanner {

    public static List<String> scanForServers(int port, int timeout) {
        List<String> servers = new ArrayList<>();
        try {
            // Scan common IP ranges (adjust as needed)
            for (int i = 1; i <= 255; i++) {
                String ipAddress = "192.168.1." + i; // Adjust the IP range as needed
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (inetAddress.isReachable(timeout)) {
                    if (isServerListening(inetAddress, port, timeout)) {
                        servers.add(ipAddress);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servers;
    }

    public void checkNetwork(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network[] networks = connectivityManager.getAllNetworks();

        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                try {
                    LinkProperties linkProperties = connectivityManager.getLinkProperties(network);


//                    Log.e("Checking",""+linkProperties.getLinkAddresses().size());
//                    Log.e("Checking",""+linkProperties.getLinkAddresses().size());
                    for (LinkAddress address : linkProperties.getLinkAddresses()) {
                        Log.e("Ethernet IP Address", address.getAddress().getHostAddress());
                    }
                } catch (Exception e) {
                    Log.e("Exception", e.getMessage());
                }
                break; // Only handle the first Ethernet network found, adjust as needed.
            }
        }
    }
    public void changeEthernetIpAddress(Context context, String newIpAddress, String newGateway, String newDnsServer) {
        try {
            Log.e("Hy","Reached Here");
            Process process = Runtime.getRuntime().exec("su"); // Get root access
            DataOutputStream outputStream = new DataOutputStream(process.getOutputStream());
            Log.e("Hy","Reached Here 1");
            // Build the command to set the new IP address, gateway, and DNS server
            String command = "ifconfig eth0 " + newIpAddress + " netmask 255.255.255.0\n" +
                    "route add default gw " + newGateway + "\n" +
                    "setprop net.dns1 " + newDnsServer + "\n";
            Log.e("Hy","Reached Here 2");
            outputStream.writeBytes(command);
            outputStream.writeBytes("exit\n");
            outputStream.flush();
            Log.e("Hy","Reached Here 3");
            // Wait for the process to finish
            process.waitFor();
            Log.e("Hy","Reached Here 4");
            // Close streams
            outputStream.close();
        } catch (IOException | InterruptedException e) {
            Log.e("Exception",""+e.getMessage());
        }
    }
    private static boolean isServerListening(InetAddress address, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(address, port), timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
