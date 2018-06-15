package main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import wav.WavFileException;


public class Result {
	
	public static void results(double[] sound, double[] soundim,double T,int prozorskaFunkcija,int sirinaProzora,int redniBrojProzora) throws IOException, WavFileException {		
	
		double[] voiceoutim = FFTbase.fft(sound, soundim, true); //rezultat fft
		int length=sound.length;
		
		//ispis nizova
		int k;
		double[] x = new double[length / 2];
		double[] y = new double[length / 2];
		double[] ypom=new double[length/2];
		for (k = 1; k < voiceoutim.length / 2; k++) { 
			double f=1.0 * k / (T);
			x[k-1] = 10*Math.log10(f/20);
			y[k-1] = Math.abs(voiceoutim[k]); // da bi sve bile pozitivne
			ypom[k-1]=y[k-1]/2;
		}
		

		XYChart chart = new XYChartBuilder().width(800).height(600).title("Frekventni spektar").xAxisTitle("X - frekvencije").yAxisTitle("Y - amplituda frekvencije").build();
	    chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
	    chart.getStyler().setChartTitleVisible(true);
	    chart.getStyler().setLegendVisible(false);
	    chart.getStyler().setAxisTitlesVisible(true);	 
	    XYSeries series = chart.addSeries("wd", x, ypom,ypom);
	    series.setMarker(SeriesMarkers.NONE);
	    JDialog.setDefaultLookAndFeelDecorated(true);
	    JDialog c=new JDialog();
	    c.setSize(800,600);
	    c.add(new XChartPanel(chart));
	    c.setVisible(true);
	    c.setLocationRelativeTo(null);
	    
	    if (prozorskaFunkcija==0){
	    	c.setTitle("Bez prozorske funkcije, sirina prozora "+sirinaProzora+"ms, redni br. "+redniBrojProzora);
	    }
	    if (prozorskaFunkcija==1){
	    	c.setTitle("Prozorska funkcija Hanning, sirina prozora "+sirinaProzora+"ms, redni br. "+redniBrojProzora);
	    }
	    if (prozorskaFunkcija==2){
	    	c.setTitle("Prozorska funkcija Hamming, sirina prozora "+sirinaProzora+"ms, redni br. "+redniBrojProzora);
	    }
	}
}
