package com.example.smartlockerandroid.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.dao.BayDao;
import com.example.smartlockerandroid.data.dao.CourierDao;
import com.example.smartlockerandroid.data.dao.OrderBayDao;
import com.example.smartlockerandroid.data.dao.OrderDao;
import com.example.smartlockerandroid.data.dao.OrderHistoryDao;
import com.example.smartlockerandroid.data.dao.PickupLogDao;
import com.example.smartlockerandroid.data.dao.PreferenceDao;
import com.example.smartlockerandroid.data.dao.UserDao;
import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.CourierStatus;
import com.example.smartlockerandroid.data.enums.KeyboardInputMethod;
import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.utils.BitmapConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author itschathurangaj on 5/5/23
 */
@Database(entities = {User.class, Courier.class, Bay.class, Order.class, OrderHistory.class, OrderBay.class, Preference.class, PickupLog.class}, version = 1, exportSchema = false)
@TypeConverters({BitmapConverter.class})
public abstract class SmartLockerDatabase extends RoomDatabase {
    private static final int NUMBER_OF_THREADS = 5;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile SmartLockerDatabase INSTANCE;
    private static Context appContext;
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);

            databaseWriteExecutor.execute(() -> {

                //default preferences
                PreferenceDao preferenceDao = INSTANCE.preferenceDao();
                preferenceDao.insert(new Preference(
                        1L,
                        "START PICK-UP",
                        "Select How the Order was Placed",
                        "0.2",
                        false, false, 60000L, 60000L,
                        BayLightStatus.RED,
                        "Please contact a Staff member",
                        BayLightStatus.OFF,
                        BayLightStatus.GREEN,
                        BayLightStatus.YELLOW,
                        10000L,
                        BayLightStatus.RED,
                        false,
                        10000L,
                        BayLightStatus.RED,
                        false,
                        4,
                        4, false, "Back", null, null
                ));

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                String currentDate = sdf.format(currentTime);
                UserDao userDao = INSTANCE.userDao();
                userDao.insert(new User(1L, "supusr", "supusr", UserRole.SUPER_ADMIN, "7513", "7513", true, currentDate));
                userDao.insert(new User(2L, "admin", "admin", UserRole.ADMIN, "1111", "1111", true, currentDate));

                BayDao bayDao = INSTANCE.bayDao();
                bayDao.insert(new Bay(BayDoorStatus.CLOSE, BayStatus.AVAILABLE, BayLightStatus.OFF, 1, 1));
                bayDao.insert(new Bay(BayDoorStatus.CLOSE, BayStatus.AVAILABLE, BayLightStatus.OFF, 2, 2));
                bayDao.insert(new Bay(BayDoorStatus.CLOSE, BayStatus.AVAILABLE, BayLightStatus.OFF, 3, 3));
                bayDao.insert(new Bay(BayDoorStatus.CLOSE, BayStatus.SERVICE_BAY, BayLightStatus.GREEN, 4, 4)); // service bay

                //default couriers
                CourierDao courierDao = INSTANCE.courierDao();
                courierDao.insert(new Courier(1L, "uberEats", "Uber Eats", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.uber_eats, 0));
                courierDao.insert(new Courier(2L, "grubhub", "Grubhub", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.grubhub, 1));
                courierDao.insert(new Courier(3L, "doordash", "Doordash", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.doordash, 2));
                courierDao.insert(new Courier(4L, "postmates", "Postmates", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.postmates, 3));
                courierDao.insert(new Courier(5L, "toasttakeout", "Toast Takeout", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.toasttakeout, 4));
                courierDao.insert(new Courier(6L, "chownow", "Chow Now", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.chownow, 5));
                courierDao.insert(new Courier(7L, "onlineorder", "Call in Order", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.call_in_order, 6));
                courierDao.insert(new Courier(8L, "callinorder", "Online Order", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.online_order, 7));
                courierDao.insert(new Courier(9L, "pickup", "Pickup", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, appContext, R.drawable.pickup_logo, 8));
                courierDao.insert(new Courier(10L, "asap", "ASAP", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.asap, 9));
                courierDao.insert(new Courier(11L, "slimChicken", "Slim Chicken", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.slim_chicken, 10));
                courierDao.insert(new Courier(12L, "biteSquad", "Bite Squad", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.bite_squad, 11));
                courierDao.insert(new Courier(13L, "google", "Google", CourierStatus.ACTIVE, KeyboardInputMethod.ALPHANUMERIC, appContext, R.drawable.google, 12));

            });
        }
    };

    public static SmartLockerDatabase getDatabase(final Context context) {
        appContext = context;
        if (INSTANCE == null) {
            synchronized (SmartLockerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    SmartLockerDatabase.class,
                                    "smart_locker"
                            )
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract UserDao userDao();

    public abstract CourierDao courierDao();

    public abstract OrderDao orderDao();

    public abstract OrderHistoryDao orderHistoryDao();

    public abstract BayDao bayDao();

    public abstract OrderBayDao orderBayDao();

    public abstract PreferenceDao preferenceDao();
    public abstract PickupLogDao logDao();
}
