/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */


import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import java.net.InetAddress;

public class ModbusInterface {

    private static InetAddress ipAddress;
    private static TCPMasterConnection connnection;
    private static ModbusTCPTransaction transaction;

    private static ReadInputRegistersRequest readRequest;
    private static ReadInputRegistersResponse readResult;

    private static String ip = "192.168.0.8";                                        // the CCGX in my installation
    private static int port = 502;                                                   // default modbus port

    public ModbusInterface() {
        try {
            ipAddress = InetAddress.getByName(ip);
        } catch (Exception ex) {
            System.out.println("Malformed ip");
        }
    }

    public void connectGateway() {

        try {
            connnection = new TCPMasterConnection(ipAddress);               //the connection
            transaction = null;                                             //the transaction
            connnection.setPort(port);                                      // specify the port for the connection
            connnection.connect();
            connnection.setTimeout(1000);
            if (connnection.isConnected()) {
                transaction = new ModbusTCPTransaction(connnection);       // define the transaction for an established connection
                transaction.setRetries(2);//
                transaction.setReconnecting(true);
            }
        } catch (Exception ex) {
            System.out.println("Modbus Connection Error");
        }
    }

    public ReadInputRegistersResponse readRegister(int slaveId, int register) {
        try {
            if (transaction != null) {
                readRequest = new ReadInputRegistersRequest(register, 1);
                readResult = new ReadInputRegistersResponse();
                readRequest.setTransactionID(transaction.getTransactionID());
                readRequest.setUnitID(slaveId);                                         // the device ID connected to the CCGX  
                readResult.setUnitID(slaveId);
                transaction.setRequest(readRequest);
                transaction.execute();
                readResult = (ReadInputRegistersResponse) transaction.getResponse();
                return readResult;
            }

        } catch (Exception ex) {
            System.out.println("Modbus TCP Read Error");
        }
        System.out.println("No connection yet");
        return null;
    }
   
    public boolean isConnected() {
        if (transaction != null) 
            return true;
        else
            return false;            
    }
    
     void closeConnection() {
        connnection.close();
    }
}
