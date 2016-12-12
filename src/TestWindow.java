/**
 *
 * @author Copyright Inno-VAN-tion Perth, 2016 
 *         You are free to use with a link to teutanic.com or teutanic.com.au
 *         Author is not responsible for any misuse or consequential damage of
 *         use with any Victron components or any other modbus unit.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 *
 * @author gmd
 */
public class TestWindow extends JFrame implements Observer {

    JLabel valueDisplayField;
    JTextField inputRegisterNumber;
    JTextField inputDeviceIndex;
    JLabel nameDisplayField;
    JLabel timeDisplayField;
    JLabel sampleCount;

    ModbusRegisterObject currentObservable = null;

    // just a quick and dirty display - nothing fancy
    public TestWindow() {
        super();
        setResizable(false);
        setSize(300, 400);
        setTitle("Modbus TestWindow");
        getContentPane().setBackground(new Color(0, 0, 0));
        getContentPane().setLayout(null);


        sampleCount = new JLabel("0");
        sampleCount.setBounds(20+110, 30+0, 30, 29);
        sampleCount.setBackground(new Color(255, 255, 255));
        sampleCount.setForeground(new Color(0, 255, 153));
        getContentPane().add(sampleCount);
        
        valueDisplayField = new JLabel("....");
        valueDisplayField.setBounds(20+0, 30+60, 100, 29);
        valueDisplayField.setBackground(new Color(255, 255, 255));
        valueDisplayField.setForeground(new Color(0, 255, 153));
        getContentPane().add(valueDisplayField);

        nameDisplayField = new JLabel(" ");
        nameDisplayField.setBounds(20+0, 30+90, 100, 29);
        nameDisplayField.setBackground(new Color(255, 255, 255));
        nameDisplayField.setForeground(new Color(0, 255, 153));
        getContentPane().add(nameDisplayField);
        
        timeDisplayField = new JLabel(" ");
        timeDisplayField.setBounds(20+0, 30+120, 100, 29);
        timeDisplayField.setBackground(new Color(255, 255, 255));
        timeDisplayField.setForeground(new Color(0, 255, 153));
        getContentPane().add(timeDisplayField);
        
        inputRegisterNumber = new JTextField("23");
        inputRegisterNumber.setBounds(20+0, 30+0, 50, 29);
        inputRegisterNumber.setForeground(new Color(0, 0, 0));
        getContentPane().add(inputRegisterNumber);

        inputDeviceIndex = new JTextField("0");
        inputDeviceIndex.setBounds(20+50, 30+0, 50, 29);
        inputDeviceIndex.setForeground(new Color(0, 0, 0));
        getContentPane().add(inputDeviceIndex);
        
        JButton btn = new JButton("Read");
        btn.setFont(new Font("Tahoma", Font.PLAIN, 10));
        // this is the actual read action 
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                readField(Integer.parseInt(inputRegisterNumber.getText()),      // the field number
                          Integer.parseInt(inputDeviceIndex.getText()));        // the device index
            }
        });
        btn.setBounds(20+0, 30+30, 100, 29);
        getContentPane().add(btn);
        
        JButton stpbtn = new JButton("Stop");
        stpbtn.setFont(new Font("Tahoma", Font.PLAIN, 10));
        // this is the actual read action 
        stpbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stop();
            }
        });
        stpbtn.setBounds(20+0, 30+160, 100, 29);
        getContentPane().add(stpbtn);
    }

    // with read field  we make this instance an observer of the given 
    // registerObject. As soon as the object has an observer it will 
    // send notifications when changed or when sampled depending on the 
    // sampling type 
    // this test only allows for one active read due to the limitation 
    // of one display field. a previous observer will be removed 
    public void readField(int register,int index) {
        ModbusRegisterObject anObject = ModbusController.mbController.findRegisterObject(register,index);
        stop();                         // stop any current observers
        if (anObject != null) {
            anObject.addObserver(this);
        }else {
            valueDisplayField.setText("unknown");
        }
    }
    
    private void stop() {
        if (currentObservable != null) {
                currentObservable.deleteObserver(this);
        }
    }
    // this is the actual update method invoked by the change notification 
    // of the linked observable registerObject.
    @Override
    public void update(Observable observable, Object arg) {
        ModbusRegisterObject anObject = (ModbusRegisterObject) observable;
        valueDisplayField.setText(String.format("%.2f", anObject.getValue()));
        nameDisplayField.setText(anObject.getRegisterName());
        timeDisplayField.setText(Integer.toString((int)anObject.getSamplingInterval())+ " ms");
        sampleCount.setText(Integer.toString((int)anObject.getSampleNumber()));
        
        currentObservable = anObject;

    }
}
