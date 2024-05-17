package com.example.smartlockerandroid.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.LogViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderBayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UdpServerThread extends Thread {
    private static final int SERVER_PORT = 9876;
    private boolean running = true;

    MessageReceived messageReceived;

    InetAddress clientAddress;

    private BayViewModel bayViewModel;
    private OrderBayViewModel orderBayViewModel;
    private OrderViewModel orderViewModel;
    private OrderHistoryViewModel orderHistoryViewModel;
    private CourierViewModel courierViewModel;
    private LogViewModel logViewModel;
    LifecycleOwner lifeCycleOwner;
    private Preference preference;
    private PreferenceViewModel preferenceViewModel;

    private static UdpServerThread instance;

    private SerialHelperImpl serialHelper;
    private final Handler mainLooper;
    Context context;
    UdpHelper helper;
    public boolean heartBeatBool = false;
    private Handler handler = new Handler();

    public UdpServerThread(Context context, MessageReceived messageReceived) {
        this.messageReceived = messageReceived;
        this.context = context;
        mainLooper = new Handler(Looper.getMainLooper());
        bayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(BayViewModel.class);
        orderBayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderBayViewModel.class);
        orderViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderViewModel.class);
        orderHistoryViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderHistoryViewModel.class);
        courierViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CourierViewModel.class);
        logViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LogViewModel.class);
        preferenceViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PreferenceViewModel.class);
        lifeCycleOwner = (LifecycleOwner) context;
        preference = preferenceViewModel.getPreference();
        serialHelper = SerialHelperImpl.getInstance(context);
        helper = new UdpHelper();
    }

    public static synchronized UdpServerThread getInstance(Context context, MessageReceived messageReceived) {
        if (instance == null || instance.getState() != State.RUNNABLE) {
            instance = new UdpServerThread(context, messageReceived);
            instance.start();
        }
        return instance;
    }

    public void setViewModelStoreOwner(Context context, MessageReceived messageReceived) {
        this.messageReceived = messageReceived;
        bayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(BayViewModel.class);
        preferenceViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PreferenceViewModel.class);
        orderBayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderBayViewModel.class);
        orderViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderViewModel.class);
        logViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LogViewModel.class);
        orderHistoryViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderHistoryViewModel.class);
        courierViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CourierViewModel.class);
        preference = preferenceViewModel.getPreference();
        lifeCycleOwner = (LifecycleOwner) context;
        serialHelper = SerialHelperImpl.getInstance(context);
        helper = new UdpHelper();
    }


    @Override
    public void run() {
        try {

            DatagramSocket serverSocket = null;
//            if (isPortAvailable(SERVER_PORT)) {
            System.out.println("Port " + SERVER_PORT + " is available. Initializing DatagramSocket...");
            try {
                serverSocket = new DatagramSocket(SERVER_PORT);
                // Use serverSocket for further operations
                System.out.println("DatagramSocket initialized successfully.");
            } catch (SocketException e) {
                System.err.println("Error initializing DatagramSocket: " + e.getMessage());
            }
//            } else {
//                System.err.println("Port " + SERVER_PORT + " is already in use.");
//            }


            byte[] receiveData = new byte[1024];

            while (running) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                JSONObject jsonObject = new JSONObject(receivedMessage);
                Log.e("UDP Server", "Received Something" + jsonObject);

                if (jsonObject.getString("type").equals("message")) {
                    runOnUiThread(() -> {
                        String name = null;
                        try {
                            name = jsonObject.getString("message");
                        } catch (JSONException e) {
                            Log.e("Exception 1", e.getMessage());
                        }
//                        clientAddress = receivePacket.getAddress();
                        messageReceived.onMessageReceived2(name);
                    });
                } else if (jsonObject.getString("type").equals("loading")) {
                    receivebayID(jsonObject.getString("bayId"));
                } else if (jsonObject.getString("type").equals("load")) {
                    receivedloading(jsonObject);
                } else if (jsonObject.getString("type").equals("cancel")) {
                    cancelbays(jsonObject.getString("bayIds"));
                } else if (jsonObject.getString("type").equals("remove")) {
                    receivedremoved(jsonObject);
                } else if (jsonObject.getString("type").equals("reloading")) {
                    receivedreloading(jsonObject);
                } else if (jsonObject.getString("type").equals("pickup")) {
                    Log.e("Pickup Problem", "Received Pickup JSON" + jsonObject);
                    receivedpickup(jsonObject);
                } else if (jsonObject.getString("type").equals("ping")) {
                    heartBeatBool = true;
                    delayHeartbeat(receivePacket.getAddress());
                } else if (jsonObject.getString("type").equals("scan")) {
                    Log.e("UDP Server", "Received Sync");
                    String ownIpAddress = "";
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = interfaces.nextElement();
                        if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                            while (addresses.hasMoreElements()) {
                                InetAddress address = addresses.nextElement();
                                if (address instanceof Inet4Address) {
                                    ownIpAddress = address.getHostAddress();
                                    Log.e("Own IP Address", address.getHostAddress());
                                }
                            }
                        }
                    }
                    InetAddress serverAddress = receivePacket.getAddress();
                    String finalOwnIpAddress = ownIpAddress;


                    runOnUiThread(() -> {
                        try {
                            if (!finalOwnIpAddress.equals(serverAddress.getHostAddress())) {
//                                preference.setClientIp(serverAddress.getHostAddress());
//                                Log.e("Saving IP Address", preference.getClientIp());

                                preferenceViewModel.getPreferenceLiveData().observe(lifeCycleOwner, preference -> {
                                    preference.setClientIp(serverAddress.getHostAddress());
                                    preferenceViewModel.update(preference);
                                });


//                                preferenceViewModel.update(preference);
                            }
                        } catch (Exception e) {
                            Log.e("Exception 2", "" + e.getMessage());
                        }
                    });
                    if (!finalOwnIpAddress.equals(serverAddress.getHostAddress())) {
                        heartBeat(serverAddress);
                        // Assuming you have access to a Handler object associated with the main Looper

                    }
                }

