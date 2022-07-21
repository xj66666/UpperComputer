package utils;

import java.util.Arrays;

/**
 * @ClassName StringUtils
 * @Description
 * @Author meng
 * @Date 2021/5/1917:19
 */
public class StringUtils {
    public  static int [] StringToInt(String x){
        int [] c = new int[x.length()/2];
        int i =0;
        while (x.length()> 0){
            String s = x.substring(0, 2);
            x =x.substring(2,x .length());
            c[i] = Integer.parseInt(s,16);
//            System.out.println(s+"======"+x+"====="+c[i]);
            i++;
        }
        return c;
    }

    public static String StrTo16(String s) {
        String str = "";
        while (s.length()>0) {
            int x = s.indexOf(',');
            if (x>0){
                int ch = Integer.parseInt(s.substring(0,x));
//            System.out.println("===="+ch);
                s = s.substring(x+1,s.length());
//            System.out.println("====="+s);
                String s16 = "00"+Integer.toHexString(ch);
//            System.out.println("====="+s16);
                str = str + s16;
            }else {
                int ch = Integer.parseInt(s);
//            System.out.println("===="+ch);
                String s16 ="00"+ Integer.toHexString(ch);
//            System.out.println("====="+s16);
                str = str + s16;
                s="";
            }
        }
        return str;
    }


    public static String JLEvennumbers(String x){
        if ((x.length()%2)!=0){
            x="0"+x;
        }
        return x;

    }

    public static String StringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }

    public static String AsciiToString(String d) {
        String z="";
        int x[] =new int[d.length()/4];
        int i=0;
        while (d.length()>0){
            String a = d.substring(2,4);
            x[i]=StringUtils.StringToInt(a)[0];
            d = d.substring(4,d.length());
            if (x[i]!=0){
                z+=(char)x[i];
            }
            i++;
        }
        System.out.println(Arrays.toString(x));
        return z;
    }


}
