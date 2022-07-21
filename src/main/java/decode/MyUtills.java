package decode;

public class MyUtills {
    public static int[] hexToInt(byte[] hex){
        int[] intBytes=new int[hex.length];
        for (int i = 0; i < hex.length; i++) {
            if(hex[i]>>7==0){
                intBytes[i] =hex[i]&0x7f;
            }else {
                intBytes[i] = ((~hex[i]&0x7f)+1)*(-1);
            }
        }
        return intBytes;
    }
}
