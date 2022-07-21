package utils;

/**
 * @ClassName utils.CRC16
 * @Description
 * @Author meng
 * @Date 2021/5/188:41
 */
public class CRC16 {
    public static String getCRC2(int [] bytes) {


        int CRC = 0xFFFF;
        int POLYNOMIAL = 0x8408;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= bytes[i];
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x0001)!=0) {
                    CRC = (CRC >> 1)^POLYNOMIAL;
                } else {
                    CRC= (CRC>>1);
                }
            }
        }
        //高低位转换
         CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        return Integer.toHexString(CRC);
    }

}
