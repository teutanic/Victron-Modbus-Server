/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import java.util.Observable;
import java.util.Observer;


public class ModbusRegisterObject extends Observable implements Observer{
    
    private String fieldType;
    private int deviceIndex;
    private String sampleType;
    private String registerName;
    private int registerNumber;
    private String valueType;
    private float scaleFactor;
    private boolean isWritable;
    private float sampleFactor;
    private float value;
    private long samplingInterval;
    private long lastUpdate;
    
    private int unitId;
    private int sampleCount = 0;
    private long sampleNumber=0;
    private boolean forceRead = false;
    
    public ModbusRegisterObject ( String aType,int anIndex, String aSampleType, String aName, int aRegisterNumber, 
                                  String aValueType, float aScaleFactor, boolean aFlag, float aSampleFactor ,int aUnitId) {
        
        fieldType           =aType; 
        deviceIndex         =anIndex;
        sampleType          =aSampleType;
        registerName        =aName;
        registerNumber      =aRegisterNumber;
        valueType           =aValueType;
        scaleFactor         =aScaleFactor;
        isWritable          =aFlag;
        sampleFactor        =aSampleFactor;
        unitId              =aUnitId;
        samplingInterval    = 0;
        
        setLastUpdate(System.currentTimeMillis());
        
    }
    
    @Override
    public void update(Observable observable, Object arg) {
        if ((sampleFactor!=0 && this.countObservers()>0) || forceRead){
            // check if we need a new sample 
            // int currentCount =  ((ModbusController)observable).getSampleCount();
            // sample factor example:  5 means 5 samples pro second, each count represents 100ms
            sampleCount++;
            if (sampleCount >= 10/sampleFactor || forceRead) {
                 sampleCount = 0; // reset the count
                 setSamplingInterval(System.currentTimeMillis()-getLastUpdate()); 
                 setLastUpdate(System.currentTimeMillis());
                 // it's our turn, get registered to read
                sampleNumber+=1;
                ((ModbusController)observable).addToFifo(this);
                forceRead = false;
            }           
        }
    }
    
    public void forceRead(){
        forceRead = true;
    }
    
    // We recived the response object. Get the first value and update
    public void updateValue (ReadInputRegistersResponse aResponse) {
        if (aResponse != null){
// System.out.println(samplingInterval + " : " + getRegisterName() + " = " + getValue());
            this.setValue(aResponse.getRegisterValue(0));
        }
    }
     
    // ------------------------------------ Getters and Setters -----------------------
    
// the main runtime function: converting unsingned results
    // into signed ints and notify our observers depending on 
    // sampling type c or d. Only notify when we have observers.
    // the received value is scaled according to the definition file
    /**
     * @param value  the value to set
     */
    public void setValue(int aValue) {
        
        if (valueType.equals("int16")) {    // we need unsigned to signed conversion
            String hex = Integer.toHexString(aValue);
            short s = (short) Integer.parseInt(hex,16);
            if (value != s/scaleFactor || sampleType.equals("c")) {
                value = s/scaleFactor;
                if (this.countObservers()>0){
                    setChanged();
                    notifyObservers();
                }
            }
         }else  // we can take the unsigned value
         if (value != aValue/scaleFactor || sampleType.equals("c")) {        
            value = aValue/scaleFactor;
            if (this.countObservers()>0){
                setChanged();
                notifyObservers();
            }
        }  
    }
    
    /**
     * @return the fieldValue
     */
    public float getValue() {
        return value;
    }
    
    /**
     * @return the fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return the registerName
     */
    public String getRegisterName() {
        return registerName;
    }

    /**
     * @param registerName the registerName to set
     */
    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }

    /**
     * @return the registerNumber
     */
    public int getRegisterNumber() {
        return registerNumber;
    }

    /**
     * @param registerNumber the registerNumber to set
     */
    public void setRegisterNumber(int registerNumber) {
        this.registerNumber = registerNumber;
    }

    /**
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the scaleFactor
     */
    public float getScaleFactor() {
        return scaleFactor;
    }

    /**
     * @param scaleFactor the scaleFactor to set
     */
    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return the isWritable
     */
    public boolean isIsWritable() {
        return isWritable;
    }

    /**
     * @param isWritable the isWritable to set
     */
    public void setIsWritable(boolean isWritable) {
        this.isWritable = isWritable;
    }

    /**
     * @return the sampleFactor
     */
    public float getSampleFactor() {
        return sampleFactor;
    }

    /**
     * @param sampleFactor the sampleFactor to set
     */
    public void setSampleFactor(float sampleFactor) {
        this.sampleFactor = sampleFactor;
    }

    /**
     * @return the unitId
     */
    public int getUnitId() {
        return unitId;
    }

    /**
     * @param unitId the unitId to set
     */
    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the deviceIndex
     */
    public int getDeviceIndex() {
        return deviceIndex;
    }

    /**
     * @param deviceIndex the deviceIndex to set
     */
    public void setDeviceIndex(int deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    /**
     * @return the samplingInterval
     */
    public long getSamplingInterval() {
        return samplingInterval;
    }

    /**
     * @param samplingInterval the samplingInterval to set
     */
    public void setSamplingInterval(long samplingInterval) {
        this.samplingInterval = samplingInterval;
    }

    /**
     * @return the sampleNumber
     */
    public long getSampleNumber() {
        return sampleNumber;
    }

    /**
     * @param sampleNumber the sampleNumber to set
     */
    public void setSampleNumber(long sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    /**
     * @return the lastUpdate
     */
    public long getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate the lastUpdate to set
     */
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


}


