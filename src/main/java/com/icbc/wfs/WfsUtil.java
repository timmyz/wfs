package com.icbc.wfs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.icbc.dubbo.util.MurMurHash;

public class WfsUtil {
    public static final String PATH_SEPARATOR = "/";
    public static final String ROOT = PATH_SEPARATOR;

    private static Logger logger = LoggerFactory.getLogger(WfsUtil.class);

    public static boolean isDirectory(String path) {
        return path.endsWith(PATH_SEPARATOR);
    }

    public static boolean isEmptyString(String str) {

        if (str == null || str.isEmpty()) {
            return true;
        } else {
            return false;
        }

    }

    public static String getFileName(String path) {
        if (ROOT.equals(path)) {
            return ROOT;
        } else {
            int sub;
            if (isDirectory(path)) {
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
            if (isDirectory(path)) {
                sub = path.lastIndexOf(PATH_SEPARATOR, path.length() - 2);
            } else {
                sub = path.lastIndexOf(PATH_SEPARATOR);
            }

            return path.substring(0, sub + 1);
        }
    }

    public static String getName(File file) {
        if (file.isDirectory()) {
            return file.getName() + PATH_SEPARATOR;
        } else
            return file.getName();
    }

    public static boolean isHash(String str) {
        try {
            MurMurHash.nParseLong(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean putPhy(InputStream in, File phyFile) {
        if (!phyFile.getParentFile().exists()) {
            logger.debug("parent not exists");
            if (!phyFile.getParentFile().mkdirs()) {
                logger.error("putPhy-->mkdir false");
                return false;
            }
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(phyFile));
            long totalLen = 0;
            int n;
            byte[] b = new byte[0x2000];
            while (-1 != (n = in.read(b))) {
                totalLen += n;
                if (totalLen > WfsEnv.MAX_FILE_LENGTH) {
                    out.close();
                    out = null;
                    if (phyFile.delete()) {
                        logger.warn("too large, delete it");
                    }
                    return false;
                }
                out.write(b, 0, n);
            }
            out.flush();
        } catch (IOException e) {
            logger.error("putPhy-->IOException", e);
            return false;
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                logger.error("putPhy-->close IOException", e);
            }
        }
        return true;
    }

    public static String getPhyFilePath(String path) {
        return getPhyFilePathByHash(MurMurHash.hashHex(path));
    }

    public static String getPhyFilePathByHash(String hash) {
        return WfsEnv.ROOT_DIR + hash.substring(0, WfsEnv.PHY_DIR_LENGTH) + File.separator + hash;
    }

    public static File getPhyFile(String path) {
        return new File(getPhyFilePath(path));
    }

    public static boolean delete(File targetFile) {
        if (targetFile.isDirectory()) {
            File[] fileList = targetFile.listFiles();
            if (fileList != null) {
                for (File file : fileList) {
                    if (!delete(file)) {
                        return false;
                    }
                }
            }
        } else if (targetFile.exists()) {
            return targetFile.delete();
        }
        return true;
    }

    public static boolean mergerFalse(boolean[] booleans) {
        for (boolean b : booleans) {
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public static long nParseLong(String s) throws NumberFormatException {
        if (s == null) {
            throw new NumberFormatException("null");
        }
        long result = 0L;
        int i = 0, max = s.length();
        int digit;
        if (max > 0) {
            while (i < WfsEnv.PHY_DIR_LENGTH) {
                result <<= 4;
                if (i < max) {
                    digit = Character.digit(s.charAt(i), 16);
                    if (digit < 0) {
                        throw new NumberFormatException(s);
                    }
                    result -= digit;
                }
                i++;
            }
        }
        return result;
    }
}
