package utils;

import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import manager.SerialPortManager;

/**
 * @ClassName Tools
 * @Description
 * @Author meng
 * @Date 2021/5/1817:12
 */
public class  Tools {

    public static SerialPort OpenPort(SerialPortManager serialPortManager) throws PortInUseException {
        return SerialPortManager.openPort(SerialPortManager.findPorts().toString().substring(1,5),9600);

    }

    public  static void ClosePort(SerialPortManager serialPortManager,SerialPort serialPort){
        SerialPortManager.closePort(serialPort);
        System.out.println("关闭成功");
    }


    public  static String WRserialPort(SerialPortManager serialPortManager, SerialPort serialPort, String WString){

        byte[] Wbytes = ByteUtils.hexStr2Byte(WString);
        SerialPortManager.sendToPort(serialPort,Wbytes);

        byte[] Rbytes = SerialPortManager.readFromPort(serialPort);

        return ByteUtils.byteArrayToHexString(Rbytes);

    }


    public  static String FindId(SerialPortManager serialPortManager, SerialPort serialPort) throws InterruptedException {

        String x ="04ff0f";
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Find命令:"+WString);
        return  Tools.getEpc(serialPortManager,serialPort,WRserialPort(serialPortManager,serialPort,WString));
    }


    public  static String getEpc(SerialPortManager serialPortManager, SerialPort serialPort,String x) throws InterruptedException {
        if  (x.substring(6,8).equals("01")){
            x=x.substring(12,x.length()-4);
            System.out.println("EpcId:"+x);
        }else {
            System.out.println("未找到卡:"+x);
            x="";
        }
        return x;
    }

    public  static String FindIdAll(SerialPortManager serialPortManager, SerialPort serialPort){

        String x ="04ff01";
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("命令:"+WString);
        return WRserialPort(serialPortManager,serialPort,WString);
    }


    public  static String ReadCard(SerialPortManager serialPortManager, SerialPort serialPort,String s,int Number){
        if(Number>120){
            Number=120;
        }
        String RNumber = Integer.toHexString(Number).toUpperCase();
        String SLength = Integer.toHexString(14+(s.length()/2)).toUpperCase();
        String EpcLength = Integer.toHexString(s.length()/4).toUpperCase();
        SLength =StringUtils.JLEvennumbers(SLength);
        EpcLength =StringUtils.JLEvennumbers(EpcLength);
        RNumber=StringUtils.JLEvennumbers(RNumber);
        System.out.println("ReadCard======="+SLength + "===="+EpcLength +"==="+RNumber);

        String x =SLength+"0002"+EpcLength+s+"0300"+RNumber+"000000000000";
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Read命令:"+WString);
        String z = WRserialPort(serialPortManager,serialPort,WString);
//        System.out.println("读取返回数据:"+z);
        if (z.substring(6,8).equals("00")){
           x= z.substring(8, z.length()-4);
        }else {
            System.out.println("数据为空或者读取错误:"+x);
            x="";
        }
        return  x ;
    }

    public  static String WriteCard(SerialPortManager serialPortManager, SerialPort serialPort,String s,String transmitting){

        String SLength = Integer.toHexString(14+(s.length()/2)+(transmitting.length()/2)).toUpperCase();
        String TLength = Integer.toHexString(transmitting.length()/4).toUpperCase();
        String EpcLength = Integer.toHexString(s.length()/4).toUpperCase();

        SLength =StringUtils.JLEvennumbers(SLength);
        TLength =StringUtils.JLEvennumbers(TLength);
        EpcLength =StringUtils.JLEvennumbers(EpcLength);
//        System.out.println("WriteCard======="+SLength + "===="+TLength );

        String x =SLength+"0003"+TLength+EpcLength+s+"0300"+transmitting+"000000000000";
        System.out.println(x.length());
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Write命令:"+WString);
        return WRserialPort(serialPortManager,serialPort,WString);
    }


    public  static String WriteEPCid(SerialPortManager serialPortManager, SerialPort serialPort,String s){

        String SLength = Integer.toHexString(9+(s.length()/2)).toUpperCase();
        String TLength = Integer.toHexString(s.length()/4).toUpperCase();

        SLength =StringUtils.JLEvennumbers(SLength);
        TLength =StringUtils.JLEvennumbers(TLength);
        System.out.println("WriteCard======="+SLength + "===="+TLength );

        String x =SLength+"0004"+TLength+"00000000"+s;
        System.out.println(x.length());
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Write命令:"+WString);
        return WRserialPort(serialPortManager,serialPort,WString);
    }



    public  static String  BlockErase(SerialPortManager serialPortManager, SerialPort serialPort,String s){

        String SLength = Integer.toHexString(14+(s.length()/2)).toUpperCase();
        String EpcLength = Integer.toHexString(s.length()/4).toUpperCase();

        SLength =StringUtils.JLEvennumbers(SLength);
        EpcLength =StringUtils.JLEvennumbers(EpcLength);
        System.out.println("WriteCard======="+SLength + "===="+EpcLength );

        String x =SLength+"0007"+EpcLength+s+"030014000000000000";
        System.out.println(x.length());
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Write命令:"+WString);
        return WRserialPort(serialPortManager,serialPort,WString);
    }

    public  static String  Alert(SerialPortManager serialPortManager, SerialPort serialPort){

        String x ="070033140203";
        System.out.println(x.length());
        int [] c =StringUtils.StringToInt(x);
        String WString =x+ CRC16.getCRC2(c);
        System.out.println("Write命令:"+WString);
        return WRserialPort(serialPortManager,serialPort,WString);
    }


}
