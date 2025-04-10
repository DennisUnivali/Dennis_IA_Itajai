import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.awt.image.*;
import javax.imageio.ImageIO;


public class GamePanel extends Canvas implements Runnable
{
private static final int PWIDTH = 960;
private static final int PHEIGHT = 800;
private Thread animator;
private boolean running = false;
private boolean gameOver = false; 


int FPS,SFPS;
int fpscount;

public static Random rnd = new Random();

//BufferedImage imagemcharsets;

boolean LEFT, RIGHT,UP,DOWN;

public static int mousex,mousey; 

public static ArrayList<Agente> listadeagentes = new ArrayList<Agente>();

Mapa_Grid mapa;

double posx,posy;

MeuAgente meuHeroi = null;

//TODO ESSE È O RESULTADO
int caminho[] = null;

float zoom = 1;

int ntileW = 60;
int ntileH = 50;

Font f = new Font("", Font.BOLD, 20);

public GamePanel()
{

	setBackground(Color.white);
	setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

	// create game components
	setFocusable(true);

	requestFocus(); // JPanel now receives key events	
	
	
	// Adiciona um Key Listner
	addKeyListener( new KeyAdapter() {
		public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = true;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = true;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = true;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = true;
				}	
			}
		@Override
			public void keyReleased(KeyEvent e ) {
				int keyCode = e.getKeyCode();
				
				if(keyCode == KeyEvent.VK_LEFT){
					LEFT = false;
				}
				if(keyCode == KeyEvent.VK_RIGHT){
					RIGHT = false;
				}
				if(keyCode == KeyEvent.VK_UP){
					UP = false;
				}
				if(keyCode == KeyEvent.VK_DOWN){
					DOWN = false;
				}
			}
	});
	
	addMouseMotionListener(new MouseMotionListener() {
		
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			mousex = e.getX(); 
			mousey = e.getY();
			

		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			if(e.getButton()==3){
				int mousex = (int)((e.getX()+mapa.MapX)/zoom);
				int mousey = (int)((e.getY()+mapa.MapY)/zoom);
				
				int mx = mousex/16;
				int my = mousey/16;
				
				if(mx>mapa.Altura) {
					return;
				}
				if(my>mapa.Largura) {
					return;
				}
				
				mapa.mapa[my][mx] = 1;
			}
		}
	});
	
	addMouseListener(new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			//System.out.println(" "+arg0.getButton());
			int mousex = (int)((arg0.getX()+mapa.MapX)/zoom);
			int mousey = (int)((arg0.getY()+mapa.MapY)/zoom);
			
			//System.out.println(""+arg0.getX()+" "+mapa.MapX+" "+zoom);
			//System.out.println(""+mousex+" "+mousey);
			
			int mx = mousex/16;
			int my = mousey/16;
			
			if(mx>mapa.Altura) {
				return;
			}
			if(my>mapa.Largura) {
				return;
			}
			
			if(arg0.getButton()==3){

				
				if(mapa.mapa[my][mx]==0){
					mapa.mapa[my][mx] = 1;
				}else{
					mapa.mapa[my][mx] = 0;
				}
			}
			if(arg0.getButton()==1){
				if(mapa.mapa[my][mx]==0) {
					caminho = null;
					long timeini = System.currentTimeMillis();

					// TODO Executa Algoritmo
					System.out.println(""+my+" "+mx);
					System.out.println("meueroi "+(int)(meuHeroi.X/16)+" "+(int)(meuHeroi.Y/16));
					rodaBuscaProfundidade((int)(meuHeroi.X/16),(int)(meuHeroi.Y/16),mx,my);

					long timefin = System.currentTimeMillis() - timeini;
					System.out.println("Tempo Final: "+timefin);
				}else {
					System.out.println("Caminho Final Bloqueado");
				}
			}
		}
		
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	});
	
	addMouseWheelListener(new MouseWheelListener() {
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			//System.out.println("w "+e.getWheelRotation());
			if(e.getWheelRotation()>0) {
				zoom= zoom*1.1f;
			}else if(e.getWheelRotation()<0) {
				zoom= zoom*0.90f;
			}
			
			ntileW = (int)((960/zoom)/16)+1;
			ntileH = (int)((800/zoom)/16)+1;
			
			if(ntileW>=1000) {
				ntileW = 1000;
			}
			if(ntileH>=1000) {
				ntileH = 1000;
			}
			mapa.NumeroTilesX = ntileW;
			mapa.NumeroTilesY = ntileH;
		}
	});

	meuHeroi = new MeuAgente(10, 10, Color.blue);
	
	listadeagentes.add(meuHeroi);
	
	mousex = mousey = 0;
	
	mapa = new Mapa_Grid(100,100,ntileW, ntileH);
	mapa.loadmapfromimage("/imagemlabirinto1000.png");
	
} // end of GamePanel()

