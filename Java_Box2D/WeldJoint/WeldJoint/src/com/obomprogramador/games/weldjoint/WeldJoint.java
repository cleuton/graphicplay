/*
 * Parte do livro "Manual do Indie Gamer", de Cleuton Sampaio, 
 * doravante denominado "O Autor".
 * Este codigo é distribuído sob a licença Apache 2.0, cujo texto pode ser lido em:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Se você for utilizar este código em seus produtos, por favor mencione a autoria
 * original, citando o livro e o nome do Autor.
 * 
 * O código  é disponibilizado "AS IS", e não há nenhum compromisso de suporte ou
 * garantia por parte do Autor.
 * 
 */
package com.obomprogramador.games.weldjoint;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.WeldJointDef;



/**
 * Esta classe serve como laboratorio para estudar Box2D (JBox2D)
 * @author Cleuton Sampaio
 *
 */
public class WeldJoint extends JFrame {
	
	private static final long serialVersionUID = 2093806019531125684L;
	protected GameLoopTask task;
	protected Timer timer;
	protected boolean simulando = false;
	protected int FPS = 30;
	protected long gameLoopInterval = 5; 
	protected Graphics2D gx;               // Rotação
	protected Image gImagem;
	protected int largura;
	protected int altura;
	
	// Dimensões da "viewport", ou a janela de renderização:
	protected int larguraImagem;
	protected int alturaImagem;
	
	// Dimensões do nosso "mundo", ou seja, onde os objetos estão trabalhando:
	protected float larguraMundo;
	protected float alturaMundo;
	
	protected PainelGrafico pGrafico;
	protected String tituloParado = "Parede Box2D - Clique para rodar simulação";
	protected String tituloRodando = "(Rodando) Parede Box2D - Clique para parar simulação";
	protected World world;
	
	// Para o ajuste de escala
	protected float fatorEscalaVisual = 4.0f;  
	
	// Variáveis específicas deste laboratório (devem ser substituidas)
	protected float timeStep = 1.0f / 30.0f; // 100 hertz
	protected int   velocityIterations = 8;
	protected int   positionIterations = 3;
	protected Body chao;
	protected Body [] tijolos;
	protected Body bola;
	protected Body rampa;
	protected Body tijoloAtingido;
	protected int  numTijolos = 6;
	protected boolean atingiu = false;
	protected Random random = new Random();
	protected Body paredeEsquerda;  // Estático
	protected Body paredeDireita;  // Estático
	protected boolean chutando = false;
	protected float origemX;
	protected float vMax;
	protected boolean debugging = false;
	
	// *** Iniciador da aplicacao *** 
	
	public static void main (String [] args) {
		WeldJoint lm = new WeldJoint();
		lm.setVisible(true);
		lm.larguraImagem = lm.pGrafico.getWidth();
		lm.alturaImagem = lm.pGrafico.getHeight();
		lm.alturaMundo = lm.alturaImagem / lm.fatorEscalaVisual;
		lm.larguraMundo = lm.larguraImagem / lm.fatorEscalaVisual;
		lm.gImagem = lm.createImage(lm.larguraImagem, lm.alturaImagem);
		lm.gx = (Graphics2D) lm.gImagem.getGraphics();    // Rotação
		
		// Inicaliza o "mundo" Box2D
		lm.initBox2D();
	}
	
	//  *** Construtor *** 
	
