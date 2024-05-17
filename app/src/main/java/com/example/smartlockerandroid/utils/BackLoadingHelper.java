package com.example.smartlockerandroid.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.smartlockerandroid.BuildConfig;
import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderBayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackLoadingHelper implements SerialInputOutputManager.Listener, LifecycleObserver {
    Context context;
    private static final int READ_WAIT_MILLIS = 2000;
    private static final int WRITE_WAIT_MILLIS = 2000;

    public enum UsbPermission {Unknown, Requested, Granted, Denied}

    private UsbPermission usbPermission = UsbPermission.Unknown;
    private UsbSerialPort usbSerialPort;
    private int deviceId, portNum, baudRate = 115200;
    private boolean withIoManager = true;
    private SerialInputOutputManager usbIoManager;
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private boolean connected = true;
    private Handler mainLooper;
    public boolean heart = false;
    MessageReceived messageReceived;

    List<UsbSerialDriver> driverList = new ArrayList<>();

    public static final String FRONT_CONFIG = "Front";
    public static final String BACK_CONFIG = "Back";

    private BayViewModel bayViewModel;
    private OrderBayViewModel orderBayViewModel;
    private OrderViewModel orderViewModel;
    private OrderHistoryViewModel orderHistoryViewModel;
    private CourierViewModel courierViewModel;
    LifecycleOwner lifeCycleOwner;
    private Preference preference;
    private PreferenceViewModel preferenceViewModel;

    private static BackLoadingHelper instance;

    private SerialHelperImpl serialHelper;

    public BackLoadingHelper(Context context, MessageReceived messageReceived) {
        this.context = context;
        this.messageReceived = messageReceived;
        mainLooper = new Handler(Looper.getMainLooper());
        bayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(BayViewModel.class);
        orderBayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderBayViewModel.class);
        orderViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderViewModel.class);
        orderHistoryViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderHistoryViewModel.class);
        courierViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CourierViewModel.class);
        preferenceViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PreferenceViewModel.class);
        lifeCycleOwner = (LifecycleOwner) context;
        preference = preferenceViewModel.getPreference();
        serialHelper = SerialHelperImpl.getInstance(context);

        if (preference.getConnected()) {
            //disconnect();
            connect();

        }

    }


