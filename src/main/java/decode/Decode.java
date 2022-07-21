package decode;
public class Decode {

    static  short[] indexTable = {
        -1, -1, -1, -1, 2, 4, 6, 8,
                -1, -1, -1, -1, 2, 4, 6, 8,
    };

    static short[] stepsizeTable = {
        7, 8, 9, 10, 11, 12, 13, 14, 16, 17,
                19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
                50, 55, 60, 66, 73, 80, 88, 97, 107, 118,
                130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
                337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
                876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066,
                2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
                5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899,
                15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767
    };
    static boolean LOW=false;
    static boolean HIGH=true;
    public static void adpcm_decode(int[] inData, int[] outData, int len, Adpcm_state state){

        //char* inp;            /* Input buffer pointer */
        //short* outp;        /* output buffer pointer */
        int sign;            /* Current adpcm sign bit */
        int delta;        /* Current adpcm output value */
        int step;            /* Stepsize */
        int valpred;        /* Predicted value */
        int vpdiff;        /* Current change to valpred */
        int index;        /* Current step change index */
        //int inputbuffer = 0;    /* place to keep next 4-bit value */
        boolean bufferstep;    /* toggle between inputbuffer/input */
        int inPointer=0; //输入数组的指针下标
        int outPointer=0; //输出指针的下标


        valpred = state.valprev;
        index = state.index;
        step = stepsizeTable[index];


        bufferstep = LOW;
        len *= 2;            /* !!!! verify important TODO (FIX ME) JGF*/

        for (; len > 0; len--)
        {
            /* Step 1 - get the delta value */
            //œ»»°µÕ4Œª OK
            if (bufferstep)  //若该读高四位
            {
                delta =  ((inData[inPointer] >> 4) & 0x0f);  //取高四位
                inPointer++; //输入后移一个索引位置

            }
            else  //读低四位
            {

                delta =  (inData[inPointer] & 0x0f); //取低四位

            }

            bufferstep = !bufferstep;  //高低交替

            /* Step 2 - Find new index value (for later) */
            index += indexTable[delta];
            if (index < 0)
            {
                index = 0;
            }
            if (index > 88)
            {
                index = 88;
            }

            /* Step 3 - Separate sign and magnitude */
            sign = delta & 8;
            delta = delta & 7;

            /* Step 4 - Compute difference and new predicted value */
            /*
             ** Computes 'vpdiff = (delta+0.5)*step/4', but see comment
             ** in adpcm_coder.
             */
            vpdiff = step >> 3;
            if ((delta & 4)>0)
            {
                vpdiff += step;
            }
            if ((delta & 2)>0)
            {
                vpdiff += step >> 1;
            }
            if ((delta & 1)>0)
            {
                vpdiff += step >> 2;
            }

            if (sign>0)
            {
                valpred -= vpdiff;
            }
            else
            {
                valpred += vpdiff;
            }

            /* Step 5 - clamp output value */
            if (valpred > 32767)
            {
                valpred = 32767;
            }
            else if (valpred < -32768)
            {
                valpred = -32768;
            }

            /* Step 6 - Update step value */
            step = stepsizeTable[index];

            /* Step 7 - Output value */
            outData[outPointer]= valpred;  //输出值
            outPointer++; //更新指针
        }

        state.valprev = valpred;
        state.index = index;
    }
}
