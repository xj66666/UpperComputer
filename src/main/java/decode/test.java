package decode;

import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        byte[] in={(byte) 0x80, 0x40};
        int[] ints = MyUtills.hexToInt(in);
        System.out.println(Arrays.toString(ints));




       /* Adpcm_state state = new Adpcm_state(2000, 57);
        byte[] inData={-128,58,43,75,-128,76,43,75,62,-128,-128,58,59,60,8,76,59,60,63,8};
        int[] outData=new int[inData.length*2];
        adpcm_decode(inData,outData,inData.length,state);
        System.out.println(Arrays.toString(outData));*/
    }
}