//    public static synchronized BackLoadingHelper getInstance(Context context, MessageReceived messageReceived) {
//        if (instance == null) {
//            instance = new BackLoadingHelper(context, messageReceived);
//
//        }
//        return instance;
//    }

    public void setViewModelStoreOwner(Context context) {
        bayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(BayViewModel.class);
        preferenceViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(PreferenceViewModel.class);
        orderBayViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderBayViewModel.class);
        orderViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderViewModel.class);
        orderHistoryViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(OrderHistoryViewModel.class);
        courierViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(CourierViewModel.class);
        preference = preferenceViewModel.getPreference();
        lifeCycleOwner = (LifecycleOwner) context;
        serialHelper = SerialHelperImpl.getInstance(context);

    }

    public void setMessageReceived(MessageReceived messageReceived) {
        this.messageReceived = messageReceived;
    }

    public List<UsbSerialDriver> getDrivers() {
        return driverList;
    }

    public void connect() {
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        driverList.addAll(availableDrivers);
        UsbSerialDriver driver = availableDrivers.get(0);
        usbSerialPort = driver.getPorts().get(portNum);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }

        try {
            usbSerialPort.open(usbConnection);
            try {
                usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
            } catch (UnsupportedOperationException e) {
                status("unsupport setparameters");
            }
            if (withIoManager) {
                usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
                usbIoManager.start();
            }
            status("connected");
            connected = true;
            // heartbeat();
            //  controlLines.start();

        } catch (Exception e) {
            status("connection failed: " + e.getMessage());

        }
    }

    public void disconnect() {
        connected = true;
        if (usbIoManager != null) {

            usbIoManager.stop();

        }
        usbIoManager = null;
        try {

            usbSerialPort.close();

            status("Disconnected");
        } catch (IOException ignored) {
        }
        usbSerialPort = null;
    }

    public void status(String str) {

        System.out.println(str);
    }

    public String read() {
        if (!connected) {
            Toast.makeText(context, "not connected", Toast.LENGTH_SHORT).show();
        }
        String receive = "";
        try {
            byte[] buffer = new byte[100000];
            int len = usbSerialPort.read(buffer, READ_WAIT_MILLIS);
//            receive = receive(Arrays.copyOf(buffer, len));
        } catch (IOException e) {
            // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
            // like connection loss, so there is typically no exception thrown here on error
            status("connection lost: " + e.getMessage());
        }
        return receive;
    }

    public String receive(String data) {
        System.out.println("Byte Array: " + data);

        processing(data);

//            String data_string = new String(data, StandardCharsets.UTF_8);
//            if (data.length > 0) {
//                System.out.println("receive " + data.length + " bytes\n");
//                System.out.println("converted" + HexDump.dumpHexString(data) + ("\n"));
//                System.out.println("string data  " + data_string);
//
//
//            } else System.out.println("no data");
        return data;
    }

    public void processing(String data_string) {
        switch (data_string.charAt(0)) {
            case 'a':
                receivedloading(data_string);
                break;
            case 'L':
                receivebayID(data_string);
                break;
            case 'b':
                receivedreloading(data_string);
                break;
            case 'c':
                receivedremoved(data_string);
                break;
            case 'd': //Ascci "c"
                receivedpickup(data_string);
                break;
            case '+':
                mainLooper.post(() -> messageReceived.onMessageReceived(data_string));
                break;
            case 'T': //Ascci "c"
                cancelbays(data_string);
                break;

        }
    }

    private void receivedloading(String data) {

        // Check if the string is not empty
        if (!data.isEmpty()) {
            // Check the first character for the loading array indicator
            if (data.charAt(0) == 'a') {

                // Create a new ASCII string for APP Name

                int appIdStartIndex = 1;
                int appIdEndIndex = data.indexOf('/', appIdStartIndex);
                String appId = data.substring(appIdStartIndex, appIdEndIndex);

                // Print the new string for verification
                System.out.println("APPId: " + appId);

                // Create a new ASCII string for Order Num between '%' symbols
                int orderNumStartIndex = appIdEndIndex + 2;
                int orderNumEndIndex = data.indexOf('%', orderNumStartIndex);
                String orderNum = data.substring(orderNumStartIndex, orderNumEndIndex);

                // Print the new string for verification
                System.out.println("Order Num: " + orderNum);

                // Create a new ASCII string for Order ID between '$' symbols
                int orderIDStartIndex = orderNumEndIndex + 2;
                int orderIDEndIndex = data.indexOf('$', orderIDStartIndex);
                String orderID = data.substring(orderIDStartIndex, orderIDEndIndex);

                // Print the new string for verification
                System.out.println("Order ID: " + orderID);


                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsStartIndex = orderIDEndIndex + 2;
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);

                int[] integerArray = convertCSVToIntArray(bayIds);

                int usernameStartIndex = bayIdsEndIndex + 2;
                int usernameEndIndex = data.indexOf('#', usernameStartIndex);
                String username = data.substring(usernameStartIndex, usernameEndIndex);

                // Print the result int Array
                System.out.print("Bay Array: ");

                Handler handler1 = new Handler(Looper.getMainLooper());

                final Long[] originalOrderId = new Long[1];
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Long courierId = -1L;
                        Courier courierToUpdate = courierViewModel.findCourierByName(appId);
                        if (courierToUpdate != null) {
                            courierId = courierToUpdate.getCourierId();
                        }

                        Order order = new Order();
                        Log.e("Courier ID Log",""+courierId);

                        order.setOrderId(Long.valueOf(orderID));
                        order.setCourierId(courierId);
                        order.setOrderNumber(orderNum);
                        order.setStatus(OrderStatus.LOADED);
                        order.setLoadedAt(System.currentTimeMillis());
                        order.setUpdatedAt(System.currentTimeMillis());
                        order.setLoadedByName(username);
                        order.setLoadedBy(2L);
                        orderViewModel.insert(order).observe(lifeCycleOwner, new Observer<Long>() {
                            @Override
                            public void onChanged(Long orderId) {
                                Log.e("From Where Order ID is coming", "" + orderId);
                                originalOrderId[0] = orderId;
                            }
                        });
                    }
                });

                for (int digit : integerArray) {
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
                                @Override
                                public void onChanged(Bay bay) {
                                    if (bay == null) {
                                        Log.e("What's NULL", "Bay is NULL");
                                    } else {

                                        bay.setDoorStatus(BayDoorStatus.CLOSE);
                                        bay.setLed(preference.getBayColorWhenFull());
                                        bay.setStatus(BayStatus.OCCUPIED);

                                        OrderBay orderBay = new OrderBay();
                                        orderBay.setOrderId(originalOrderId[0]);
                                        orderBay.setBayId(bay.getBayId());
                                        orderBayViewModel.insert(orderBay);

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
                            });
                        }
                    });
                }

            } else {
                System.out.println("Invalid loading array indicator");
            }
        } else {
            System.out.println("Empty string");
        }
    } // DECODING RECEIVED ORDER "a001%orderID%*bayIds*#username#"

    private void receivedreloading(String data) {

        // Check if the string is not empty
        if (!data.isEmpty()) {
            // Check the first character for the loading array indicator
            if (data.charAt(0) == 'b') {
                int OrderIdStartIndex = 1;

                int orderIdEndIndex = data.indexOf('%', OrderIdStartIndex);
                String orderId = data.substring(OrderIdStartIndex, orderIdEndIndex);

                // Print the new string for verification
                System.out.println("Order ID: " + orderId);

                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsStartIndex = orderIdEndIndex + 2;
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);

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
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
                                @Override
                                public void onChanged(Bay bay) {

                                    Log.e("Before", "" + bay.getLed());
                                    bay.setLed(preference.getBayColorWhenReloading());
                                    //open bay
                                    serialHelper.executeBayAction(bay);

                                    Log.e("After", "" + bay.getLed());
                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(() -> {

                                        bay.setLed(preference.getBayColorWhenFull());
                                        bay.setDoorStatus(BayDoorStatus.CLOSE);
                                        bayViewModel.update(bay);
                                        serialHelper.executeBayAction(bay);
                                    }, 10000);

                                    Log.e("End", "" + bay.getLed());
                                }
                            });
                        }
                    });

                }

            } else {
                System.out.println("Invalid reloading array indicator");
            }


        } else {
            System.out.println("Empty string");
        }


        // after decoding we have to use that information to create an reloading senario for the with above information.
    } // DECODING RELOADING "b001%orderID%*bayIds*#username#"

    private void receivedremoved(String data) {
        // Check if the string is not empty
        if (!data.isEmpty()) {
            // Check the first character for the loading array indicator
            if (data.charAt(0) == 'c') {
                int OrderIdStartIndex = 1;
                int orderIdEndIndex = data.indexOf('%', OrderIdStartIndex);
                String orderId = data.substring(OrderIdStartIndex, orderIdEndIndex);

                // Print the new string for verification
                System.out.println("Order ID: " + orderId);

                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsStartIndex = orderIdEndIndex + 2;
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);

                int[] integerArray = convertCSVToIntArray(bayIds);

                // Print the result int Array
                System.out.print("Bay Array: ");
                Handler handler1 = new Handler(Looper.getMainLooper());
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
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
                                @Override
                                public void onChanged(Bay bay) {


                                    bay.setLed(preference.getBayColorWhenEmpty());
                                    bay.setDoorStatus(BayDoorStatus.CLOSE);
                                    bayViewModel.update(bay);
                                    serialHelper.executeBayAction(bay);
                                }
                            });
                        }
                    });

                }
            } else {
                System.out.println("Invalid remove array indicator");
            }
        } else {
            System.out.println("Empty string");
        }
        // after decoding we have to use that information to perform remove order using above infomration.
    } // DECODING REMOVING "c001%orderID%*bayIds*#username#"

    public void receivedpickup(String data) {
        // Check if the string is not empty
        if (!data.isEmpty()) {
            // Check the first character for the loading array indicator
            if (data.charAt(0) == 'd') {
                int OrderIdStartIndex = 1;
                int orderIdEndIndex = data.indexOf('%', OrderIdStartIndex);
                String orderId = data.substring(OrderIdStartIndex, orderIdEndIndex);

                // Print the new string for verification
                System.out.println("Order ID: " + orderId);

                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsStartIndex = orderIdEndIndex + 2;
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);

                int[] integerArray = convertCSVToIntArray(bayIds);

                // Print the result int Array
                System.out.print("Bay Array: ");
                Handler handler1 = new Handler(Looper.getMainLooper());
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Order order = orderViewModel.getOrderById(Long.valueOf(orderId));

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
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bayViewModel.getBayById((long) digit).observe(lifeCycleOwner, new Observer<Bay>() {
                                @Override
                                public void onChanged(Bay bay) {
                                    bay.setStatus(BayStatus.AVAILABLE);

                                    bayViewModel.update(bay);
                                    bay.setDoorStatus(BayDoorStatus.OPEN);
                                    serialHelper.executeBayAction(bay);
                                }
                            });
                        }
                    });

                }
            } else {
                System.out.println("Invalid remove array indicator");
            }
        } else {
            System.out.println("Empty string");
        }
        // after decoding we have to use that information to perform pick order using above infomration.
    }

    private void receivebayID(String data) {
        if (!data.isEmpty()) {

            if (data.charAt(0) == 'L') {
                int bayIdsStartIndex = 1;
                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);

                Log.e("Checking Logs", "" + bayIds);
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        bayViewModel.getBayById(Long.valueOf(bayIds)).observe(lifeCycleOwner, new Observer<Bay>() {
                            @Override
                            public void onChanged(Bay bay) {
                                Log.e("Checking Logs", "" + bay.getBayId());
                                if (bay == null) {
                                    Log.e("What's NULL", "Bay is NULL");
                                } else {
                                    bay.setLed(BayLightStatus.GREEN);
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
                        });
                    }
                });


                // Print the result int Array
                System.out.print("Bay Array: ");

                System.out.println();
            } else {
                System.out.println("Invalid loading array indicator");
            }
        } else {
            System.out.println("Empty string");
        }

    }

    private void cancelbays(String data) {
        if (!data.isEmpty()) {

            if (data.charAt(0) == 'T') {
                int bayIdsStartIndex = 1;
                // Create a new ASCII string for Bay IDs between '*' symbols
                int bayIdsEndIndex = data.indexOf('*', bayIdsStartIndex);
                String bayIds = data.substring(bayIdsStartIndex, bayIdsEndIndex);
                int[] integerArray = convertCSVToIntArray(bayIds);

                for (int singleBayId : integerArray) {
                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            bayViewModel.getBayById((long) singleBayId).observe(lifeCycleOwner, new Observer<Bay>() {
                                @Override
                                public void onChanged(Bay bay) {
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
                            });
                        }
                    });
                }

                // Print the result int Array
                System.out.print("Bay Array: ");
                for (int digit : integerArray) {
                    System.out.print(digit + " ");
                }
                System.out.println();
            } else {
                System.out.println("Invalid loading array indicator");
            }
        } else {
            System.out.println("Empty string");
        }

    }


    public String sendbayID(int bay) {
        String send = "L" + Integer.toString(bay) + "*";

        return send;
    }

    public String sendloading(int appID, int orderID, int bays, String username) {
        String send = "a" + appID + "%" + orderID + "%" + "*" + bays + "*" + "#" + username + "#";

        return send;
    }

    public String sendreloading(int appID, int orderID, int bays, String username) {
        String send = "b" + appID + "%" + orderID + "%" + "*" + bays + "*" + "#" + username + "#";

        return send;
    }

    public String sendremoving(int appID, int orderID, int bays, String username) {
        String send = "c" + appID + "%" + orderID + "%" + "*" + bays + "*" + "#" + username + "#";

        return send;
    }

    public String sendpickup(int appID, int orderID, int bays, String username) {
        String send = "d" + appID + "%" + orderID + "%" + "*" + bays + "*" + "#" + username + "#";

        return send;
    }

    public String donepickup(int appID, int orderID, int bays) {
        String send = "d" + appID + "%" + orderID + "%" + "*" + bays + "*";
        return send;
    }

    public void checkcom(String data_send) {
        try {
            Thread.sleep(200);
            send("TX@");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        heartbeat();
        if (heart == true) {
            send(data_send);
            System.out.println("heart beat okay recieved and data send");
            heart = false;
        } else System.out.println("Front is unresposive");
    }


    public void heartbeat() {
        int count = 0;
        do {
            String beat = read();
            System.out.println("beat " + beat);
            if (beat != null && !beat.isEmpty() && beat.substring(0, 2).equals("RX")) {
                heart = true;
                System.out.println("heart " + heart);
                break;
            } else count++;
        } while (count <= 10);
    }

    private static int[] convertStringToIntArray(String str) {
        int[] resultArray = new int[str.length()];

        for (int i = 0; i < str.length(); i++) {
            char digitChar = str.charAt(i);
            // Convert the character digit to an integer
            int digit = Character.getNumericValue(digitChar);
            resultArray[i] = digit;
        }

        return resultArray;
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


    public void send(String str) {
        try {
            Log.e("kim log", "Send is Called");
            Log.e("kim log", "Data " + str);
            if (!connected) {
                Log.e("kim log", "Not Connected Called");
                return;
            }
            byte[] data = (str + '\n').getBytes();
            Log.e("kim log", "Sending" + Arrays.toString(data));
            usbSerialPort.write(data, WRITE_WAIT_MILLIS);
        } catch (Exception e) {
            Log.e("kim log", "Exception" + e.getMessage());
            onRunError(e);

        }
    }

    private StringBuilder dataBuffer = new StringBuilder();

    @Override
    public void onNewData(byte[] data) {
        String receivedData = new String(data, StandardCharsets.UTF_8);

        // Remove all whitespace characters (including newlines) from the received data
        receivedData = receivedData.replaceAll("\\n", "");

        dataBuffer.append(receivedData);

        Log.e("Data Received", "New Data Received: " + receivedData);

        // Check if the buffer contains the delimiter
        int messageEndIndex = dataBuffer.indexOf("@");

        if (messageEndIndex != -1) {
            // Extract the complete message without including the delimiter
            String completeMessage = dataBuffer.substring(0, messageEndIndex);
            Log.e("Complete Message", completeMessage);

            // Process the complete message
            receive(completeMessage);

            // Remove the processed message and the delimiter from the buffer
            dataBuffer.delete(0, messageEndIndex + 1);
        }
    }

    @Override
    public void onRunError(Exception e) {
        System.out.println(e);
    }

    public interface MessageReceived {
        void onMessageReceived(String message);
    }
}