//                String responseMessage = "Sending Hello from UDP server!";
//                byte[] sendData = responseMessage.getBytes();
//
//
//                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
//                        receivePacket.getAddress(), receivePacket.getPort());
//
//
//                serverSocket.send(sendPacket);
            }

//            serverSocket.close();
        } catch (Exception e) {
            Log.e("Exception 3", "" + e.getMessage());
        }
    }

    public void stopServer() {
        running = false;
    }

    public interface MessageReceived {
        void onMessageReceived2(String message);
    }

    private void runOnUiThread(Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(action);
    }

    private void delayHeartbeat(final InetAddress serverIp) {
        handler.postDelayed(() -> new Thread(() -> heartBeat(serverIp)).start(), 500);

    }

    // Every 1 Second


    public void heartBeat(InetAddress serverIp) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setSoTimeout(1000);
            clientSocket.setReuseAddress(true);
            clientSocket.setBroadcast(true);
            JSONObject pingJson = new JSONObject();
            pingJson.put("type", "ping");
            String jsonString = pingJson.toString();
            byte[] sendData = jsonString.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverIp, SERVER_PORT);
            clientSocket.send(sendPacket);
            clientSocket.close();
            heartBeatBool = true;
            sendDataIfThere();
        } catch (Exception e) {
            Log.e("Exception 5", e.getMessage());
            heartBeatBool = false;
        }
    }

    public void sendDataIfThere() {

        List<PickupLog> list = logViewModel.getPickupLogs2();
        Log.e("LOG DATA", "Started the observer");
        if (list != null && list.size() > 0) {
            Log.e("LOG DATA", "Some data found");
            AtomicInteger remainingMessages = new AtomicInteger(list.size());
            AtomicInteger sentMessages = new AtomicInteger(0);

            for (PickupLog p : list) {
                try {
                    InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                    JSONObject pingJson = new JSONObject();
                    pingJson.put("type", p.getType());
                    pingJson.put("orderId", p.getOrderId());
                    pingJson.put("bayIds", p.getBayIds());

                    helper.sendMessage(pingJson, ipAddress);
                    sentMessages.incrementAndGet();

                    p.setStatus(true);
                    logViewModel.update(p);
                } catch (Exception e) {
                    Log.e("Exception 123", "" + e.getMessage());
                }
            }
        } else {
            Log.e("LOG DATA", "No Data Found in Logs");
        }

    }


    private void cancelbays(String data) {
        if (!data.isEmpty()) {

            int[] integerArray = convertCSVToIntArray(data);

            for (int singleBayId : integerArray) {
                Handler handler = new Handler(Looper.getMainLooper());
                Bay bay = bayViewModel.getBayByCalibratedId(singleBayId);
                if (bay == null) {
                    Log.e("What's NULL", "Bay is NULL");
                } else {
                    bay.setLed(BayLightStatus.OFF);
                    bay.setDoorStatus(BayDoorStatus.CLOSE);

                    if (bayViewModel == null) {
                        Log.e("What's NULL", "Bay Viewmodel is NULL");
                    } else {
                        bayViewModel.update(bay);
                        Log.e("What's NULL", "Bay Viewmodel is not NULL");

                        if (serialHelper == null) {
                            Log.e("What's NULL", "Bay Serial is NULL");
                        } else {
                            Log.e("What's NULL", "Bay Serial is not NULL");
                            serialHelper.executeBayAction(bay);
                        }
                    }
                    Log.e("What's NULL", "Bay is not NULL");
                }
            }

            // Print the result int Array
            System.out.print("Bay Array: ");
            for (int digit : integerArray) {
                System.out.print(digit + " ");
            }
            System.out.println();

        } else {
            System.out.println("Empty string");
        }

    }

    public void receivedpickup(JSONObject data) {

        String orderId;
        String bayIds;
        try {
            orderId = data.getString("orderId");
            bayIds = data.getString("bayIds");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        int[] integerArray = convertCSVToIntArray(bayIds);
        Order order = orderViewModel.getOrderById(Long.valueOf(orderId));
        if (order != null) {
            // Print the result int Array
            System.out.print("Bay Array: ");
            Handler handler1 = new Handler(Looper.getMainLooper());
            handler1.post(new Runnable() {
                @Override
                public void run() {


                    order.setStatus(OrderStatus.PICKED);
                    order.setPickedUpAt(System.currentTimeMillis());
                    order.setUpdatedAt(System.currentTimeMillis());

                    OrderHistory orderHistory = new OrderHistory();
                    orderHistory.setOrderId(order.getOrderId());
                    orderHistory.setLoadedByName(order.getLoadedByName());
                    orderHistory.setBarcode(order.getBarcode());
                    orderHistory.setOrderNumber(order.getOrderNumber());
                    orderHistory.setCourierId(order.getCourierId());
                    orderHistory.setNoOfBays(order.getNoOfBays());
                    orderHistory.setStatus(order.getStatus());
                    orderHistory.setLoadedAt(order.getLoadedAt());
                    orderHistory.setReloadedAt(order.getReloadedAt());
                    orderHistory.setCanceledAt(order.getCanceledAt());
                    orderHistory.setUpdatedAt(order.getUpdatedAt());
                    orderHistory.setPickedUpAt(order.getPickedUpAt());
                    orderHistory.setLoadedBy(order.getLoadedBy());
                    orderHistory.setReloadedBy(order.getReloadedBy());
                    orderHistory.setCanceledBy(order.getCanceledBy());


                    orderViewModel.delete(order);
                    orderHistoryViewModel.insert(orderHistory);
                }
            });

            for (int digit : integerArray) {

                Bay bay = bayViewModel.getBayByCalibratedId(digit);
                bay.setStatus(BayStatus.AVAILABLE);

                bayViewModel.update(bay);
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                serialHelper.executeBayAction(bay);


//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
//                            @Override
//                            public void onChanged(Bay bay) {
//                                bay.setStatus(BayStatus.AVAILABLE);
//
//                                bayViewModel.update(bay);
//                                bay.setDoorStatus(BayDoorStatus.CLOSE);
//                                serialHelper.executeBayAction(bay);
//                            }
//                        });
//                    }
//                });

            }
        }

        // after decoding we have to use that information to perform pick order using above infomration.
    }

    private void receivedloading(JSONObject data) {


        String appId;
        String orderNum;
        String orderID;
        String bayIds;
        String username;
        try {
            appId = data.getString("courierName");
            orderNum = data.getString("orderNumber");
            orderID = data.getString("orderId");
            bayIds = data.getString("bays");
            username = data.getString("loadedBy");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        int[] integerArray = convertCSVToIntArray(bayIds);

        System.out.print("Bay Array: ");

        Handler handler1 = new Handler(Looper.getMainLooper());

        final Long[] originalOrderId = new Long[1];
        handler1.post(() -> {
            Long courierId = -1L;
            Courier courierToUpdate = courierViewModel.findCourierByName(appId);
            if (courierToUpdate != null) {
                courierId = courierToUpdate.getCourierId();
            }

            Order order = new Order();
            Log.e("Courier ID Log", String.valueOf(courierId));

            order.setOrderId(Long.valueOf(orderID));
            order.setCourierId(courierId);
            order.setOrderNumber(orderNum);
            order.setStatus(OrderStatus.LOADED);
            order.setLoadedAt(System.currentTimeMillis());
            order.setUpdatedAt(System.currentTimeMillis());
            order.setLoadedByName(username);
            order.setLoadedBy(2L);
            orderViewModel.insert(order).observe(lifeCycleOwner, orderId -> {
                Log.e("From Where Order ID is coming", String.valueOf(orderId));
                originalOrderId[0] = orderId;
            });
        });

        for (int digit : integerArray) {

            Bay bay = bayViewModel.getBayByCalibratedId(digit);
            bay.setDoorStatus(BayDoorStatus.CLOSE);
            bay.setLed(preference.getBayColorWhenFull());
            bay.setStatus(BayStatus.OCCUPIED);

            OrderBay orderBay = new OrderBay();
            orderBay.setOrderId(Long.valueOf(orderID));
            Log.e("-=-=-=-=-", "" + bay.getBayId());
            orderBay.setBayId(bay.getBayId());
            orderBayViewModel.insert(orderBay);

            serialHelper.executeBayAction(bay);

//            Handler handler = new Handler(Looper.getMainLooper());
//
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
//                        @Override
//                        public void onChanged(Bay bay) {
//                            if (bay == null) {
//                                Log.e("What's NULL", "Bay is NULL");
//                            } else {
//
//                                bay.setDoorStatus(BayDoorStatus.CLOSE);
//                                bay.setLed(preference.getBayColorWhenFull());
//                                bay.setStatus(BayStatus.OCCUPIED);
//
//                                OrderBay orderBay = new OrderBay();
//                                orderBay.setOrderId(originalOrderId[0]);
//                                orderBay.setBayId(bay.getBayId());
//                                orderBayViewModel.insert(orderBay);
//
//                                if (bayViewModel == null) {
//                                    Log.e("What's NULL", "Bay Viewmodel is NULL");
//                                } else {
//                                    bayViewModel.update(bay);
//                                    Log.e("What's NULL", "Bay Viewmodel is not NULL");
//
//
//                                    if (serialHelper == null) {
//                                        Log.e("What's NULL", "Bay Serial is NULL");
//                                    } else {
//                                        Log.e("What's NULL", "Bay Serial is not NULL");
//                                        serialHelper.executeBayAction(bay);
//                                    }
//                                }
//                                Log.e("What's NULL", "Bay is not NULL");
//                            }
//
//                        }
//                    });
//                }
//            });
        }


    }

    private void receivebayID(String bayIds) {


        Log.e("Checking Logs", bayIds);
        Handler handler = new Handler(Looper.getMainLooper());

        Bay bay = bayViewModel.getBayByCalibratedId(Integer.valueOf(bayIds));
        bay.setLed(BayLightStatus.GREEN);
        bay.setDoorStatus(BayDoorStatus.CLOSE);
        serialHelper.executeBayAction(bay);
//        handler.post(() -> bayViewModel.getBayByCalibratedId(Integer.valueOf(bayIds))).observe(lifeCycleOwner, new Observer<Bay>() {
//            @Override
//            public void onChanged(Bay bay) {
//                Log.e("Checking Logs", String.valueOf(bay.getBayId()));
//                if (bay == null) {
//                    Log.e("What's NULL", "Bay is NULL");
//                } else {
//                    bay.setLed(BayLightStatus.GREEN);
//                    bay.setDoorStatus(BayDoorStatus.CLOSE);
//
//                    if (bayViewModel == null) {
//                        Log.e("What's NULL", "Bay Viewmodel is NULL");
//                    } else {
//                        bayViewModel.update(bay);
//                        Log.e("What's NULL", "Bay Viewmodel is not NULL");
//
//
//                        if (serialHelper == null) {
//                            Log.e("What's NULL", "Bay Serial is NULL");
//                        } else {
//                            Log.e("What's NULL", "Bay Serial is not NULL");
//                            serialHelper.executeBayAction(bay);
//                        }
//                    }
//                    Log.e("What's NULL", "Bay is not NULL");
//                }
//
//            }
//        }));


        // Print the result int Array
        System.out.print("Bay Array: ");

        System.out.println();


    }

    private void receivedremoved(JSONObject data) {


        String orderId;
        String bayIds;
        try {
            orderId = data.getString("orderId");
            bayIds = data.getString("bayIds");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        int[] integerArray = convertCSVToIntArray(bayIds);

        // Print the result int Array
        System.out.print("Bay Array: ");
        Handler handler1 = new Handler(Looper.getMainLooper());
        Order order = orderViewModel.getOrderById(Long.valueOf(orderId));
        if (order != null) {


            handler1.post(new Runnable() {
                @Override
                public void run() {
                    Order order = orderViewModel.getOrderById(Long.valueOf(orderId));

                    if (order.getStatus() == OrderStatus.OVERDUE) {
                        order.setStatus(OrderStatus.LATE);
                    } else {
                        order.setStatus(OrderStatus.CANCELED);
                    }
                    order.setCanceledAt(System.currentTimeMillis());

                    OrderHistory orderHistory = new OrderHistory();
                    orderHistory.setOrderId(order.getOrderId());
                    orderHistory.setLoadedByName(order.getLoadedByName());
                    orderHistory.setBarcode(order.getBarcode());
                    orderHistory.setOrderNumber(order.getOrderNumber());
                    orderHistory.setCourierId(order.getCourierId());
                    orderHistory.setNoOfBays(order.getNoOfBays());
                    orderHistory.setStatus(order.getStatus());
                    orderHistory.setLoadedAt(order.getLoadedAt());
                    orderHistory.setReloadedAt(order.getReloadedAt());
                    orderHistory.setCanceledAt(order.getCanceledAt());
                    orderHistory.setUpdatedAt(order.getUpdatedAt());
                    orderHistory.setPickedUpAt(order.getPickedUpAt());
                    orderHistory.setLoadedBy(order.getLoadedBy());
                    orderHistory.setReloadedBy(order.getReloadedBy());
                    orderHistory.setCanceledBy(order.getCanceledBy());


                    orderViewModel.delete(order);
                    orderHistoryViewModel.insert(orderHistory);
                }
            });

            for (int digit : integerArray) {
                Bay bay = bayViewModel.getBayByCalibratedId(digit);
                bay.setLed(preference.getBayColorWhenEmpty());
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bayViewModel.update(bay);
                serialHelper.executeBayAction(bay);

//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
//                        @Override
//                        public void onChanged(Bay bay) {
//
//
//                            bay.setLed(preference.getBayColorWhenEmpty());
//                            bay.setDoorStatus(BayDoorStatus.CLOSE);
//                            bayViewModel.update(bay);
//                            serialHelper.executeBayAction(bay);
//                        }
//                    });
//                }
//            });

            }

        } else {
            // Order is not available in the table
            // Handle this case as per your requirements
        }

        // after decoding we have to use that information to perform remove order using above infomration.
    }

    private void receivedreloading(JSONObject data) {
        Log.e("Reloading Data Received", data.toString());

        String orderId;
        String bayIds;
        try {
            orderId = data.getString("orderId");
            bayIds = data.getString("bayIds");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        int[] integerArray = convertCSVToIntArray(bayIds);

        // Print the result int Array
        System.out.print("Bay Array: ");
        for (int digit : integerArray) {
            System.out.print(digit + " ");

        }
        System.out.println();
        Handler handler1 = new Handler(Looper.getMainLooper());
        handler1.post(new Runnable() {
            @Override
            public void run() {
                Order order = orderViewModel.getOrderById(Long.valueOf(orderId));
                order.setReloadedAt(System.currentTimeMillis());
                orderViewModel.update(order);
            }
        });

        for (int digit : integerArray) {
            Bay bay = bayViewModel.getBayByCalibratedId(digit);
            Log.e("Before", "Bay Id" + " " + bay.getCalibratedId() + "  " + bay.getLed());
            bay.setLed(preference.getBayColorWhenReloading());
            serialHelper.executeBayAction(bay);
            Log.e("After", "Bay Id" + " " + bay.getCalibratedId() + "  " + bay.getLed());
            final Handler handler2 = new Handler(Looper.getMainLooper());
            handler2.postDelayed(() -> {

                bay.setLed(preference.getBayColorWhenFull());
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bayViewModel.update(bay);
                serialHelper.executeBayAction(bay);
            }, 10000);

//            Handler handler = new Handler(Looper.getMainLooper());
//            handler.post(() -> bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
//                @Override
//                public void onChanged(Bay bay) {
//
//                    Log.e("Before", "Bay Id" + " " + bay.getCalibratedId() + "  " + bay.getLed());
//                    bay.setLed(preference.getBayColorWhenReloading());
//                    //open bay
//                    serialHelper.executeBayAction(bay);
//                    Log.e("After", "Bay Id" + " " + bay.getCalibratedId() + "  " + bay.getLed());
//                    final Handler handler2 = new Handler(Looper.getMainLooper());
//                    handler2.postDelayed(() -> {
//
//                        bay.setLed(preference.getBayColorWhenFull());
//                        bay.setDoorStatus(BayDoorStatus.CLOSE);
//                        bayViewModel.update(bay);
//                        serialHelper.executeBayAction(bay);
//                    }, 10000);
//
//                    Log.e("End", String.valueOf(bay.getLed()));
//                }
//            }));

        }

    }

    private static int[] convertCSVToIntArray(String csvString) {
        // Split the CSV string based on the comma delimiter
        String[] values = csvString.split(",");

        // Create an int array to store the parsed values
        int[] intArray = new int[values.length];

        // Parse each substring into an integer and add to the array
        for (int i = 0; i < values.length; i++) {
            try {
                intArray[i] = Integer.parseInt(values[i].trim());
            } catch (NumberFormatException e) {
                // Handle the case where a value cannot be parsed as an integer
                System.err.println("Skipping invalid integer: " + values[i]);
            }
        }

        return intArray;
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true); // Optional: Allows the port to be reused
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
