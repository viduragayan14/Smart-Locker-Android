package com.example.smartlockerandroid.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class DatabaseBackupUtil {

    // Change to your Room database class
    private SmartLockerDatabase roomDatabase;

    public DatabaseBackupUtil(Context context) {
        // Initialize your Room database instance
        roomDatabase = SmartLockerDatabase.getDatabase(context);

    }

    public void backupDatabaseToJson() {
        // Retrieve data from all DAOs
        LiveData<List<Bay>> bays = roomDatabase.bayDao().getAllBaysMain();
        LiveData<List<OrderWithCourierAndBays>> orders = roomDatabase.orderDao().getAllOrdersMain();
        LiveData<List<OrderBay>> order_bay = roomDatabase.orderBayDao().getAllOrderBayMain();
        // Repeat for other DAOs

        // Convert data from all DAOs to JSON
        Gson gson = new Gson();
        String jsonEntities1 = gson.toJson(bays);
        String jsonEntities2 = gson.toJson(orders);
        String jsonEntities3 = gson.toJson(order_bay);
        // Repeat for other DAOs

        // Merge JSON data from all DAOs
        StringBuilder jsonDataBuilder = new StringBuilder();
        jsonDataBuilder.append("{");
        jsonDataBuilder.append("\"bays\":").append(jsonEntities1).append(",");
        jsonDataBuilder.append("\"orders\":").append(jsonEntities2).append(",");
        jsonDataBuilder.append("\"orderbays\":").append(jsonEntities3);
        // Repeat for other DAOs
        jsonDataBuilder.append("}");

        // Write merged JSON data to file
        Log.e("Databse Backup",""+jsonDataBuilder.toString());
//        writeJsonToFile(jsonDataBuilder.toString());
    }
}
