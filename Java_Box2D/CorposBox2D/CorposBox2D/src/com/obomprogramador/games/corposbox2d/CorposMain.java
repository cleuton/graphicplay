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
package com.obomprogramador.games.corposbox2d;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
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
 * Esta classe serve como laboratorio para estudar tipos de corpos Box2D (JBox2D)
 * @author Cleuton Sampaio
 *
 */
public class CorposMain extends JFrame {
	
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
	
	// Dimensões da "viewport", ou a janela de renderização:
	protected int larguraImagem;
	protected int alturaImagem;
	
	// Dimensões do nosso "mundo", ou seja, onde os objetos estão trabalhando:
	protected float larguraMundo;
	protected float alturaMundo;
	
	protected PainelGrafico pGrafico;
	protected String tituloParado = "Corpos Box2D - Clique para rodar simulação";
	protected String tituloRodando = "(Rodando) Corpos Box2D - Clique para parar simulação";
	protected World world;
	
	// Para o ajuste de escala
	protected float fatorEscalaVisual = 4.0f; 
	
	// Variáveis específicas deste laboratório (devem ser substituidas)
	protected float timeStep = 1.0f / 30.0f; // 100 hertz
	protected int   velocityIterations = 6;
	protected int   positionIterations = 2;
	protected Body chao;     		// Estático
	protected Body paredeEsquerda;  // Estático
	protected Body paredeDireita;  // Estático
	protected Body bola;     		// Dinâmico
	protected Body bola2;     		// Dinâmico
	protected Body quadrado; 		// Estático
	protected Body barra;           // Cinemático
	protected Body barra2;           // Cinemático
	
	
	// *** Iniciador da aplicacao *** 
	
	public static void main (String [] args) {
		CorposMain lm = new CorposMain();
		lm.setVisible(true);
		lm.larguraImagem = lm.pGrafico.getWidth();
		lm.alturaImagem = lm.pGrafico.getHeight();
		lm.alturaMundo = lm.alturaImagem / lm.fatorEscalaVisual;
		lm.larguraMundo = lm.larguraImagem / lm.fatorEscalaVisual;
		lm.gImagem = lm.createImage(lm.larguraImagem, lm.alturaImagem);
		lm.gx = lm.gImagem.getGraphics();
		
		// Inicaliza o "mundo" Box2D
		lm.initBox2D();
	}
	
	//  *** Construtor *** 
	
