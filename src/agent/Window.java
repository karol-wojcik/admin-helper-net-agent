package agent;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;

public class Window extends JFrame implements ActionListener {
	/**
	 * 
	 */
	JButton bStartPingTest,  bStopPingTest;
	private static final long serialVersionUID = 1L;

	public Window(){
		setSize(400, 300);
		// Get the size of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		 
		// Determine the new location of the window
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width-w)/2;
		int y = (dim.height-h)/2;
		 
		// Move the window
		setLocation(x, y);
		
		setTitle("NetAgent");
		setLayout(null);
		
		bStartPingTest = new JButton("START");
		bStartPingTest.setBounds(75, 50, 100, 30);
		add(bStartPingTest);
		bStartPingTest.addActionListener(this);
		
		bStopPingTest = new JButton("STOP");
		bStopPingTest.setBounds(180, 50, 100, 30);
		add(bStopPingTest);
		bStopPingTest.addActionListener(this);
	}
	
	public static void main(String[] args) {
		Window w = new Window();
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == bStartPingTest) {
			
			ClassExecutingTask executor = new ClassExecutingTask();
			System.out.println("pocz¹tek pingtestu");
			executor.runPingTest();
			System.out.println("Wykona³ siê pingtest");
			
			bStartPingTest.setEnabled(false);
			bStopPingTest.setEnabled(true);
			
		} else if (src == bStopPingTest) {
			dispose();
		}
	}
	
	
}