	public WeldJoint() {
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
		chaoDef.position.set(larguraMundo / 2, 2.5f);
		chao = world.createBody(chaoDef);
		chao.setType(BodyType.STATIC);
		float larguraChao = larguraMundo - 2.5f;
		Vec2 chaoTamanho = new Vec2(larguraChao, 2.5f);
		// Atribuímos uma forma retangular à fixture do chão
		PolygonShape chaoShape = new PolygonShape();
		chaoShape.setAsBox(chaoTamanho.x / 2, 1.25f);
		FixtureDef chaoFixDef = new FixtureDef();
		chaoFixDef.shape = chaoShape;
		chaoFixDef.friction = 0.5f;
		chaoFixDef.density = 10.0f;
		chao.createFixture(chaoFixDef);
		DadosCorpo chaoCorpo = new DadosCorpo(chaoTamanho,1);
		chao.setUserData(chaoCorpo);
		
		// Criamos a parede esquerda
		BodyDef paredeEsquerdaDef = new BodyDef();
		
		paredeEsquerdaDef.position.set(2.5f, alturaMundo / 2);
		paredeEsquerda = world.createBody(paredeEsquerdaDef);
		paredeEsquerda.setType(BodyType.STATIC);
		PolygonShape paredeEsquerdaShape = new PolygonShape();
		paredeEsquerdaShape.setAsBox(1.25f, alturaMundo / 2);
		FixtureDef paredeEsquerdaFixDef = new FixtureDef();
		paredeEsquerdaFixDef.shape = paredeEsquerdaShape;
		paredeEsquerda.createFixture(paredeEsquerdaFixDef);
		Vec2 paredeEsquerdaTamanho = new Vec2(2.5f, alturaMundo);
		DadosCorpo paredeEsquerdaCorpo = new DadosCorpo(paredeEsquerdaTamanho,2);
		paredeEsquerda.setUserData(paredeEsquerdaCorpo);
		
		// Criamos a parede direita
		BodyDef paredeDireitaDef = new BodyDef();
		
		paredeDireitaDef.position.set(larguraMundo - 2.5f, alturaMundo / 2);
		paredeDireita = world.createBody(paredeDireitaDef);
		paredeDireita.setType(BodyType.STATIC);
		PolygonShape paredeDireitaShape = new PolygonShape();
		paredeDireitaShape.setAsBox(1.25f, alturaMundo / 2);
		FixtureDef paredeDireitaFixDef = new FixtureDef();
		paredeDireitaFixDef.shape = paredeDireitaShape;
		paredeDireita.createFixture(paredeDireitaFixDef);
		Vec2 paredeDireitaTamanho = new Vec2(2.5f, alturaMundo);
		DadosCorpo paredeDireitaCorpo = new DadosCorpo(paredeDireitaTamanho,3);
		paredeDireita.setUserData(paredeDireitaCorpo);
		
		// Vamos criar a bola que vai colidir
		
		BodyDef bolaDef = new BodyDef();
		
		bolaDef.position.set(larguraMundo / 5, 5);
		bola = world.createBody(bolaDef);
		bola.setType(BodyType.DYNAMIC);
		Vec2 bolaTamanho = new Vec2(10.0f, 10.0f); // A bola tem 10 metros de diâmetro
		// Atribuímos uma forma circular à fixture da bola
		CircleShape bolaShape = new CircleShape();
		bolaShape.m_radius = 5.0f;      // A bola tem 5m de raio
		FixtureDef bolaFixDef = new FixtureDef();
		bolaFixDef.shape = bolaShape;
		bolaFixDef.density = 5.0f;
		bolaFixDef.restitution = 0.6f;
		bolaFixDef.friction = 0.1f;
		bola.createFixture(bolaFixDef);
		bola.resetMassData();
		DadosCorpo bolaCorpo = new DadosCorpo(bolaTamanho,4);
		bola.setUserData(bolaCorpo);
		
		// Vamos criar uma rampa estática
		BodyDef rampaDef = new BodyDef();
		rampaDef.position.set(larguraMundo / 5 + 6, 2);
		rampa = world.createBody(rampaDef);
		rampa.setType(BodyType.STATIC);
		Vec2 [] vertices = {
				new Vec2(rampaDef.position.x - 5, rampaDef.position.y),
				new Vec2(rampaDef.position.x + 5, rampaDef.position.y),
				new Vec2(rampaDef.position.x + 5, rampaDef.position.y + 5)
				};
		PolygonShape rampaShape = new PolygonShape();
		rampaShape.set(vertices, 3);
		FixtureDef rampaFixDef = new FixtureDef();
		rampaFixDef.shape = rampaShape;
		rampaFixDef.friction = 0.1f;
		rampa.createFixture(rampaFixDef);
		DadosCorpo rampaCorpo = new DadosCorpo(null,0);
		rampa.setUserData(rampaCorpo);
		
		
		// Vamos criar a cadeia de tijolos
		
		tijolos = new Body[numTijolos];
		
		for (int ix = 0; ix < numTijolos; ix++) {
			BodyDef tijoloDef = new BodyDef();
			tijoloDef.position.set((larguraMundo / 5) * 4,
					8 * (ix) + 5.0f);
			Body tijolo = world.createBody(tijoloDef);
			tijolo.setType(BodyType.DYNAMIC);
			Vec2 tijoloTamanho = new Vec2(5.0f, 5.0f); 
			FixtureDef tijoloFixDef = new FixtureDef();
			PolygonShape tijoloshape = new PolygonShape();
			tijoloshape.setAsBox(2.5f, 2.5f);
			tijoloFixDef.shape = tijoloshape;
			tijoloFixDef.density = 4.0f;
			tijoloFixDef.restitution = 0.6f;
			tijoloFixDef.friction = 0.1f;
			tijolo.createFixture(tijoloFixDef);
			tijolo.resetMassData();
			DadosCorpo tijoloCorpo = new DadosCorpo(tijoloTamanho,5 + ix);
			tijolo.setUserData(tijoloCorpo);
			tijolos[ix] = tijolo;
		}
		
		// *** Criamos a junção entre cada par de tijolos:
		
		for (int ix = 0; ix < (numTijolos - 1); ix++) {
			WeldJointDef jointDef = new WeldJointDef();
			jointDef.initialize(tijolos[ix], tijolos[ix+1], tijolos[ix].getWorldCenter());
			jointDef.collideConnected = true;
			world.createJoint(jointDef);
		}
		
		// Adiciona o contact listener
		Contato c = new Contato();
		world.setContactListener(c);

	}
	
