package ando.dialog.sample.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://github.com/Tencent/QMUI_Android
 *
 * @author cginechen
 * @date 2016-08-11
 */
@SuppressLint("PrivateApi")
public class QMUIDeviceHelper {
    private final static String TAG = "QMUIDeviceHelper";
    private final static String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_FLYME_VERSION_NAME = "ro.build.display.id";
    private final static String FLYME = "flyme";
    private final static String MEIZUBOARD[] = {"m9", "M9", "mx", "MX"};
    private final static String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
    private final static String CPU_FILE_PATH_0 = "/sys/devices/system/cpu/";
    private final static String CPU_FILE_PATH_1 = "/sys/devices/system/cpu/possible";
    private final static String CPU_FILE_PATH_2 = "/sys/devices/system/cpu/present";
    private static FileFilter CPU_FILTER = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            return Pattern.matches("cpu[0-9]", pathname.getName());
        }
    };

    private static String sMiuiVersionName;
    private static String sFlymeVersionName;
    private static boolean sIsTabletChecked = false;
    private static boolean sIsTabletValue = false;
    private static final String BRAND = Build.BRAND.toLowerCase();
    private static long sTotalMemory = -1;
    private static long sInnerStorageSize = -1;
    private static long sExtraStorageSize = -1;
    private static double sBatteryCapacity = -1;
    private static int sCpuCoreCount = -1;
    private static boolean isInfoReaded = false;

    private static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void checkReadInfo() {
        if (isInfoReaded) {
            return;
        }
        isInfoReaded = true;
        Properties properties = new Properties();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // android 8.0，读取 /system/uild.prop 会报 permission denied
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(new File(Environment.getRootDirectory(), "build.prop"));
                properties.load(fileInputStream);
            } catch (Exception e) {
                //QMUILog.printErrStackTrace(TAG, e, "read file error");
            } finally {
                close(fileInputStream);
            }
        }

        Class<?> clzSystemProperties = null;
        try {
            clzSystemProperties = Class.forName("android.os.SystemProperties");
            Method getMethod = clzSystemProperties.getDeclaredMethod("get", String.class);
            // miui
            sMiuiVersionName = getLowerCaseName(properties, getMethod, KEY_MIUI_VERSION_NAME);
            //flyme
            sFlymeVersionName = getLowerCaseName(properties, getMethod, KEY_FLYME_VERSION_NAME);
        } catch (Exception e) {
//            QMUILog.printErrStackTrace(TAG, e, "read SystemProperties error");
        }
    }

    private static boolean _isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断是否为平板设备
     */
    public static boolean isTablet(Context context) {
        if (sIsTabletChecked) {
            return sIsTabletValue;
        }
        sIsTabletValue = _isTablet(context);
        sIsTabletChecked = true;
        return sIsTabletValue;
    }

    /**
     * 判断是否是flyme系统
     */
    private static OnceReadValue<Void, Boolean> isFlymeValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            checkReadInfo();
            return !TextUtils.isEmpty(sFlymeVersionName) && sFlymeVersionName.contains(FLYME);
        }
    };

    public static boolean isFlyme() {
        return isFlymeValue.get(null);
    }

    /**
     * 判断是否是MIUI系统
     */
    public static boolean isMIUI() {
        checkReadInfo();
        return !TextUtils.isEmpty(sMiuiVersionName);
    }

    public static boolean isMIUIV5() {
        checkReadInfo();
        return "v5".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV6() {
        checkReadInfo();
        return "v6".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV7() {
        checkReadInfo();
        return "v7".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV8() {
        checkReadInfo();
        return "v8".equals(sMiuiVersionName);
    }

    public static boolean isMIUIV9() {
        checkReadInfo();
        return "v9".equals(sMiuiVersionName);
    }

    public static boolean isFlymeLowerThan(int majorVersion) {
        return isFlymeLowerThan(majorVersion, 0, 0);
    }

    public static boolean isFlymeLowerThan(int majorVersion, int minorVersion, int patchVersion) {
        checkReadInfo();
        boolean isLower = false;
        if (sFlymeVersionName != null && !sFlymeVersionName.equals("")) {
            try {
                Pattern pattern = Pattern.compile("(\\d+\\.){2}\\d");
                Matcher matcher = pattern.matcher(sFlymeVersionName);
                if (matcher.find()) {
                    String versionString = matcher.group();
                    if (versionString.length() > 0) {
                        String[] version = versionString.split("\\.");
                        if (version.length >= 1) {
                            if (Integer.parseInt(version[0]) < majorVersion) {
                                isLower = true;
                            }
                        }

                        if (version.length >= 2 && minorVersion > 0) {
                            if (Integer.parseInt(version[1]) < majorVersion) {
                                isLower = true;
                            }
                        }

                        if (version.length >= 3 && patchVersion > 0) {
                            if (Integer.parseInt(version[2]) < majorVersion) {
                                isLower = true;
                            }
                        }
                    }
                }
            } catch (Throwable ignore) {

            }
        }
        return isMeizu() && isLower;
    }


    private static OnceReadValue<Void, Boolean> isMeizuValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            checkReadInfo();
            return isPhone(MEIZUBOARD) || isFlyme();
        }
    };

    public static boolean isMeizu() {
        return isMeizuValue.get(null);
    }

    /**
     * 判断是否为小米
     * https://dev.mi.com/doc/?p=254
     */
    private static OnceReadValue<Void, Boolean> isXiaomiValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return Build.MANUFACTURER.toLowerCase().equals("xiaomi");
        }
    };

    public static boolean isXiaomi() {
        return isXiaomiValue.get(null);
    }

    private static OnceReadValue<Void, Boolean> isVivoValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return BRAND.contains("vivo") || BRAND.contains("bbk");
        }
    };

    public static boolean isVivo() {
        return isVivoValue.get(null);
    }

    private static OnceReadValue<Void, Boolean> isOppoValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return BRAND.contains("oppo");
        }
    };

    public static boolean isOppo() {
        return isOppoValue.get(null);
    }

    private static OnceReadValue<Void, Boolean> isHuaweiValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return BRAND.contains("huawei") || BRAND.contains("honor");
        }
    };

    public static boolean isHuawei() {
        return isHuaweiValue.get(null);
    }

    private static OnceReadValue<Void, Boolean> isEssentialPhoneValue = new OnceReadValue<Void, Boolean>() {
        @Override
        protected Boolean read(Void param) {
            return BRAND.contains("essential");
        }
    };

    public static boolean isEssentialPhone() {
        return isEssentialPhoneValue.get(null);
    }

    private static OnceReadValue<Context, Boolean> isMiuiFullDisplayValue = new OnceReadValue<Context, Boolean>() {
        @Override
        protected Boolean read(Context param) {
            return isMIUI() && Settings.Global.getInt(param.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
        }
    };

    public static boolean isMiuiFullDisplay(Context context) {
        return isMiuiFullDisplayValue.get(context);
    }

    private static boolean isPhone(String[] boards) {
        checkReadInfo();
        final String board = Build.BOARD;
        if (board == null) {
            return false;
        }
        for (String board1 : boards) {
            if (board.equals(board1)) {
                return true;
            }
        }
        return false;
    }

    public static long getTotalMemory(Context context) {
        if (sTotalMemory != -1) {
            return sTotalMemory;
        }
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            sTotalMemory = memoryInfo.totalMem;
        }
        return sTotalMemory;
    }

    public static long getInnerStorageSize() {
        if (sInnerStorageSize != -1) {
            return sInnerStorageSize;
        }
        File dataDir = Environment.getDataDirectory();
        if (dataDir == null) {
            return 0;
        }
        sInnerStorageSize = dataDir.getTotalSpace();
        return sInnerStorageSize;
    }


    public static boolean hasExtraStorage() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    public static long getExtraStorageSize() {
        if (sExtraStorageSize != -1) {
            return sExtraStorageSize;
        }
        if (!hasExtraStorage()) {
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getBlockCountLong();
        sExtraStorageSize = blockSize * availableBlocks;
        return sExtraStorageSize;
    }

    public static long getTotalStorageSize() {
        return getInnerStorageSize() + getExtraStorageSize();
    }

    // From Matrix
    public static int getCpuCoreCount() {
        if (sCpuCoreCount != -1) {
            return sCpuCoreCount;
        }
        int cores;
        try {
            cores = getCoresFromFile(CPU_FILE_PATH_1);
            if (cores == 0) {
                cores = getCoresFromFile(CPU_FILE_PATH_2);
            }
            if (cores == 0) {
                cores = getCoresFromCPUFiles(CPU_FILE_PATH_0);
            }
        } catch (Exception e) {
            cores = 0;
        }
        if (cores == 0) {
            cores = 1;
        }
        sCpuCoreCount = cores;
        return cores;
    }

    private static int getCoresFromCPUFiles(String path) {
        File[] list = new File(path).listFiles(CPU_FILTER);
        return null == list ? 0 : list.length;
    }

    private static int getCoresFromFile(String file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String fileContents = buf.readLine();
            buf.close();
            if (fileContents == null || !fileContents.matches("0-[\\d]+$")) {
                return 0;
            }
            String num = fileContents.substring(2);
            return Integer.parseInt(num) + 1;
        } catch (IOException e) {
            return 0;
        } finally {
            close(is);
        }
    }

    /**
     * 判断悬浮窗权限（目前主要用户魅族与小米的检测）。
     */
    public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;
        return checkOp(context, 24);  // 24 是AppOpsManager.OP_SYSTEM_ALERT_WINDOW 的值，该值无法直接访问
    }

    public static double getBatteryCapacity(Context context) {
        if (sBatteryCapacity != -1) {
            return sBatteryCapacity;
        }
        double ret;
        try {
            Class<?> cls = Class.forName(POWER_PROFILE_CLASS);
            Object instance = cls.getConstructor(Context.class).newInstance(context);
            Method method = cls.getMethod("getBatteryCapacity");
            ret = (double) method.invoke(instance);
        } catch (Exception ignore) {
            ret = -1;
        }
        sBatteryCapacity = ret;
        return sBatteryCapacity;
    }


    private static boolean checkOp(Context context, int op) {
        AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        try {
            Method method = manager.getClass().getDeclaredMethod("checkOp", int.class, int.class, String.class);
            int property = (Integer) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            return AppOpsManager.MODE_ALLOWED == property;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    private static String getLowerCaseName(Properties p, Method get, String key) {
        String name = p.getProperty(key);
        if (name == null) {
            try {
                name = (String) get.invoke(null, key);
            } catch (Exception ignored) {
            }
        }
        if (name != null) name = name.toLowerCase();
        return name;
    }
}