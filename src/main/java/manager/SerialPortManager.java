package manager;

import decode.Adpcm_state;
import gnu.io.*;
import utils.ByteUtils;
import utils.ShowUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static decode.Decode.adpcm_decode;
import static decode.MyUtills.hexToInt;

/**
 * 串口管理
 *
 * @author yangle
 */
@SuppressWarnings("all")
public class SerialPortManager {

    /**
     * 查找所有可用端口
     *
     * @return 可用端口名称列表
     */
    public static final ArrayList<String> findPorts() {
        // 获得当前所有可用串口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        ArrayList<String> portNameList = new ArrayList<String>();
        // 将可用串口名添加到List并返回该List
        while (portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();
            portNameList.add(portName);
        }
        return portNameList;
    }

    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudrate 波特率
     *                 * @return 串口对象
     * @throws PortInUseException 串口已被占用
     */
    public static final SerialPort openPort(String portName, int baudrate) throws PortInUseException {
        try {
            // 通过端口名识别端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            // 打开端口，并给端口名字和一个timeout（打开操作的超时时间）
            CommPort commPort = portIdentifier.open(portName, 2000);
            // 判断是不是串口
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                try {
                    // 设置一下串口的波特率等参数
                    // 数据位：8
                    // 停止位：1
                    // 校验位：None
                    serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                } catch (UnsupportedCommOperationException e) {
                    e.printStackTrace();
                }
                return serialPort;
            }
        } catch (NoSuchPortException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭串口
     *
     * @param serialport 待关闭的串口对象
     */
    public static void closePort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
        }
    }

    /**
     * 往串口发送数据
     *
     * @param serialPort 串口对象
     * @param order      待发送数据
     */
    public static void sendToPort(SerialPort serialPort, byte[] order) {
        OutputStream out = null;
        try {
            out = serialPort.getOutputStream();
            out.write(order);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    out = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将数组添加到一个较大的数组中
     *
     * @param a
     * @param b
     * @param num
     */
    public static void add(double[][] a, double[][] b, int num) {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 30; j++) {
                b[i][num * 30 + j] = a[i][j];
            }
        }
    }

    /**
     * @param path
     * @param loopStartEnd
     */
    public static void saveDatas(double[][] resultDatas) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
        try (
                PrintStream ps = new PrintStream("D:/python_train/test/" + sdf.format(new Date()) + ".txt")
        ) {
            for (double[] row : resultDatas) {
                for (double data : row) {
                    ps.print((int) data);
                    ps.print("    ");
                }
                ps.println();
            }
            ps.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     */
    public static byte[] readFromPort(SerialPort serialPort) {
        InputStream in = null;
        byte[] bytes = {};
        double[][] windowDatas = new double[7][60];//用来移动数据的窗数组
        double[] sum = new double[7];//窗数组每一行的和
        double[] averageDates = new double[7];//每一行的平均值
//      double[][] frontDatas = new double[7][135];//获得能量可能用到的前面的数据
        int status = 0;
        double[][] preprocessedDatas = new double[7][30];//用来存放预处理后的数据，进行短时能量检测
        int count = 0;//用来计数到60
        double[] sum2 = new double[7];//短时能量每一行的平方和
//      double T1 = 3000;
        double T2 = 4000;
//        double[][] shoushiDatas = new double[7][600];//存放检测到动作后的数据
        int num = 0;
        double[][] resultDatas = new double[7][210];//最后的数据
        int len;
        byte[] readBuffer = new byte[28];
        int[][] res=new int[7][8];
        Adpcm_state[] states=new Adpcm_state[7];
        Adpcm_state startState=new Adpcm_state();
        for (int i = 0; i < 7; i++) {
            states[i]=startState;
        }
        double[] yuanDatas=new double[7]; //存一条数据
        try {
            in = serialPort.getInputStream();
            while ((len = in.read(readBuffer)) > 0) {
                int[] ints = hexToInt(readBuffer);
                for (int i = 0; i < ints.length/4; i++) {  //每四个一组 解码  得到 7*8 的int解码数组
                    int[] outData=new int[8];
                    adpcm_decode(Arrays.copyOfRange(ints,4*i,4*i+4),outData,8,states[i]);
                    res[i]=outData;
                }
                for (int index = 0; index < 8; index++) {
                    for (int k = 0; k < 7; k++) {
                        yuanDatas[k]=res[k][index];
                    }
                    if (yuanDatas[0] < 5000 && yuanDatas[1] < 5000 && yuanDatas[2] < 5000 && yuanDatas[3] < 5000 && yuanDatas[4] < 5000
                            && yuanDatas[5] < 5000 && yuanDatas[6] < 5000) { //串口上传数据
    //                    System.out.println(Arrays.toString(yuanDatas));
                        for (int i = 0; i < 7; i++) {
                            for (int j = 0; j < 59; j++) {
                                windowDatas[i][j] = windowDatas[i][j + 1];
                            }
                            windowDatas[i][59] = yuanDatas[i];
                        }
                        for (int m = 0; m < 7; m++) {
                            for (int n = 0; n < 60; n++) {
                                sum[m] = sum[m] + windowDatas[m][n];
                            }
                            averageDates[m] = sum[m] / 60;
                            sum[m] = 0;
                            yuanDatas[m] = yuanDatas[m] - averageDates[m];
                        }
    //                    for (int i = 0; i < 7; i++) {
    //                        System.out.print(new BigDecimal(yuanDatas[i]).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + "   ");
    //                    }
    //                    System.out.println();

                        //用于保存手势前的数据
    //                    for (int i = 0; i < 7; i++) {
    //                        for (int j = 0; j < 134; j++) {
    //                            frontDatas[i][j] = frontDatas[i][j + 1];
    //                        }
    //                        frontDatas[i][134] = yuanDatas[i];
    //                    }

                        //获得一帧数据
                        for (int i = 0; i < 7; i++) {
                            preprocessedDatas[i][count] = yuanDatas[i];
                        }
                        count++;

                        if (count == 30) {  //表示得到了一帧数据
                            count = 0;
                            //计算每一帧的能量（短时能量）
                            for (int m = 0; m < 7; m++) {
                                for (int n = 0; n < 30; n++) {
                                    sum2[m] += preprocessedDatas[m][n] * preprocessedDatas[m][n];
                                }
                                sum2[m] /= 30;
                            }
                            for (int i = 0; i < 7; i++) {
                                System.out.print(new BigDecimal(sum2[i]).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "         ");
    //                            sum2[i] = 0;
                            }
                            System.out.println();
                            if (status == 0) {
                                if ((sum2[0] + sum2[1] + sum2[2] + sum2[3] + sum2[4] + sum2[5] + sum2[6]) > T2) {
                                    status = 1;
                                    for (int i = 0; i < 7; i++) {
                                        sum2[i] = 0;
                                    }
                                    add(preprocessedDatas, resultDatas, num);
                                    num++;
                                }
                            } else {
                                for (int i = 0; i < 7; i++) {
                                    sum2[i] = 0;
                                }
                                add(preprocessedDatas, resultDatas, num);
                                num++;
                            }
                            if (num == 7) {
                                saveDatas(resultDatas);
                                status = 0;
                                num = 0;
                            }
                        }
                    }
            }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    /**
     * 添加监听器
     *
     * @param port     串口对象
     * @param listener 串口存在有效数据监听
     */
    public static void addListener(SerialPort serialPort, DataAvailableListener listener) {
        try {
            // 给串口添加监听器
            serialPort.addEventListener(new SerialPortListener(listener));
            // 设置当有数据到达时唤醒监听接收线程
            serialPort.notifyOnDataAvailable(true);
            // 设置当通信中断时唤醒中断线程
            serialPort.notifyOnBreakInterrupt(true);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
    }

    /**
     * 串口监听
     */
    public static class SerialPortListener implements SerialPortEventListener {

        private DataAvailableListener mDataAvailableListener;

        public SerialPortListener(DataAvailableListener mDataAvailableListener) {
            this.mDataAvailableListener = mDataAvailableListener;
        }

        public void serialEvent(SerialPortEvent serialPortEvent) {
            switch (serialPortEvent.getEventType()) {
                case SerialPortEvent.DATA_AVAILABLE: // 1.串口存在有效数据
                    if (mDataAvailableListener != null) {
                        mDataAvailableListener.dataAvailable();
                    }
                    break;

                case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2.输出缓冲区已清空
                    break;

                case SerialPortEvent.CTS: // 3.清除待发送数据
                    break;

                case SerialPortEvent.DSR: // 4.待发送数据准备好了
                    break;

                case SerialPortEvent.RI: // 5.振铃指示
                    break;

                case SerialPortEvent.CD: // 6.载波检测
                    break;

                case SerialPortEvent.OE: // 7.溢位（溢出）错误
                    break;

                case SerialPortEvent.PE: // 8.奇偶校验错误
                    break;

                case SerialPortEvent.FE: // 9.帧错误
                    break;

                case SerialPortEvent.BI: // 10.通讯中断
                    ShowUtils.errorMessage("与串口设备通讯中断");
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 串口存在有效数据监听
     */
    public interface DataAvailableListener {
        /**
         * 串口存在有效数据
         */
        void dataAvailable();
    }


    public static void getListener(SerialPort port) {
        SerialPortManager.addListener(port, new DataAvailableListener() {
            @Override
            public void dataAvailable() {
                byte[] data = null;
                try {
                    if (port == null) {
                        ShowUtils.errorMessage("串口对象为空，监听失败！");
                    } else {

                        // 读取串口数据
                        data = SerialPortManager.readFromPort(port);
                        System.out.println("=============数据" + ByteUtils.byteArrayToHexString(data));
                    }
                } catch (Exception e) {
                    ShowUtils.errorMessage(e.toString());
                    // 发生读取错误时显示错误信息后退出系统
                    System.exit(0);
                }
            }
        });
    }
}