	private void update() {
		// Atualiza o "mundo" Box2D e o modelo de dados
		world.step(timeStep, velocityIterations, positionIterations);
		world.clearForces();
		if (atingiu) {
			if (tijoloAtingido != null) {
				Joint junta = tijoloAtingido.getJointList() == null ? null : tijoloAtingido.getJointList().joint;
				if (junta !=null) {
					world.destroyJoint(tijoloAtingido.getJointList().joint);
				}
			}
			atingiu = false;
		}
	}
	
	class DadosCorpo {
		public Vec2 tamanho;
		public int id;
		public DadosCorpo() {
			super();
		}
		public DadosCorpo(Vec2 tamanho, int id) {
			this();
			this.tamanho = tamanho;
			this.id = id;
		}
		
	}
	
	class Contato implements ContactListener {

		@Override
		public void beginContact(org.jbox2d.dynamics.contacts.Contact c) {
			DadosCorpo corpoA = (DadosCorpo) c.getFixtureA().getBody().getUserData();
			DadosCorpo corpoB = (DadosCorpo) c.getFixtureB().getBody().getUserData();
			if (corpoA.id >=4 && corpoB.id >= 4) {
				atingiu = true;
				if (corpoA.id >=5) {
					tijoloAtingido = tijolos[corpoA.id - 5];
				}
				else if (corpoB.id >= 5) {
						tijoloAtingido = tijolos[corpoB.id - 5];
				}
			}
		}

		@Override
		public void endContact(org.jbox2d.dynamics.contacts.Contact arg0) {
			
		}

		@Override
		public void postSolve(org.jbox2d.dynamics.contacts.Contact arg0,
				ContactImpulse arg1) {
			
		}

		@Override
		public void preSolve(org.jbox2d.dynamics.contacts.Contact arg0,
				Manifold arg1) {
		
			
		}
		
	}
	
	//  *** Rotinas que traduzem as coordenadas e desenham  *** 
	
