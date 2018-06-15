package main;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import wav.WavFile;
import wav.WavFileException;


public class Main {
	
			private static String fileName="stanje.wav";
	
			private static ArrayList<Integer> a=new ArrayList<Integer>();
			private static ArrayList<Integer> b=new ArrayList<Integer>();
			private static ArrayList<Integer> c=new ArrayList<Integer>();
			private static ArrayList<Integer> prvi=new ArrayList<Integer>();

			private static int[] rezultati; 
	
			private static double nivoSuma=0;
			private static double standardnaDevijacija=0;
			private static double standardnaDevijacijaZCR=0; 
			private static double zeroCrossRate=0;
	
			private static int pocetakGovora=-1;
			private static int krajGovora=-1;
	
			//jedan kanal, 16bit po kanalu, 22050
			private static final int sirinaS=20; //sirina za spustanje jedinica u ms
			private static final int brS=9;      //max broj jedinica koje mogu da se spuste
			private static final int sirinaP=20; // sirina za podizanje nula
			private static final int brP=9;      // max broj nula koje mogu da se podignu
			private static final int sampleRate=/*44100*/22050;  // jer je jedan kanal
			private static final int vremeCutanja=100; //u ms
			private static final int sirinaProzora=/*441*/220; //broj semplova (u 10ms)
		
	
   /*I*/	public static void izbor(int sirinaProzora, int prozorskaFunkcija, int redniBrojProzora) throws IOException, WavFileException{
		
				double []niz;
				int sirinaProzoraMS=sirinaProzora;
				sirinaProzora=sirinaProzora*sampleRate/1000;  
		
				if (sirinaProzora==0){                        //ako ne izaberemo sirinu prozora
					int a = 1;
					int length=rezultati.length;               
					while (a<length)                           // Nikvistova teorema
						a *= 2;
					length = a;
					niz=new double[length];
					for (int i=0;i<rezultati.length;i++)
						niz[i]=1.0*rezultati[i];               //mnozimo sa 1 da bi bio double
				}
		
				else{											// ako korisnik unese ispravnu sirinu prozora
				
					int brojProzora=rezultati.length/sirinaProzora;
					if (redniBrojProzora<brojProzora && redniBrojProzora>-1){
						int a = 1;
						int length=sirinaProzora;
						while(a<length) 
							a *= 2;  
						length = a;
						niz=new double[length];
						for (int i=0;i<sirinaProzora;i++)
							niz[i]=1.0*rezultati[i+redniBrojProzora*sirinaProzora];	
					}
			
					else{	                                        // ako korisnik unese pogresnu vrednost
			
						int a = 1;
						int length=rezultati.length;
						while(a<length) 
							a *= 2;
						length = a;
						niz=new double[length];
						for (int i=0;i<rezultati.length;i++)
							niz[i]=1.0*rezultati[i];
				
					}
				}
		
				if (prozorskaFunkcija==1)
					Hanning(niz, niz.length);
		
				if (prozorskaFunkcija==2)
					Hamming(niz, niz.length);
		
				double [] nizIm=new double[niz.length];                //popunjavanje nulama
				for (int i=0;i<niz.length;i++)
					nizIm[i]=0;
				double T=1.0*niz.length/sampleRate; //br elemenata u nizu: xs=sampleRate:1ms formula 
		
				if ((sirinaProzoraMS==0) || (sirinaProzoraMS>krajGovora-pocetakGovora))  //ako je uneta sirina prozora veca od postojeceg
					sirinaProzoraMS=krajGovora-pocetakGovora;
		
				if (redniBrojProzora<0 || redniBrojProzora>(krajGovora-pocetakGovora)/sirinaProzoraMS){  //ako je uneta nekorektna vrednost
					redniBrojProzora=0;
					sirinaProzoraMS=krajGovora-pocetakGovora;
				}
		
				Result.results(niz, nizIm, T,prozorskaFunkcija,sirinaProzoraMS,redniBrojProzora);  //izracunava FFT
	}
	
