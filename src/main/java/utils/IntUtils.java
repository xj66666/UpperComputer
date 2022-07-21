package utils;

/**
 * @ClassName IntUtils
 * @Description
 * @Author meng
 * @Date 2021/5/2217:36
 */
public class IntUtils {
    public static String JL(String x,int y){
        int xL = x.length();
        if ((xL%y)!=0){
            for (int i = 0; i < JLy(x,y)-xL; i++) {
                x="0"+x;
            }
        }
        return x;
    }
    public static int JLy(String x,int y){
        int z =0;
        for (int i = x.length(); i < x.length()+4 ; i++) {
            if ((i%y)==0){
                z=i;
            }
        }
        return z;
    }


}
