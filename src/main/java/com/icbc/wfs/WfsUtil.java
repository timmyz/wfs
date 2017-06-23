package com.icbc.wfs;

import java.io.File;

import com.icbc.dubbo.util.MurMurHash;

public class WfsUtil {
    public static final String PATH_SEPARATOR = "/";
    public static final String ROOT = PATH_SEPARATOR;

    public static String getFileName(String path) {
        if (ROOT.equals(path)) {
            return ROOT;
        } else {
            int sub;
            if (path.endsWith(PATH_SEPARATOR)) {
                sub = path.lastIndexOf(PATH_SEPARATOR, path.length() - 2);
            } else {
                sub = path.lastIndexOf(PATH_SEPARATOR);
            }
            return path.substring(sub + 1);
        }
    }

    public static String getParent(String path) {
        if (ROOT.equals(path)) {
            return ROOT;
        } else {
            int sub;
            if (path.endsWith(PATH_SEPARATOR)) {
                sub = path.lastIndexOf(PATH_SEPARATOR, path.length() - 2);
            } else {
                sub = path.lastIndexOf(PATH_SEPARATOR);
            }

            return path.substring(0, sub + 1);
        }
    }

    public static String getPhyFilePath(String path) {
        String hash = MurMurHash.hashRange(path);
        return WfsEnv.rootDir + (WfsEnv.rootDir.endsWith(File.separator) ? "" : File.separator)
                + hash.substring(0, 2) + File.separator + hash;
    }

    public static File getPhyFile(String path) {
        return new File(getPhyFilePath(path));
    }
}