	private void redesenhar() {
		// Estamos usando a tecnica de "double buffering"
		// Vamos desenhar em uma imagem separada
		
		synchronized(gImagem) {
			gx.setColor(Color.black);
			gx.fillRect(0, 0, larguraImagem, alturaImagem);
			
			// Desenha a bola:
			
			
			gx.setColor(Color.magenta);
			Retangulo rect = criarRetangulo(bola);
			gx.drawOval(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			// Desenha a rampa:
			gx.setColor(Color.orange);
			PolygonShape rampaShape = (PolygonShape) rampa.getFixtureList().getShape();
			Vec2 [] vertices = rampaShape.getVertices();
			int  [] xpoint = new int[3];
			int  [] ypoint = new int[3];
			for (int x = 0; x < 3; x++) {
				Vec2 vnorm = normalizarCoordenadas(rampa.getWorldPoint(vertices[x]));
				xpoint[x] = (int) vnorm.x;
				ypoint[x] = (int) vnorm.y;
			}
			Polygon pol = new Polygon(xpoint, ypoint,xpoint.length);
			gx.drawPolygon(pol);
	
			// Desenha as tijolos:
			
			gx.setColor(Color.cyan);
			for (int ix = 0; ix < numTijolos; ix++) {
				gx.drawPolygon(criarPoligono(tijolos[ix]));
			}
			
			// Desenha as junções:
			
			gx.setColor(Color.yellow);
			for (int ix = 0; ix < (numTijolos - 1); ix++) {
				JointEdge je = tijolos[ix].getJointList();
				while (je != null) {
					Vec2 cdCorpoA = normalizarCoordenadas(tijolos[ix].getWorldCenter());
					Vec2 cdCorpoB = normalizarCoordenadas(je.other.getWorldCenter());
					gx.drawLine((int)cdCorpoA.x, (int)cdCorpoA.y, (int)cdCorpoB.x, (int)cdCorpoB.y);				
					je = je.next;
				}
			}
			
			
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
		}

	}
	
	private Vec2 normalizarCoordenadas(Vec2 coordB2D) {
		Vec2 resultado = new Vec2(0.0f, 0.0f);
		resultado.x = coordB2D.x * fatorEscalaVisual;
		resultado.y = (alturaMundo - coordB2D.y) * fatorEscalaVisual;
		return resultado;
	}
	
	private Retangulo criarRetangulo(Body body) {
		Retangulo rect = new Retangulo();
		DadosCorpo dados = (DadosCorpo) body.getUserData();
		Vec2 tamanho = dados.tamanho;
		tamanho = tamanho.mul(fatorEscalaVisual);
		Vec2 posicao = normalizarCoordenadas(body.getPosition());
		rect.x = (int) (posicao.x - tamanho.x / 2);
		rect.y = (int) (posicao.y - tamanho.y / 2);
		rect.width = (int) tamanho.x;
		rect.height = (int) tamanho.y;
		return rect;
	}
	
	private Polygon criarPoligono(Body body) {
		int [] xpoint = new int [4];
		int [] ypoint = new int [4];
		DadosCorpo dados = (DadosCorpo) body.getUserData();
		Vec2 tamanho = dados.tamanho;
		
		//Coordenadas do canto superior esquerdo
		Vec2 ponto = body.getWorldPoint(
					new Vec2(
								body.getLocalCenter().x - tamanho.x / 2,
								body.getLocalCenter().y + tamanho.y / 2							
							)
				);
		ponto = normalizarCoordenadas(ponto);
		xpoint[0] = (int)ponto.x;
		ypoint[0] = (int)ponto.y;
		
		//Coordenadas do canto superior direito
		ponto = body.getWorldPoint(
					new Vec2(
								body.getLocalCenter().x + tamanho.x / 2,
								body.getLocalCenter().y + tamanho.y / 2							
							)
				);
		ponto = normalizarCoordenadas(ponto);
		xpoint[1] = (int)ponto.x;
		ypoint[1] = (int)ponto.y;
		
		//Coordenadas do canto inferior direito
		ponto = body.getWorldPoint(
					new Vec2(
								body.getLocalCenter().x + tamanho.x / 2,
								body.getLocalCenter().y - tamanho.y / 2							
							)
				);
		ponto = normalizarCoordenadas(ponto);
		xpoint[2] = (int)ponto.x;
		ypoint[2] = (int)ponto.y;
		
		//Coordenadas do canto inferior esquerdo
		ponto = body.getWorldPoint(
					new Vec2(
								body.getLocalCenter().x - tamanho.x / 2,
								body.getLocalCenter().y - tamanho.y / 2							
							)
				);
		ponto = normalizarCoordenadas(ponto);
		xpoint[3] = (int)ponto.x;
		ypoint[3] = (int)ponto.y;
		
		Polygon pol = new Polygon(xpoint, ypoint,xpoint.length);
		
		return pol;
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
				Vec2 forca = 
						new Vec2(1000.0f * bola.getMass(),0);
				
				Vec2 posicao = bola.getWorldCenter();
				bola.setAwake(true);
				bola.applyForce(forca, posicao);
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
				atingiu = false;
			}
			else {
				if (world != null) {
					initBox2D();
				}
				setTitle(tituloRodando);
				atingiu = false;
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
	/*
	 * Para maior simplicidade, estamos usando as classes java.util.Timer e
	 * java.util.TimerTask. Além disto, estamos usando renderização passiva.
	 * Se quiser um melhor resultado, veja o capítulo "Framework básico de game".
	 * Este tipo de implementação de Game loop não é muito preciso, servindo apenas
	 * para demonstração do Box2D.
	 */
	public void runGameLoop() {
		simulando = true;
		task = new GameLoopTask();
		timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, gameLoopInterval);
	}
	
	public void gameLoop() {
		synchronized (this) {
			// Um lembrete de que pode haver problemas de concorrência
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
