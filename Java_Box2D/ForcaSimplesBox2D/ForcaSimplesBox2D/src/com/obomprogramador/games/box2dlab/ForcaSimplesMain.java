/*
 * Parte do livro "Manual do Indie Gamer", de Cleuton Sampaio, 
 * doravante denominado "O Autor".
 * Este c�digo � distribu�do sob a licen�a Apache 2.0, cujo texto pode ser lido em:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Se voc� for utilizar este c�digo em seus produtos, por favor mencione a autoria
 * original, citando o livro e o nome do Autor.
 * 
 * O c�digo � disponibilizado "AS IS", e n�o h� nenhum compromisso de suporte ou
 * garantia por parte do Autor.
 * 
 */
package com.obomprogramador.games.box2dlab;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Esta classe serve como laboratorio para estudar Box2D (JBox2D)
 * @author Cleuton Sampaio
 *
 */
public class ForcaSimplesMain extends JFrame {
	
	private static final long serialVersionUID = 2093806019531125684L;
	protected GameLoopTask task;
	protected Timer timer;
	protected boolean simulando = false;
	protected int FPS = 30;
	protected long gameLoopInterval = 5; 
	protected Graphics gx;
	protected Image gImagem;
	protected int largura;
	protected int altura;
	protected int larguraImagem;
	protected int alturaImagem;
	protected PainelGrafico pGrafico;
	protected String tituloParado = "Forca simples Box2D - Clique para rodar simulacao";
	protected String tituloRodando = "(Rodando) Forca simples Box2D - Clique para parar simulacao";
	protected World world;
	
	// Vari�veis espec�ficas deste laboratorio (devem ser substituidas)
	protected float timeStep = 1.0f / 30.0f; // 100 hertz
	protected int   velocityIterations = 6;
	protected int   positionIterations = 2;
	protected Body chao;
	protected Body quadrado;
	protected Body paredeEsquerda;  // Est�tico
	protected Body paredeDireita;  // Est�tico
	protected boolean chutando = false;
	protected float origemX;
	protected float vMax;
	
	// *** Iniciador da aplicacao *** 
	
	public static void main (String [] args) {
		ForcaSimplesMain lm = new ForcaSimplesMain();
		lm.setVisible(true);
		lm.larguraImagem = lm.pGrafico.getWidth();
		lm.alturaImagem = lm.pGrafico.getHeight();
		lm.gImagem = lm.createImage(lm.larguraImagem, lm.alturaImagem);
		lm.gx = lm.gImagem.getGraphics();
		
		// Inicaliza o "mundo" Box2D
		lm.initBox2D();
	}
	
	//  *** Construtor *** 
	
	public ForcaSimplesMain() {
		super();
		this.initComponents();
	}

	//  *** Iniciando componentes swing *** 
	