//LinkedList<Nodo> nodosPercorridos = new LinkedList();
HashSet<Integer> nodosPercorridos = new HashSet<Integer>();
public boolean jaPassei(int nX,int nY) {
	return nodosPercorridos.contains(nX+nY*1000);
}
LinkedList<Nodo> pilhaprofundidade = new LinkedList();

public boolean rodaBuscaProfundidade(int iniX,int iniY,int objX,int objY) {
	Nodo nodoAtivo = new Nodo(iniX, iniY);
	pilhaprofundidade.add(nodoAtivo);
	
	while(pilhaprofundidade.size()>0) {
		//System.out.println(""+nodoAtivo.x+" "+nodoAtivo.y+" | "+objX+" "+objY);
		
		if(nodoAtivo.x==objX&&nodoAtivo.y==objY) {
			caminho = new int[pilhaprofundidade.size()*2];
			int index = 0;
			for (Iterator iterator = pilhaprofundidade.iterator(); iterator.hasNext();) {
				Nodo n = (Nodo) iterator.next();
				caminho[index] = n.x;
				caminho[index+1] = n.y;
				index+=2;
			}
			return true;
		}
		
//		if(mapa.mapa[nodoAtivo.y][nodoAtivo.x]==1) {
//			pilhaprofundidade.removeLast();
//			nodoAtivo=pilhaprofundidade.getLast();
//			continue;
//		}
		
		synchronized (nodosPercorridos) {
			//nodosPercorridos.add(nodoAtivo);
			nodosPercorridos.add(nodoAtivo.x+nodoAtivo.y*1000);
		}
		
		//if(jaPassei(iniX,iniY)) {
		//	return false;
		//}/
		
		//synchronized (nodosPercorridos) {
		//	nodosPercorridos.add(new Nodo(iniX,iniY));
		//}
	
		
//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		Nodo t[] = new Nodo[4];
		t[0] = new Nodo(nodoAtivo.x, nodoAtivo.y+1);
		t[1] = new Nodo(nodoAtivo.x+1, nodoAtivo.y);
		t[2] = new Nodo(nodoAtivo.x, nodoAtivo.y-1);
		t[3] = new Nodo(nodoAtivo.x-1, nodoAtivo.y);
		
		boolean ok = false;
		for(int i = 0; i < 4; i++) {
			if(t[i].y<0||t[i].y>=1000||t[i].x<0||t[i].x>=1000) {
				continue;
			}
			if(mapa.mapa[t[i].y][t[i].x]==0&&jaPassei(t[i].x,t[i].y)==false) {
				pilhaprofundidade.add(t[i]);
				nodoAtivo=t[i];
				ok = true;
				break;
			}
		}
		
		if(ok) {
			continue;
		}
		
		pilhaprofundidade.removeLast();
		nodoAtivo=pilhaprofundidade.getLast();
	}
	
	return false;
}
//public boolean rodaBuscaProfundidadeRecursivo(int iniX,int iniY,int objX,int objY) {
//	System.out.println(""+iniX+" "+iniY+" | "+objX+" "+objY);
//	
//	if(iniX==objX&&iniY==objY) {
//		return true;
//	}
//	
//	if(mapa.mapa[iniY][iniX]==1) {
//		return false;
//	}
//	
//	if(jaPassei(iniX,iniY)) {
//		return false;
//	}
//	
//	synchronized (nodosPercorridos) {
//		nodosPercorridos.add(new Nodo(iniX,iniY));
//	}
//
//	
//	try {
//		Thread.sleep(10);
//	} catch (InterruptedException e) {
//		e.printStackTrace();
//	}
//	
//	if(rodaBuscaProfundidadeRecursivo(iniX,iniY+1,objX,objY)) {
//		return true;
//	}
//	if(rodaBuscaProfundidadeRecursivo(iniX+1,iniY,objX,objY)) {
//		return true;
//	}
//	if(rodaBuscaProfundidadeRecursivo(iniX,iniY-1,objX,objY)) {
//		return true;
//	}
//	if(rodaBuscaProfundidadeRecursivo(iniX-1,iniY,objX,objY)) {
//		return true;
//	}
//	
//	return false;
//}