 /*II*/  	private static void DFT(int dir,int m,double []x1,double []y1) {
		
   				int i, k;
   				double arg;
   				double cosarg,sinarg;
   				double[] x2=new double[m];
   				double[] y2= new double[m];
   				for (i=0;i<m;i++) {
   					x2[i] = 0;
   					y2[i] = 0;
   					arg = - dir * 2.0 * 3.141592654 * (double)i / (double)m;
   					for (k=0;k<m;k++) {
   						cosarg = Math.cos(k * arg);
   						sinarg = Math.sin(k * arg);
   						x2[i] += (x1[k] * cosarg - y1[k] * sinarg);
   						y2[i] += (x1[k] * sinarg + y1[k] * cosarg);
   					}
   				}

   				if (dir == 1) {
   					for (i=0;i<m;i++) {
   						x1[i] = x2[i] / (double)m;
   						y1[i] = y2[i] / (double)m;
   					}
   				} else {
   					for (i=0;i<m;i++) {
   						x1[i] = x2[i];
   						y1[i] = y2[i];
   					}
   				}
   		}

 /*III*/  	private static void Hanning(double [] niz, int brSemplova){
	
   					double []hanning=new double[brSemplova];
   					for (int i=0;i<brSemplova;i++){
   						hanning[i]=(1-Math.cos(2*Math.PI*i/(brSemplova-1)))/2;
   					}
   					for (int i=0;i<niz.length;i++){
   						niz[i]=niz[i]*hanning[i];
   					}
   				}

 /*IV*/  	private static void Hamming(double [] niz, int brSemplova){
	
   				brSemplova=niz.length;
   				double []hamming=new double[brSemplova];
   				for (int i=0;i<brSemplova;i++)
   					hamming[i]=0.54-0.46*Math.cos(2*Math.PI*i/(brSemplova-1));
   				for (int i=0;i<niz.length;i++)
   					niz[i]=niz[i]*hamming[i];  
   			}		
   
/*V*/		private static void ucitajWav() throws IOException, WavFileException{
	
				File file=new File(fileName);
				WavFile wavFile = WavFile.openWavFile(file);
				int[] niz=new int[(int) wavFile.getNumFrames()];
				WavFile.readFrames(niz,(int)wavFile.getNumFrames());

				for (int i=0;i<niz.length;i++){
					prvi.add(niz[i]);
					a.add(Math.abs(niz[i]));
				}
				wavFile.close();  
			}

/*VI*/		private static void odrediZCR() {
	
				int brojac=0,suma=0;
				boolean poz=true; // koliko puta ode iz plusa u minus, pretpostavimo da je prvo pozitivan
				int a[]=new int[10];
				int brojProzora=10;
				for (int i=0;i<brojProzora;i++){	//deset delova jer delim 100ms u 10 grupa
					brojac=0;
					for (int j=0;j<sampleRate*(vremeCutanja/brojProzora)/1000;j++){		
						if (prvi.get(j+i*sampleRate*(vremeCutanja/brojProzora)/1000)>0){	
							poz=true;
						}
						else{
							if (poz){
								poz=false;
								brojac++;
							}
						}
					}
					a[i]=brojac;
					suma+=brojac;
				}
				zeroCrossRate=suma/brojProzora;
				for (int i=0;i<brojProzora;i++){
					standardnaDevijacijaZCR+=(a[i]-zeroCrossRate)*(a[i]-zeroCrossRate);
				}
				standardnaDevijacijaZCR=Math.sqrt(standardnaDevijacijaZCR/(brojProzora-1));
				}

/*VII*/		private static void obeleziGlasneSemplove(){
	
				double snaga;
				int indeks=sampleRate*vremeCutanja/1000; //22050*100/1000

				for (int i=indeks/sirinaProzora;i<a.size()/sirinaProzora;i++){
					snaga=0;
					for (int j=0;j<sirinaProzora;j++){
						snaga+=a.get(indeks);
						indeks++;
					}
					if (snaga/sirinaProzora>nivoSuma+standardnaDevijacija)
						b.add(1);
					else b.add(0);
				}
				snaga=0;
				int br=0;
				while (indeks<a.size()){
					snaga+=a.get(indeks);
					indeks++;
					br++;
				}
				if (snaga/br>nivoSuma+standardnaDevijacija)
					b.add(1);
				else b.add(0);	
				}


/*VIII*/	private static void podigniRuzno(){
	
				int brojac=0;
				for (int i=0;i<b.size()-sirinaP;i++){
					brojac=0;
						for (int j=0;j<sirinaP;j++){
							if(b.get(i+j)==0)
								brojac++;
							}
						if (brojac<brP && brojac>0)
							for (int j=i;j<i+sirinaP/2;j++)
								b.set(j, 1);
					}
				}


/*IX*/		private static void spustiRuzno(){

				int brojac=0;
				for (int i=0;i<b.size()-sirinaS;i++){
					brojac=0;
					for (int j=i;j<i+sirinaS;j++){
						if(b.get(j)==1)
							brojac++;
					}
					if (brojac<brS && brojac>0){
						int pb=0;
						for(int j=i;j>i-10;j--){
							if (j>=0 && j<b.size() && b.get(j)==1)
								pb++;
						}
						if (pb<5){
							for (int j=i;j<i+sirinaS/2;j++)
								b.set(j, 0);
						}
					}	
				}
			}

/*X*/		private static void proveriZCR() {
	
				int brojac;
				boolean poz=true; 

				for(int i=10;i<a.size()/sirinaProzora;i++){
					brojac=0;
					c.add(0);
					for (int j=0;j<sirinaProzora;j++){
						if (a.get(i*sirinaProzora+j)>0)
							poz=true;
						else
							if (poz){
								poz=false;
								brojac++;
							}
					}
					if (brojac>zeroCrossRate+standardnaDevijacijaZCR || brojac>25)
						c.set(i-10,1);
				}
			}

/*XI*/		private static void spojiYIZCR() {
	
				int brojProzora=20;//20 * 10ms
				for(int i=0;i<b.size();i++){
					if(b.get(i)==1){
						if (i>0 && b.get(i-1)==0)
							for (int j=i-1;j>i-brojProzora;j--){
								if (j<0)
									break;
								if (c.get(j)==1)
									b.set(j, 1);
								}
			
						if(i<b.size()-1 && b.get(i+1)==0){
							for (int j=i+1;j<i+brojProzora;j++){
								if (j>=b.size()-1)
									break;
								if (c.get(j)==1)
									b.set(j, 1);
							}
							i=i+brojProzora;
						}
					}
				}
			}

/*XII*/		private static void nadjiGovornuOkolinu(){ 
				
				int duzinaGovora=0;
				ArrayList<Integer> okolinaGovora=new ArrayList<Integer>();
				for (int i=0;i<b.size();i++)
					okolinaGovora.add(0);
				for (int i=0;i<b.size();i++){
					if (b.get(i)==1){
						duzinaGovora++;
						okolinaGovora.set(i, duzinaGovora);
					}
					else {
						duzinaGovora=0;
					}
				}

				int max=0,imax=b.size()-1;
				for (int i=b.size()-1;i>=0;i--){
					if (okolinaGovora.get(i)>max){
						max=okolinaGovora.get(i);
						imax=i;
					}
				}
		
				int i=imax;
				while(b.get(i)==1 && i>=0){
					b.set(i,2);
					i--;
				}
				
				for (int j=0;j<b.size();j++){
					if ((b.get(j)==2) &&(pocetakGovora==-1)){
						pocetakGovora=100+j*10;
					}
					if (b.get(j)==2)
						krajGovora=100+(j+1)*10;
				}
			}
	



