import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created on 2016/10/26.
 * Use UUID based on Android_Id to define an unique device
 * Save UUID to local file after encryption and Sharepreference
 */

public class DeviceIdUtil {

    private static String filePath = File.separator + "MyApplication" + File.separator + "uuid";

    public static void saveDeviceId(Context context, String deviceId) {
        try {
            MySharedPreference sp = new MySharedPreference(context);
            sp.setKeyStr("share_prefrence_uuid", deviceId);

            String encrypt_deviceId = AES.encrypt(deviceId, uuid());
            saveDeviceIdToFile(context, encrypt_deviceId);

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    //返回DeviceId
    public static String getDeviceId(Context context) {
        MySharedPreference sp = new MySharedPreference(context);
        String deviceIdString = sp.getKeyStr("share_prefrence_uuid");
        if (StringUtil.isNotTrimBlank(deviceIdString)) {
            return returnDeviceIdIfSame(context, deviceIdString);
        }else {
            return getDeviceIdFromFile(context);
        }
    }

    //文件中是否有deviceid，有的话保存并返回，没有的话生成新的deviceid
    public static String getDeviceIdFromFile(Context context) {
        String fileRootPath = getPath(context) + filePath;
        String uuid = FileUtils.readFile(fileRootPath);
        if (StringUtil.isNotTrimBlank(uuid)) {
            try {
                String decrypt_uuid = AES.decrypt(uuid, uuid());
                saveDeviceId(context, decrypt_uuid);
                return decrypt_uuid;
            }catch (Exception e) {
                e.printStackTrace();
                String new_uuid = initNewUUID(context);
                saveDeviceId(context, new_uuid);
                return new_uuid;
            }
        }else {
            String new_uuid = initNewUUID(context);
            saveDeviceId(context, new_uuid);
            return new_uuid;
        }
    }

    //文件中是否有deviceid，有的话并且和sharepreference一致则返回，否则生成新的deviceid
    public static String returnDeviceIdIfSame(Context context, String deviceIdString) {

        String fileRootPath = getPath(context) + filePath;
        String uuid = FileUtils.readFile(fileRootPath);
        if (StringUtil.isNotTrimBlank(uuid)) {
            try {
                String decrypt_uuid = AES.decrypt(uuid, uuid());
                if (decrypt_uuid.equals(deviceIdString)) {//有的话并且和sharepreference一致则返回                    
                    return decrypt_uuid;
                }else {                                    //不一致生成新的deviceid
                    String new_uuid = initNewUUID(context);
                    saveDeviceId(context, new_uuid);
                    return new_uuid;
                }
            }catch (Exception e) {
                e.printStackTrace();
                saveDeviceId(context, deviceIdString);
                return deviceIdString;
            }
        }else {
            saveDeviceId(context, deviceIdString);//文件里没有，则取sharepreference里的deviceid
            return deviceIdString;
        }
    }

    //生成新的UUID
    public static String initNewUUID(Context context) {
        String uuid = "";
        String androidId = "" + Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        try {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            } else {

                uuid = UUID.randomUUID().toString();
            }
        } catch (Exception e) {
            uuid = UUID.randomUUID().toString();
        }

        String uuid_md5 = MD5Util.MD5Encode(uuid, "UTF-8");

        return uuid_md5;
    }

    //将deviceId保存到文件
    private static void saveDeviceIdToFile(Context context, String UUID) {
        String ExternalSdCardPath = getExternalSdCardPath() + filePath;
        FileUtils.writeFile(ExternalSdCardPath, UUID);
        String InnerPath = context.getFilesDir().getAbsolutePath() + filePath;
        FileUtils.writeFile(InnerPath,UUID);
    }

    private static String getPath(Context context) {
        //首先判断是否有外部存储卡，如没有判断是否有内部存储卡，如没有，继续读取应用程序所在存储
        String phonePicsPath = getExternalSdCardPath();
        if (phonePicsPath == null) {
            phonePicsPath = context.getFilesDir().getAbsolutePath();
        }
        return phonePicsPath;
    }

    /**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     *
     * @return
     */
    private static ArrayList<String> getDevMountList() {
        String[] toSearch = FileUtils.readFile("/system/etc/vold.fstab").split(" ");
        ArrayList<String> out = new ArrayList<>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }

    /**
     * 获取扩展SD卡存储目录
     * <p/>
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     *
     * @return
     */
    public static String getExternalSdCardPath() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sdCardFile.getAbsolutePath();
        }

        String path = null;

        File sdCardFile = null;

        ArrayList<String> devMountList = getDevMountList();

        for (String devMount : devMountList) {
            File file = new File(devMount);

            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();

                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
                File testWritable = new File(path, "test_" + timeStamp);

                if (testWritable.mkdirs()) {
                    testWritable.delete();
                } else {
                    path = null;
                }
            }
        }

        if (path != null) {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }

        return null;
    }


    //JNI
    static {
        System.loadLibrary("Appcore");
    }

    public static native String uuid();
}
