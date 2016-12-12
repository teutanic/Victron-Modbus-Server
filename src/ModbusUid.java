/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */
public class ModbusUid {
 
    private String type;        // the device type - MPPT,BMC,Multi
    private int index;          // the index of the device if more than one
    private int uId;            // the modbus uinit id or slave id 
    private int pingRegister;   // the register to read as a simple ping of the device
    
    
    // a helper class to hold the info about each modbus device connected
    public ModbusUid (String aType,int anIndex, int aUid, int aPingRegister){
        type            = aType;
        index           = anIndex;
        uId             = aUid;
        pingRegister    = aPingRegister;
    }

    // we get asked if we are the right uId for the given device type and index
    // if yes we return the uId
    public int getUidFor (String aType, int anIndex) {
        if (type.equals(aType) && index == anIndex)
            return uId;
        else 
            return -1;
    }
    
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return the uId
     */
    public int getuId() {
        return uId;
    }

    /**
     * @param uId the uId to set
     */
    public void setuId(int uId) {
        this.uId = uId;
    }

    /**
     * @return the pingRegister
     */
    public int getPingRegister() {
        return pingRegister;
    }

    /**
     * @param pingRegister the pingRegister to set
     */
    public void setPingRegister(int pingRegister) {
        this.pingRegister = pingRegister;
    }
    
    
}
