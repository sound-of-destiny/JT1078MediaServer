package util;

import java.util.Arrays;
import java.util.List;

public class ByteOperator {

    public static byte[] byteToBytes(byte val) {
        byte[] res = new byte[1];
        res[0] = val;
        return res;
    }

    public static byte[] intTo4Byte(int val) {
        byte[] res = new byte[4];
        res[0] = (byte) ((val >>> 24) & 0xFF);
        res[1] = (byte) ((val >>> 16) & 0xFF);
        res[2] = (byte) ((val >>> 8) & 0xFF);
        res[3] = (byte) (val & 0xFF);
        return res;
    }

    public static byte[] intTo3Byte(int val) {
        byte[] res = new byte[3];
        res[0] = (byte) ((val >>> 16) & 0xFF);
        res[1] = (byte) ((val >>> 8) & 0xFF);
        res[2] = (byte) (val & 0xFF);
        return res;
    }

    public static byte[] intTo2Byte(int val) {
        byte[] res = new byte[2];
        res[0] = (byte) ((val >> 8) & 0xff);
        res[1] = (byte) (val & 0xff);
        return res;
    }

    public static byte[] intTo1Byte(int value) {
        byte[] result = new byte[1];
        result[0] = (byte) (value & 0xFF);
        return result;
    }

    public static byte[] shortToBytes(int val) {
        byte[] res = new byte[2];
        res[0] = (byte) ((val >> 8) & 0xff);
        res[1] = (byte) (val & 0xff);
        return res;
    }

    public static int byte2ToShort(byte[] b) {
        return (b[0] & 0xff) | ((b[1] & 0xff) << 8);
    }

    public static int byte3ToInt(byte[] b) {
        return (b[0] & 0xff) | ((b[1] & 0xff) << 8) | ((b[2] & 0xff) << 16);
    }

    public static int byte4ToInt(byte[] b) {
        return (b[0] & 0xff) | ((b[1] & 0xff) << 8) | ((b[2] & 0xff) << 16) | ((b[3] & 0xff) << 24);
    }

    public static byte[] concatAll(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for(byte[] b : rest) {
            if (b != null) {
                System.arraycopy(b,0, result, offset, b.length);
                offset += b.length;
            }
        }
        return result;
    }

    public static int getCheckSum4JT808(byte[] bs, int start, int end) {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("getCheckSum4JT808 error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        int cs = 0;
        for (int i = start; i < end; i++) {
            cs ^= bs[i];
        }
        return cs;
    }

    public static byte[] concatAll(List<byte[]> rest) {
        int totalLength = 0;
        for (byte[] array : rest) {
            if (array != null) {
                totalLength += array.length;
            }
        }
        byte[] result = new byte[totalLength];
        int offset = 0;
        for (byte[] array : rest) {
            if (array != null) {
                System.arraycopy(array, 0, result, offset, array.length);
                offset += array.length;
            }
        }
        return result;
    }

    public static String byteToBitString(byte[] bList) {
        StringBuilder bs = new StringBuilder();
        for (byte b : bList) {
            bs.append((byte) ((b >> 7) & 0x1))
                    .append((byte) ((b >> 6) & 0x1))
                    .append((byte) ((b >> 5) & 0x1))
                    .append((byte) ((b >> 4) & 0x1))
                    .append((byte) ((b >> 3) & 0x1))
                    .append((byte) ((b >> 2) & 0x1))
                    .append((byte) ((b >> 1) & 0x1))
                    .append((byte) ((b) & 0x1));
        }
        return bs.toString();
    }

    public static byte[] longTo8Byte(long val) {
        byte[] res = new byte[8];
        res[0] = (byte) (val & 0xff);
        res[1] = (byte) ((val >> 8) & 0xff);
        res[2] = (byte) ((val >> 16) & 0xff);
        res[3] = (byte) ((val >> 24) & 0xff);
        res[4] = (byte) ((val >> 32) & 0xff);
        res[5] = (byte) ((val >> 40) & 0xff);
        res[6] = (byte) ((val >> 48) & 0xff);
        res[7] = (byte) ((val >> 56) & 0xff);
        return res;
    }
}
