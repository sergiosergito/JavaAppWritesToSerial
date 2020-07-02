package sendToArduino;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.fazecast.jSerialComm.SerialPort;

public class sendToArduino {
	static SerialPort chosenPort;
	
	static JFrame setJFrame() {
		// create and configure the window
		JFrame window = new JFrame();
		window.setTitle("Arduino LCD Clock");
		window.setSize(400, 75);
		window.setLayout(new BorderLayout());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return window;
	}
	
	public static void extracted(JComboBox<String> portList, JButton connectButton) {
		if(connectButton.getText().equals("Connect")) {
			// attempt to connect to the serial port
			chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
			chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
			
			if(chosenPort.openPort()) {
				connectButton.setText("Disconnect");
				portList.setEnabled(false);
				// create a new thread for sending data to the arduino
				sendDataToSerial();
			}
		} else {
			// disconnect from the serial port
			chosenPort.closePort();
			portList.setEnabled(true);
			connectButton.setText("Connect");
		}
	}

	private static void sendDataToSerial() {
		Thread thread = new Thread(){
			@Override public void run() {
				// wait after connecting, so the bootloader can finish
				try {Thread.sleep(100); } catch(Exception e) {}

				// enter an infinite loop that sends text to the arduino
				PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
				String value;
				Scanner scanner = new Scanner(System.in);
				//chosenPort.setBaudRate(9600);
				while(true) {
					value = scanner.next();
					//output.print(new SimpleDateFormat("hh:mm:ss a     MMMMMMM dd, yyyy").format(new Date()));
					output.print(value);
					output.flush();
					////
					//Scanner data = new Scanner(chosenPort.getInputStream());
					//String value = "";
					//System.out.println(chosenPort.getBaudRate());
					
					//while(data.hasNextLine()){
					//	try{value = (data.nextLine());}catch(Exception e){System.out.println(e);}
						//slider.setValue(value);
					//	System.out.println(value);}
					////
					//try {Thread.sleep(100); } catch(Exception e) {}
					
				}
			}
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		JFrame window = setJFrame();
		
		
		// create a drop-down box and connect button, then place them at the top of the window
		JComboBox<String> portList = new JComboBox<String>();
		JButton connectButton = new JButton("Connect");
		JPanel topPanel = new JPanel();
		topPanel.add(portList);
		topPanel.add(connectButton);
		window.add(topPanel, BorderLayout.NORTH);
		
		// populate the drop-down box
		SerialPort[] portNames = SerialPort.getCommPorts();
		for(int i = 0; i < portNames.length; i++)
			portList.addItem(portNames[i].getSystemPortName());
		
		// configure the connect button and use another thread to send data
		connectButton.addActionListener(new ActionListener(){
			@Override public void actionPerformed(ActionEvent arg0) {
				extracted(portList, connectButton);
			}
		});
		
		// show the window
		window.setVisible(true);
	}

}