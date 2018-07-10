package it.eng.hlf.android.client.config;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import it.eng.hlf.android.client.exception.HLFClientException;

import static java.lang.String.format;

public class ExternalStorageReader {

    private final static String EXTERNAL_STORAGE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/";

    /*public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }*/

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static InputStream getConfigurationFile(Context context) throws FileNotFoundException, HLFClientException {
        if (!isExternalStorageReadable())
            throw new HLFClientException("External Storage not available!");
        FileInputStream fileInputStream = context.openFileInput(EXTERNAL_STORAGE_PATH + "config-fabric-network.json");
        return fileInputStream;
    }

    public static File getSkConfigPath(Context context, String domainName, String user, String cryptoDir) throws HLFClientException {
        if (!isExternalStorageReadable())
            throw new HLFClientException("External Storage not available!");
        String usersPath = format("/users/" + user + "@%s/msp/keystore/", domainName);
        String path = EXTERNAL_STORAGE_PATH + cryptoDir + "/peerOrganizations/" + domainName + usersPath;
        return context.getDir(path, 0);
    }

    public static File getCertConfigPath(Context context, String domainName, String user, String cryptoDir) throws HLFClientException {
        if (!isExternalStorageReadable())
            throw new HLFClientException("External Storage not available!");
        String usersPath = format("/users/Admin@%s/msp/signcerts/" + user + "@%s-cert.pem", domainName,
                domainName);
        return context.getDir(EXTERNAL_STORAGE_PATH + cryptoDir + "/peerOrganizations/" +
                domainName + usersPath, 0);
    }


}
