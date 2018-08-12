/*
 * (c) 2013 by Cleuton Sampaio
 */

var webglCanvas = document.getElementById("webgl");
var gl;
var esfera;
var skyBox;

var esfereRotationMatrix;
var modelViewMatrix;
var modelMatrix;
var viewMatrix;
var projectionMatrix;
var normalsMatrix;
var glslProgram = null;
var ultimaRotacao = 0;
var radianos = 0.0174532925;
var angulox = 0;
var anguloy = 0;
var anguloz = 0;
var rotacao = 0;

function carregarWebGL() {
	gl = getGLcontext(webglCanvas);
	if (!gl) {
		alert("Seu navegador não suporta contextos WebGL");
	}
	else {
		compileGLSL();
		setupObject();
		gl.clearColor(0.0, 0.0, 0.0, 1.0);
        gl.enable(gl.DEPTH_TEST);

	}
}

function setupObject() {
	
	/*
	 * 0 = Frontal; 1 = Direita; 2 = Superior; 3 = Esquerda; 4 = Inferior
     * 5 = Trazeira.
	 */
	
	skybox = new Skybox(gl,glslProgram,20,[
	     "frontal.png", "direita.png", "superior.png", "esquerda.png", "inferior.png", "trazeira.png"
       ]);
    
	skybox.gerar();
	
	esfera = new Esfera(gl,glslProgram, [1.0, 0.0, 0.0, 1.0],2);
	esfera.gerar();
    
    setInterval(draw, 50);
	
    modelViewMatrix = mat4.create();
    projectionMatrix = mat4.create();
    normalsMatix = mat4.create();

}

function draw() {

	/*
	 * 1 grau = 0,0174532925 radianos
	 */
	
	if (!skybox.carregado) {
		return;
	}
	
	projectionMatrix = mat4.create();
	mat4.identity(projectionMatrix);
	viewMatrix = mat4.create();
	mat4.identity(viewMatrix);
	modelMatrix = mat4.create();
	mat4.identity(modelMatrix);

	
	setupView(viewMatrix,projectionMatrix,gl);
	bindMatrices(modelViewMatrix, projectionMatrix);
	
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    
    // Desenhar skybox:
    
    skybox.renderizar();
    
    // Desenhar planeta:
   /* 
    mat4.rotate(modelMatrix, modelMatrix, angulox, [1,0,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloy, [0,1,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloz, [0,0,1]);
	*/
	setupView(viewMatrix,projectionMatrix,gl);
	mat4.multiply(modelViewMatrix, viewMatrix, modelMatrix);
	
	bindMatrices(modelViewMatrix, projectionMatrix);
    
    esfera.renderizar();
    
    // Rotacionar:
    
    var agora = (new Date).getTime();
    
    if (ultimaRotacao) {

        var delta = agora - ultimaRotacao;
        
        rotacao += (radianos * delta) / 1000.0;
        if (rotacao >= (radianos * 360)) {
        	rotacao = 0;
        }
        
        angulox = 0;
        anguloy = 10 * rotacao;
        anguloz = 0;
    }
    ultimaRotacao = agora;
    
}