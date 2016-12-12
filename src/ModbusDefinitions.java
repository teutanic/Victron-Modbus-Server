/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;


public class ModbusDefinitions {
    
    /*
     * This class contains all field definitions and related info 
     * for reading information from Victron devices over modbus
    */
    
    private ArrayList<ModbusRegisterObject> fieldList;                              // The list of all fileds I intend to monitor
    private ArrayList<ModbusUid> uidList ;                                          // the list of devices
         
    
    public ModbusDefinitions(Observable notifier){
        String csvFile = "fieldlist.csv";                                           // read from resources - fixed name - change if you need to
        String line = "";
        fieldList   = new ArrayList<ModbusRegisterObject>();                          // create the list 
        uidList     = new ArrayList<ModbusUid> ();
        // before wered the definitions from the csv file, 
        // we create the device list hardcoded
        // this list is used to locate the uId for each device
        // this data is fairly fixed 
        uidList.add( new ModbusUid("MPPT", 0, 0, 774));         // Ve.can on CCGX starts with device id 0
        uidList.add( new ModbusUid("MPPT", 1, 1, 774));         // index 1 is the second MPPT controller at aId 1
        uidList.add( new ModbusUid("BMV",  0, 245, 259));       // the BMV on Ve-direct 1
        uidList.add( new ModbusUid("BMV",  1, 247, 259));       // the BMV on Ve-direct 2
        uidList.add( new ModbusUid("Multi",0, 246, 33));        // the pair of multis appears as one 

        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");                                  // standard comma delimited
                // get the fields and convert to required format
                String fieldType    = fields[0];                                    // the device type - MPPT, BMV, MULTI in my installation
                int deviceIndex     = Integer.parseInt(fields[1]);                  // the device index if more than one device is connected of the same type
                String sampleType   = fields[2];                                    // c = continuous (change notification with each sample, d = discrete (change notification only when changed) 
                String registerName = fields[3];                                    // the meaning of the field as defined by Victron
                int registerNumber  = Integer.parseInt(fields[4]);                  // the fieldnumber as per xcel file from Victron 
                String valueType    = fields[5];                                    // uint16 and int16 are supported, no string yet
                float scaleFactor   = Float.parseFloat(fields[6]);                  // the scale factor of the result as per xcel field list
                boolean isWritable  = Boolean.parseBoolean(fields[7]);              // flag if you can write the field as per xcel file
                float sampleFactor  = Float.parseFloat(fields[8]);                  // a factor based on timeBase for the sampling of the register
                // create the field Objects and add to list
                ModbusRegisterObject registerObject =  new ModbusRegisterObject( fieldType, deviceIndex , sampleType ,registerName, registerNumber, 
                                                                                 valueType, scaleFactor,isWritable,sampleFactor, 
                                                                                 findUid(fieldType, deviceIndex) );         // fill in the uID here to avoid permanent lookups
                
                notifier.addObserver(registerObject);                               // The new object is an observer for the controller(notifier) to receive timer ticks               
                fieldList.add(registerObject);                                      // keep track of the object in our fieldlist
            }
            // the field list exists now 
            
        } catch (IOException e) {
            System.out.println("Definition file (csv) not found");
        }
    }

    // finding the uId object for the device with index
     public ModbusUid findUidObject(String aType, int anIndex) {
          for (int i=0; i< uidList.size(); i++) {
              ModbusUid aUidObject = uidList.get(i);
              if ( aUidObject.getType().equals(aType) && (aUidObject.getIndex()==anIndex) ){
                  return aUidObject;
             }
          }
          return null;
    }
     
    // finding the pingRegister for the device with index
    public int findPingRegister(String aType, int anIndex) {
          ModbusUid aUidObject = findUidObject(aType,anIndex);
          if (aUidObject != null)
              return aUidObject.getPingRegister();
          else
              return -1;
    }

    // finding the proper uId for the device with index
    public int findUid(String aType, int anIndex) {
          ModbusUid aUidObject = findUidObject(aType,anIndex);
          if (aUidObject != null)
              return aUidObject.getuId();
          else
              return -1;
    }
    
    // return the number of definition objects
    public int numberOfFields(){
        return fieldList.size();
    }
    // return the register object at the given index
    public ModbusRegisterObject registerObjectAt(int index){
        if (index>=0 && index < fieldList.size())
            return fieldList.get(index);
        else
            return null;        // not found - I am not a friend of exceptions for this sort of situation
    }
    // find the object for a field (register) but consider for which device (index)
    public ModbusRegisterObject findRegisterObject(int registerNumber, int index){
        for (int i=0; i< fieldList.size();i++) {
            ModbusRegisterObject registerObject = fieldList.get(i);
            if (registerObject.getRegisterNumber() == registerNumber && registerObject.getDeviceIndex() == index) {
                return registerObject;  // we found the correct object
            }
        }
        return null;    // not found
    }
}