	public CorposMain() {
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
		chao.createFixture(chaoFixDef);
		chao.setUserData(chaoTamanho);
		
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
		paredeEsquerda.setUserData(paredeEsquerdaTamanho);
		
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
		paredeDireita.setUserData(paredeDireitaTamanho);
		
		// Criamos uma bola
		BodyDef bolaDef = new BodyDef();
		
		bolaDef.position.set(larguraMundo / 2, alturaMundo - 5);
		bola = world.createBody(bolaDef);
		bola.setType(BodyType.DYNAMIC);
		Vec2 bolaTamanho = new Vec2(10.0f, 10.0f); // A bola tem 10 metros de diâmetro
		// Atribuímos uma forma circular à fixture da bola
		CircleShape bolaShape = new CircleShape();
		bolaShape.m_radius = 5.0f;      // A bola tem 5m de raio
		FixtureDef bolaFixDef = new FixtureDef();
		bolaFixDef.shape = bolaShape;
		bolaFixDef.density = 1.0f;
		bolaFixDef.restitution = 0.6f;
		bolaFixDef.friction = 0.3f;
		bola.createFixture(bolaFixDef);
		bola.resetMassData();
		bola.setUserData(bolaTamanho);
		
		// Criamos outra bola
		BodyDef bola2Def = new BodyDef();
		bola2Def.position.set((larguraMundo / 4) * 3, 10);
		bola2 = world.createBody(bola2Def);
		bola2.setType(BodyType.DYNAMIC);
		Vec2 bola2Tamanho = new Vec2(10.0f, 10.0f);
		
		CircleShape bola2Shape = new CircleShape();
		bola2Shape.m_radius = 5.0f;
		FixtureDef bola2FixDef = new FixtureDef();
		bola2FixDef.shape = bola2Shape;
		bola2FixDef.density = 1.0f;
		bola2FixDef.restitution = 0.6f;
		bola2FixDef.friction = 0.3f;
		bola2.createFixture(bola2FixDef);
		bola2.resetMassData();
		bola2.setUserData(bola2Tamanho);
		
		// Criamos um quadrado estático
		BodyDef quadradoDef = new BodyDef();
		quadradoDef.position.set(larguraMundo / 4, alturaMundo / 2);
		quadrado = world.createBody(quadradoDef);
		quadrado.setType(BodyType.STATIC);
		Vec2 quadradoTamanho = new Vec2(10.0f, 10.0f);
		
		PolygonShape quadradoShape = new PolygonShape();
		quadradoShape.setAsBox(5.0f, 5.0f);
		FixtureDef quadradoFixDef = new FixtureDef();
		quadradoFixDef.shape = quadradoShape;
		quadradoFixDef.density = 1.0f;
		quadradoFixDef.restitution = 0.6f;
		quadradoFixDef.friction = 0.3f;
		quadrado.createFixture(quadradoFixDef);
		quadrado.resetMassData();
		quadrado.setUserData(quadradoTamanho);
		
		// Criamos uma barra cinemática
		BodyDef barraDef = new BodyDef();
		barraDef.position.set(larguraMundo / 2, alturaMundo / 2);
		barra = world.createBody(barraDef);
		barra.setType(BodyType.KINEMATIC);
		Vec2 barraTamanho = new Vec2(17.5f, 7.5f);
		
		PolygonShape barraShape = new PolygonShape();
		barraShape.setAsBox(8.75f, 3.75f);
		FixtureDef barraFixDef = new FixtureDef();
		barraFixDef.shape = barraShape;
		barraFixDef.density = 1.0f;
		barraFixDef.restitution = 0.6f;
		barra.setLinearVelocity(new Vec2(-4.0f,0.0f));
		barraFixDef.friction = 0.3f;
		barra.createFixture(barraFixDef);
		barra.resetMassData();
		barra.setUserData(barraTamanho);
		
		// Criamos outra barra cinemática
		BodyDef barra2Def = new BodyDef();
		barra2Def.position.set((larguraMundo / 4) * 3, alturaMundo - 5);
		barra2 = world.createBody(barra2Def);
		barra2.setType(BodyType.KINEMATIC);
		Vec2 barra2Tamanho = new Vec2(10.0f, 10.0f);
		
		PolygonShape barra2Shape = new PolygonShape();
		barra2Shape.setAsBox(5.0f, 5.0f);
		FixtureDef barra2FixDef = new FixtureDef();
		barra2FixDef.shape = barra2Shape;
		barra2FixDef.density = 1.0f;
		barra2FixDef.restitution = 0.6f;
		barra2.setLinearVelocity(new Vec2(0.0f,-4.0f));
		barra2FixDef.friction = 0.3f;
		barra2.createFixture(barra2FixDef);
		barra2.resetMassData();
		barra2.setUserData(barra2Tamanho);
		
	}
	
	private void update() {
		// Atualiza o "mundo" Box2D e o modelo de dados
		world.step(timeStep, velocityIterations, positionIterations);
		Vec2 velocidade = barra.getLinearVelocity();
		Vec2 posicao = barra.getTransform().position;
		if (posicao.x < 0 || posicao.x > larguraMundo) {
			velocidade.x *= -1;
			barra.setLinearVelocity(velocidade);
		}
		Vec2 velocidade2 = barra2.getLinearVelocity();
		Vec2 posicao2 = barra2.getTransform().position;
		if (posicao2.y < 0 || posicao2.y > alturaMundo) {
			velocidade2.y *= -1;
			barra2.setLinearVelocity(velocidade2);
		}
	}
	
	//  *** Rotinas que traduzem as coordenadas e desenham  *** 
	
	private void redesenhar() {
		// Estamos usando a técnica de "double buffering"
		// Vamos desenhar em uma imagem separada
		synchronized(gImagem) {
			gx.setColor(Color.black);
			gx.fillRect(0, 0, larguraImagem, alturaImagem);
	
			gx.setColor(Color.yellow);
			Retangulo rect = criarRetangulo(bola);
			gx.drawOval(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.width));
			rect = criarRetangulo(bola2);
			gx.drawOval(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.width));
	
			gx.setColor(Color.red);
			rect = criarRetangulo(quadrado);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
	
			gx.setColor(Color.red);
			rect = criarRetangulo(chao);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.setColor(Color.red);
			rect = criarRetangulo(paredeEsquerda);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.setColor(Color.red);
			rect = criarRetangulo(paredeDireita);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			
			gx.setColor(Color.green);
			rect = criarRetangulo(barra);
			gx.drawRect(Math.round(rect.x), Math.round(rect.y), 
					Math.round(rect.width), Math.round(rect.height));
			rect = criarRetangulo(barra2);
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
		Vec2 tamanho = (Vec2) body.getUserData();
		tamanho = tamanho.mul(fatorEscalaVisual);
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