public void startGame()
// initialise and start the thread
{
	if (animator == null || !running) {
		animator = new Thread(this);
		animator.start();
	}
} // end of startGame()

public void stopGame()
// called by the user to stop execution
{ running = false; }


public void run()
/* Repeatedly update, render, sleep */
{
	running = true;
	
	long DifTime,TempoAnterior;
	
	int segundo = 0;
	DifTime = 0;
	TempoAnterior = System.currentTimeMillis();
	
	this.createBufferStrategy(2);
	BufferStrategy strategy = this.getBufferStrategy();
	
	while(running) {
	
		gameUpdate(DifTime); // game state is updated
		Graphics g = strategy.getDrawGraphics();
		gameRender((Graphics2D)g); // render to a buffer
		strategy.show();
	
		try {
			Thread.sleep(0); // sleep a bit
		}	
		catch(InterruptedException ex){}
		
		DifTime = System.currentTimeMillis() - TempoAnterior;
		TempoAnterior = System.currentTimeMillis();
		
		if(segundo!=((int)(TempoAnterior/1000))){
			FPS = SFPS;
			SFPS = 1;
			segundo = ((int)(TempoAnterior/1000));
		}else{
			SFPS++;
		}
	
	}
System.exit(0); // so enclosing JFrame/JApplet exits
} // end of run()

int timerfps = 0;
private void gameUpdate(long DiffTime)
{ 
	
	if(LEFT){
		posx-=1000*DiffTime/1000.0;
	}
	if(RIGHT){
		posx+=1000*DiffTime/1000.0;
	}	
	if(UP){
		posy-=1000*DiffTime/1000.0;
	}
	if(DOWN){
		posy+=1000*DiffTime/1000.0;
	}
	
	if(posx>mapa.Largura*16) {
		posx=mapa.Largura*16;
	}
	if(posy>mapa.Altura*16) {
		posy=mapa.Altura*16;
	}
	if(posx<0) {
		posx=0;
	}
	if(posy<0) {
		posy=0;
	}
	
	mapa.Posiciona((int)posx,(int)posy);
	
	for(int i = 0;i < listadeagentes.size();i++){
		  listadeagentes.get(i).SimulaSe((int)DiffTime);
	}
}

private void gameRender(Graphics2D dbg)
// draw the current frame to an image buffer
{
	// clear the background
	dbg.setColor(Color.white);
	dbg.fillRect (0, 0, PWIDTH, PHEIGHT);

	AffineTransform trans = dbg.getTransform();
	dbg.scale(zoom, zoom);
	
	try {
		mapa.DesenhaSe(dbg);
	}catch (Exception e) {
		System.out.println("Erro ao desenhar mapa");
	}
	
	for(int i = 0;i < listadeagentes.size();i++){
	  listadeagentes.get(i).DesenhaSe(dbg, mapa.MapX, mapa.MapY);
	}
	
	synchronized (nodosPercorridos) {
		for (Iterator iterator = nodosPercorridos.iterator(); iterator.hasNext();) {
			Integer nxy = (Integer) iterator.next();
			int px = nxy%1000;
			int py = (int)(nxy/1000);
			dbg.setColor(Color.GREEN);
			dbg.fillRect(px*16-mapa.MapX, py*16-mapa.MapY, 16, 16);
		}
	}
	
	if(caminho!=null){
		
		try {
			if(caminho!=null){
				for(int i = 0; i < caminho.length/2;i++){
					int nx = caminho[i*2];
					int ny = caminho[i*2+1];
					
					dbg.setColor(Color.BLUE);
					dbg.fillRect(nx*16-mapa.MapX, ny*16-mapa.MapY, 16, 16);
				}
			}
		}catch (Exception e) {
		}
	}
	
	dbg.setTransform(trans);
	
	dbg.setFont(f);
	dbg.setColor(Color.BLUE);	
	dbg.drawString("FPS: "+FPS, 10, 30);	
	
	dbg.drawString("N: "+nodosPercorridos.size(), 100, 30);	
	//System.out.println("left "+LEFT);
		
}

}

