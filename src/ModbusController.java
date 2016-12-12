/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */

import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;


/* 
 * The ModusController is the central mechanism to organise access to the
 * modbus and the CCGX. The field definitions are read from a csv file 
 * and the reader object is instantiated. The controller is the scheduler 
 * for all modbus access and has a task list which is sequentially worked
 * through for all the field requests. The controller has a timer handling 
 * the highest sampling rate and all other fields will be handled depending 
 * on their sampling rate. Some call the task list also a stack and the 
 * type would be a fifo - first in first out 
 */
public class ModbusController extends Observable{
    
    private final int                               timeBase = 100;                           // 100ms is our current time base - change for slower or fater sampling
    private static ModbusDefinitions                fieldDefinitions;                         // only one list of all field definitions
    private static ModbusInterface                  modbusReader;                             // only one reader instance to avoid conflicts    
    private ArrayList<ModbusRegisterObject>         fifoForRead;                              // the read list (stack)
    public static ModbusController                  mbController;                             // A global variable to find this class without explicit reference
    
    private Timer t = new java.util.Timer();
    
    public ModbusController(){
        
        mbController=this;                                                                  // let others find us easily
        modbusReader = new ModbusInterface();                                                  // instantiate the actual modbus interface class
        fieldDefinitions = new ModbusDefinitions((Observable)this);                         // get all the definitions
        fifoForRead = new ArrayList<ModbusRegisterObject>() ;                               // the read list (stack)
        modbusReader.connectGateway();
        
        if (modbusReader.isConnected() ) {                                                // connect to modbus and we will keep it connected
            // modbus connected we can start reading data 
            
            System.out.println("Modbus connected"); 
                                                                             // start sampling 
        }else {
            System.out.println("Modbus connection failure");                                // we had an error
        }
    }

    public void startSampling() {
        setTimer();        
    }
    
    public boolean isActive(String deviceType, int index) {
        int regNum = fieldDefinitions.findPingRegister(deviceType, index);
        if (regNum > -1) {
            ModbusRegisterObject aRegisterObject = fieldDefinitions.findRegisterObject(regNum, index);
            if (aRegisterObject != null) {
                ReadInputRegistersResponse aResponse = modbusReader.readRegister(aRegisterObject.getUnitId(), regNum);
                if (aResponse != null) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean ping(String deviceType, int index) {
        boolean aFlag = isActive(deviceType, index);
        if (!aFlag) {
            System.out.println("ping retry");
            aFlag = isActive(deviceType, index);
            return aFlag;
        }
        return false;
    }
    // find the corresponding registerObject for a field number and device index
    public ModbusRegisterObject findRegisterObject(int registerNumber, int index){
            return fieldDefinitions.findRegisterObject(registerNumber,index);
    }
    
    // add a read request 
    public void addToFifo(ModbusRegisterObject anObject){    
         
         fifoForRead.add(anObject);
    }
    
    // send notifications
    private void notifyListeners() {
        setChanged();
        notifyObservers();
    }
    
    // the sampling thread
    private void setTimer() {
            
            TimerTask tt = new java.util.TimerTask() {
            @Override
            public void run() {            
                notifyListeners();
                if (fifoForRead.size() > 0) {
                    // work through the list of checked in objects and read the registers
                    ModbusRegisterObject registerObject = fifoForRead.get(0);
                    if (registerObject != null) {
                        // we have an object - read it
                        ReadInputRegistersResponse aResponse = modbusReader.readRegister(registerObject.getUnitId(), registerObject.getRegisterNumber());
                        if (aResponse != null) {
                            registerObject.updateValue(aResponse);
                        }else {
                            registerObject.deleteObservers();       // make sure it does not bother us again
                        }
                    }
                    fifoForRead.remove(0);      // take the request off the stack, we are done with it 
                }
            }
        };
        t.schedule(tt, timeBase ,timeBase);    // our time Base for the timer interrupt (min sampling rate)         
    }
}