 /*XIII*/	private static void endpointing() throws IOException, WavFileException{
		
					ucitajWav();
				
					//odredjivaje nivoa suma u vremenu cutanja - 100ms
					for(int i=0;i<sampleRate*vremeCutanja/1000;i++)		//sampleRate:1000ms=x:vremeCutanja
						nivoSuma+=a.get(i);
				
					//nadji standardnu devijaciju
					nivoSuma/=(sampleRate*vremeCutanja/1000);
					for(int i=0;i<sampleRate*vremeCutanja/1000;i++)		//sampleRate:1000ms=x:vremeCutanja
						standardnaDevijacija+=(a.get(i)-nivoSuma)*(a.get(i)-nivoSuma);
				
					standardnaDevijacija=Math.sqrt(standardnaDevijacija/(sampleRate*vremeCutanja/1000-1));

					odrediZCR();				
					obeleziGlasneSemplove(); // da li je glasniji od sredine
					podigniRuzno(); 
					spustiRuzno();
					proveriZCR();
					spojiYIZCR();
					nadjiGovornuOkolinu();
				
					File file=new File(fileName);
					WavFile wav=WavFile.openWavFile(file);
					rezultati=new int[sampleRate*(krajGovora-pocetakGovora)/1000];
					int framesRead=wav.readFrames(new int[sampleRate*pocetakGovora/1000], sampleRate*pocetakGovora/1000);
					framesRead=wav.readFrames(rezultati, sampleRate*(krajGovora-pocetakGovora)/1000);
			}
	
		public static void main(String[] args) throws IOException, WavFileException{
			
				endpointing();
				MojFrejm.setDefaultLookAndFeelDecorated(true);
				new MojFrejm(pocetakGovora,krajGovora);
		}
	}