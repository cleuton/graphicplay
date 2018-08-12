/*
 * (c) 2013 by Cleuton Sampaio
 */

var webglCanvas = document.getElementById("webgl");
var gl;
var esfera;
var verticesEsfera;
var normaisEsfera;
var indicesEsfera;
var coresEsfera;
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
        setInterval(draw, 50);
	}
}

function setupObject() {
	
	esfera = new Esfera(gl,[1.0, 0.0, 0.0, 1.0],2);
	esfera.gerar();

	verticesEsfera = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesEsfera);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(esfera.vertexPositionData), gl.STATIC_DRAW);
    verticesEsfera.itemSize = 3;
    
	normaisEsfera = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, normaisEsfera);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(esfera.normalData), gl.STATIC_DRAW);
    normaisEsfera.itemSize = 3;
    
    indicesEsfera = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesEsfera);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(esfera.indexData), gl.STATIC_DRAW);
    indicesEsfera.itemSize = 3;
   
    coresEsfera = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, coresEsfera);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(esfera.vertexColors), gl.STATIC_DRAW);
    coresEsfera.itemSize = 4;
    
    modelViewMatrix = mat4.create();
    projectionMatrix = mat4.create();
    normalsMatix = mat4.create();

}

function draw() {

	/*
	 * 1 grau = 0,0174532925 radianos
	 */
	projectionMatrix = mat4.create();
	mat4.identity(projectionMatrix);
	viewMatrix = mat4.create();
	mat4.identity(viewMatrix);
	modelMatrix = mat4.create();
	mat4.identity(modelMatrix);
	
	
	mat4.translate(modelMatrix, modelMatrix, [-4,0,0]);
	mat4.rotate(modelMatrix, modelMatrix, angulox, [1,0,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloy, [0,1,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloz, [0,0,1]);
	
	setupView(viewMatrix,projectionMatrix,gl);
	mat4.multiply(modelViewMatrix, viewMatrix, modelMatrix);
	
	bindMatrices(modelViewMatrix, projectionMatrix);
	
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesEsfera);
    gl.vertexAttribPointer(glslProgram.vertexPositionAttribute, verticesEsfera.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, coresEsfera);
    gl.vertexAttribPointer(glslProgram.vertexColorAttribute, coresEsfera.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, normaisEsfera);
    gl.vertexAttribPointer(glslProgram.normalPositionAttribute, normaisEsfera.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesEsfera);
    
    gl.drawElements(gl.LINE_STRIP, esfera.indexData.length, gl.UNSIGNED_SHORT, 0);
    
    
    // Desenha a segunda esfera, coberta:
    
	mat4.identity(projectionMatrix);
	mat4.identity(viewMatrix);
	mat4.identity(modelMatrix);
	
	mat4.translate(modelMatrix, modelMatrix, [+4,0,0]);
	mat4.rotate(modelMatrix, modelMatrix, angulox, [1,0,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloy, [0,1,0]);
	mat4.rotate(modelMatrix, modelMatrix, anguloz, [0,0,1]);
	
	setupView(viewMatrix,projectionMatrix,gl);
	mat4.multiply(modelViewMatrix, viewMatrix, modelMatrix);
	
	bindMatrices(modelViewMatrix, projectionMatrix);

	
	gl.drawElements(gl.TRIANGLES, esfera.indexData.length, gl.UNSIGNED_SHORT, 0);
    
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