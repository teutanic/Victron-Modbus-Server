

/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */
public class Modbus {
          
       
       // just a little testprogram before I include this in the main app
    
       public static void main(String[] args) {
    
             new ModbusController();                    // this activates all classes for the modbus server
             TestWindow window = new TestWindow();      // this is the application part
             window.setVisible(true);                   // show the little test input window

       }
    
}
