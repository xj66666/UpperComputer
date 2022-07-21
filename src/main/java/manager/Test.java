package manager;


import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import static manager.SerialPortManager.openPort;
import static manager.SerialPortManager.readFromPort;

public class Test {
    public static void main(String[] args) throws PortInUseException {
        SerialPort serialPort=openPort("COM1",256000);
        while(true){
            byte[] res=readFromPort(serialPort);
        }
    }
}
