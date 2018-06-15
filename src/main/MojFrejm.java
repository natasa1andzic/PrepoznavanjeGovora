package main;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import wav.WavFileException;

	@SuppressWarnings("serial")
	public class MojFrejm extends JFrame {
	
		private JTextField sirinaProzoraTf;
		JLabel sirinaProzoraLabela=new JLabel("Sirina DFT prozora (ms) :");
		private JTextField redniBrojProzoraTf;
		JLabel redniBrojPrLabela=new JLabel("Redni broj DFT prozora  : ");
		
		
		JRadioButton bez=new JRadioButton("Bez prozorske funkcije");
		JRadioButton hanning=new JRadioButton("Hanning");
		JRadioButton hamming=new JRadioButton("Hamming");
		
		ButtonGroup bGroup = new ButtonGroup();
		JLabel izbor=new JLabel("Izaberi prozorsku funkciju:");
		JPanel radioPanel = new JPanel(new GridLayout(0, 1));
		JButton dugme=new JButton("OK");
		
		public MojFrejm(int pocetakGovora,int krajGovora){
			
		init(pocetakGovora,krajGovora);
		setSize(400, 200);
		setTitle("Izaberi ulazne parametre");
		setLayout(new FlowLayout(FlowLayout.CENTER));
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ImageIcon img = new ImageIcon("graph.png");
		setIconImage(img.getImage());
		
	}
		
	private void init(int pocetakGovora,int krajGovora){
		
		sirinaProzoraTf=new JTextField(20);
		add(sirinaProzoraLabela);
		add(sirinaProzoraTf);
		
		redniBrojProzoraTf=new JTextField(20);
		add(redniBrojPrLabela);
		add(redniBrojProzoraTf);
		
		bez.setSelected(true);
		@SuppressWarnings("unused")
		ButtonGroup group = new ButtonGroup();
	    bGroup.add(bez);
	    bGroup.add(hanning);
	    bGroup.add(hamming);
	    
		add(izbor);
		
	
        radioPanel.add(bez);
        radioPanel.add(hanning);
        radioPanel.add(hamming);
        add(radioPanel);
        
        JLabel govor=new JLabel("Glas pocinje na "+pocetakGovora+"ms, a zavrsava se na "+krajGovora+"ms");
        add(govor);
     
	
		dugme.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int f=0;
				if (hanning.isSelected())
					f=1;
				if(hamming.isSelected())
					f=2;
				try {
					Main.izbor(Integer.parseInt(sirinaProzoraTf.getText()), f, Integer.parseInt(redniBrojProzoraTf.getText()));
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (WavFileException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		add(dugme);
		
	}
}
