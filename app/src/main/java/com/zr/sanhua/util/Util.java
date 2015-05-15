package com.zr.sanhua.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/5/8.
 */
public class Util {

    public static boolean hasNetwork(Context mContext) {
        boolean netStatus = false;
        try {
            ConnectivityManager connectManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectManager
                    .getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.isAvailable()
                        && activeNetworkInfo.isConnected()) {
                    netStatus = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return netStatus;
    }

    public static String formatDateHM(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(date);
    }

    public static String formatYMDHMS(long time) {

        Date d = new Date(time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return sdf.format(d);
    }

    public static String formatYMD(long time) {

        Date d = new Date(time);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return sdf.format(d);
    }

    public static String formatHM(long time) {

        Date d = new Date(time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        return sdf.format(d);
    }

    public static String formatHMS(long time) {

        Date d = new Date(time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        return sdf.format(d);
    }

    public static String getDeviceId(Context context) {

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);//
        String result = "";
        result += "MAC = " + getLANMac() + "\n";
        result += "DeviceId(IMEI) = " + tm.getDeviceId() + "\n";
        result += "HardwareSerialNumber = " + android.os.Build.SERIAL + "\n";
        result += "AndroidID = "
                + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID) + "\n";
        return MD5Util.getMD5String(result);

    }

    // 获取mac地址
    private static String getLANMac() {
        String mac = getMacLevel9("eth[0-9]+");
        if (mac.equals("")) {
            mac = getMacNetcfg("eth[0-9]+");
        }

        // MAC地址总是使用大写的。
        if (TextUtils.isEmpty(mac)) {
            mac.toUpperCase();
        }

        return mac;
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要API Level 9, <uses-permission
     * android:name="android.permission.INTERNET"/>
     */
    private static String getMacLevel9(String name_pattern_rgx) {
        try {
            Method getHardwareAddress = NetworkInterface.class
                    .getMethod("getHardwareAddress");
            Enumeration<NetworkInterface> nis = NetworkInterface
                    .getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface n = nis.nextElement();
                getHardwareAddress.invoke(n);
                byte[] hw_addr = (byte[]) getHardwareAddress.invoke(n);
                if (hw_addr != null) {
                    String name = n.getName().toLowerCase();
                    StringBuffer buffer = new StringBuffer();
                    for (int i = 0; i < hw_addr.length; i++) {
                        if (i != 0) {
                            buffer.append(':');
                        }

                        String str = Integer.toHexString(hw_addr[i] & 0xFF);
                        buffer.append(str.length() == 1 ? 0 + str : str);
                    }
                    String mac = buffer.toString().toUpperCase();
                    // Logger.d("NetTools.getMacLevel9", name + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (SocketException e) {
            // Logger.d("NetTools.getMacLevel9", "Exception: SocketException.");
            e.printStackTrace();
        } catch (SecurityException e) {
            // Logger.d("NetTools.getMacLevel9",
            // "Exception: SecurityException.");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // Logger.d("NetTools.getMacLevel9",
            // "Exception: NoSuchMethodException.");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Logger.d("NetTools.getMacLevel9",
            // "Exception: IllegalArgumentException.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // Logger.d("NetTools.getMacLevel9",
            // "Exception: IllegalAccessException.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // Logger.d("NetTools.getMacLevel9",
            // "Exception: InvocationTargetException.");
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取第一个符合name_pattern的网卡的MAC地址 需要netcfg工具, <uses-permission
     * android:name="android.permission.INTERNET"/>
     */
    private static String getMacNetcfg(String name_pattern_rgx) {
        Process proc;
        try {
            proc = Runtime.getRuntime().exec("netcfg");
            BufferedReader is = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            proc.waitFor();
            Pattern result_pattern = Pattern
                    .compile(
                            "^([a-z0-9]+)\\s+(UP|DOWN)\\s+([0-9./]+)\\s+.+\\s+([0-9a-f:]+)$",
                            Pattern.CASE_INSENSITIVE);
            while (is.ready()) {
                String info = is.readLine();
                Matcher m = result_pattern.matcher(info);
                if (m.matches()) {
                    String name = m.group(1).toLowerCase();
                    String status = m.group(2);
                    String addr = m.group(3);
                    String mac = m.group(4).toUpperCase();
                    // Logger.d("NetTools.getMacNetcfg", "match success:" + name
                    // + " " + status + " " + addr + " " + mac);
                    if (name.matches(name_pattern_rgx)) {
                        return mac;
                    }
                }
            }
        } catch (IOException e) {
            // Logger.d("NetTools.getMacNetcfg", "Exception: IOException.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            // Logger.d("NetTools.getMacNetcfg",
            // "Exception: InterruptedException.");
            e.printStackTrace();
        }
        return "";
    }


    public static boolean isServiceRunning(Context mContext, String className) {

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(100);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {

            if (serviceList.get(i).service.getClassName().equals(className)) {

                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    // 获取一个字符串里包括一个子字符串的位置

    public static int[] getSubStringIndexs(String str1, String str2) {

        if (str1.contains(str2)) {

            int[] indexs = new int[2];

            indexs[0] = str1.indexOf(str2);

            indexs[1] = indexs[0] + str2.length();

            return indexs;

        }

        return null;
    }
}