	private void initComponents() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		largura = 480; 
		altura  = 320; 
		this.setSize(largura, altura);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (dim.width-largura)/2;
		int y = (dim.height-altura)/2;
		this.setLocation(x, y);
		this.setTitle(this.tituloParado);
		this.setResizable(false);
		this.getContentPane().setBackground(Color.black);
		this.addMouseListener(new TrataMouse());
		this.pGrafico = new PainelGrafico();
		Container cont = getContentPane();   
	    cont.add(this.pGrafico, "Center");
		this.addKeyListener(new TrataTeclado());

	}
	
	//  *** Funcoes que lidam com o Box2D *** 
	
	private void initBox2D() {
		// Inicializa o "mundo" e os objetos
		world = new World(new Vec2(0.0f, -10.0f), true);
		
		// Criamos o chao
		BodyDef chaoDef = new BodyDef();
		// A origem do eixo das ordenadas e no canto inferior, e nao no superior
		chaoDef.position.set(larguraImagem / 2, 10);
		chao = world.createBody(chaoDef);
		chao.setType(BodyType.STATIC);
		float larguraChao = larguraImagem - 10.0f;
		Vec2 chaoTamanho = new Vec2(larguraChao, 10.0f);
		// Atribu�mos uma forma retangular � fixture do ch�o
		PolygonShape chaoShape = new PolygonShape();
		chaoShape.setAsBox(chaoTamanho.x / 2, 5.0f);
		FixtureDef chaoFixDef = new FixtureDef();
		chaoFixDef.friction = 0.1f;
		chaoFixDef.shape = chaoShape;
		chao.createFixture(chaoFixDef);
		chao.setUserData(chaoTamanho);
		
		// Criamos a parede esquerda
		BodyDef paredeEsquerdaDef = new BodyDef();
		paredeEsquerdaDef.position.set(10, alturaImagem / 2);
		paredeEsquerda = world.createBody(paredeEsquerdaDef);
		paredeEsquerda.setType(BodyType.STATIC);
		PolygonShape paredeEsquerdaShape = new PolygonShape();
		paredeEsquerdaShape.setAsBox(5.0f, alturaImagem / 2);
		FixtureDef paredeEsquerdaFixDef = new FixtureDef();
		paredeEsquerdaFixDef.shape = paredeEsquerdaShape;
		paredeEsquerda.createFixture(paredeEsquerdaFixDef);
		Vec2 paredeEsquerdaTamanho = new Vec2(10.0f, alturaImagem);
		paredeEsquerda.setUserData(paredeEsquerdaTamanho);
		
		// Criamos a parede direita
		BodyDef paredeDireitaDef = new BodyDef();
		paredeDireitaDef.position.set(larguraImagem - 10, alturaImagem / 2);
		paredeDireita = world.createBody(paredeDireitaDef);
		paredeDireita.setType(BodyType.STATIC);
		PolygonShape paredeDireitaShape = new PolygonShape();
		paredeDireitaShape.setAsBox(5.0f, alturaImagem / 2);
		FixtureDef paredeDireitaFixDef = new FixtureDef();
		paredeDireitaFixDef.shape = paredeDireitaShape;
		paredeDireita.createFixture(paredeDireitaFixDef);
		Vec2 paredeDireitaTamanho = new Vec2(10.0f, alturaImagem);
		paredeDireita.setUserData(paredeDireitaTamanho);
		
		// Criamos um quadrado dinamico
		BodyDef quadradoDef = new BodyDef();
		quadradoDef.position.set(larguraImagem / 3, 35);
		quadrado = world.createBody(quadradoDef);
		quadrado.setType(BodyType.DYNAMIC);
		Vec2 quadradoTamanho = new Vec2(40.0f, 40.0f);
		PolygonShape quadradoShape = new PolygonShape();
		quadradoShape.setAsBox(20.0f, 20.0f);
		FixtureDef quadradoFixDef = new FixtureDef();
		quadradoFixDef.shape = quadradoShape;
		quadradoFixDef.density = 5.0f;
		quadradoFixDef.restitution = 0.6f;
		quadradoFixDef.friction = 0.2f;
		quadrado.createFixture(quadradoFixDef);
		quadrado.resetMassData();
		quadrado.setUserData(quadradoTamanho);
		origemX = quadrado.getTransform().position.x;
	}
	
	private void update() {
		// Atualiza o "mundo" Box2D e o modelo de dados
		world.step(timeStep, velocityIterations, positionIterations);
		world.clearForces();
	}
	
	//  *** Rotinas que traduzem as coordenadas e desenham  *** 
	
	private void redesenhar() {
		// Estamos usando a tecnica de "double buffering"
		// Vamos desenhar em uma imagem separada
		synchronized(gImagem) {
			gx.setColor(Color.black);
			gx.fillRect(0, 0, larguraImagem, alturaImagem);
	
			gx.setColor(Color.yellow);
			Retangulo rect = criarRetangulo(quadrado);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.width));
			
			gx.setColor(Color.red);
			rect = criarRetangulo(chao);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.setColor(Color.red);
			rect = criarRetangulo(paredeEsquerda);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.setColor(Color.yellow);
			rect = criarRetangulo(paredeDireita);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.drawString("Massa: " + quadrado.getMass() + " kg",30, 12);
			gx.drawString("Aceleracao X: " + 500 + " m/s",30, 24);
			gx.drawString("Forca: " + quadrado.getMass() * 500.0f + " N", 30, 36);
			gx.drawString("Distancia X: " + (quadrado.getTransform().position.x - origemX) + " m", 30, 48);
			if (quadrado.getLinearVelocity().x > vMax) {
				vMax = quadrado.getLinearVelocity().x;
			}
			gx.drawString("Velocidade X max: " + (vMax) + " m/s", 30, 60);
		}
	}
	
	private Vec2 normalizarCoordenadas(Vec2 coordB2D) {
		Vec2 resultado = new Vec2(0.0f, 0.0f);
		resultado.x = coordB2D.x;
		resultado.y = alturaImagem - coordB2D.y;
		return resultado;
	}
	
	private Retangulo criarRetangulo(Body body) {
		Retangulo rect = new Retangulo();
		Vec2 tamanho = (Vec2) body.getUserData();
		Vec2 posicao = normalizarCoordenadas(body.getPosition());
		rect.x = (int) (posicao.x - tamanho.x / 2);
		rect.y = (int) (posicao.y - tamanho.y / 2);
		rect.width = (int) tamanho.x;
		rect.height = (int) tamanho.y;
		return rect;
	}
	
	class PainelGrafico extends JPanel {

		private static final long serialVersionUID = 5173079166655854668L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (gImagem != null) {
				synchronized(gImagem) {
					g.drawImage(gImagem, 0, 0, null);
				}
			}
		}
		
	}

	class Retangulo {
		public float x = 0.0f;
		public float y = 0.0f;
		public float width = 0.0f;
		public float height = 0.0f;
	}
	
	//  *** Tratamento do mouse click *** 
	
	class TrataTeclado implements KeyListener {

		@Override
		public void keyPressed(KeyEvent key) {
			if (key.getKeyCode() == KeyEvent.VK_SPACE) {
				Vec2 forca = quadrado.getWorldVector(new Vec2(+500.0f * quadrado.getMass(), 0.0f * quadrado.getMass()));
				quadrado.setAwake(true);
				origemX = quadrado.getTransform().position.x;
				vMax = 0;
				quadrado.applyForce(forca, quadrado.getWorldPoint(quadrado.getLocalCenter().sub(new Vec2(20,0))));

			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
		}
		
	}

	class TrataMouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent arg0) {
			if (simulando) {
				setTitle(tituloParado);
				timer.cancel();
				task = null;
				simulando = false;
			}
			else {
				if (world != null) {
					initBox2D();
				}
				setTitle(tituloRodando);
				runGameLoop();
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}
		
	}
	
	//  *** Rotinas do Game Loop *** 
	
	public void runGameLoop() {
		simulando = true;
		task = new GameLoopTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, gameLoopInterval);
	}
	
	public void gameLoop() {
		synchronized (this) {
			// Um lembrete de que pode haver problemas de concorrencia
			update();
			redesenhar();
		};
		this.pGrafico.repaint();
	}

	class GameLoopTask extends TimerTask {

		@Override
		public void run() {
			gameLoop();
		}
		
	}
	
}
