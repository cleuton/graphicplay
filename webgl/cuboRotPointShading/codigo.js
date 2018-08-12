/*
 * (c) 2013 by Cleuton Sampaio
 */

var webglCanvas = document.getElementById("webgl");
var gl;
var verticesCubo;
var normaisCubo;
var indicesCubo;
var coresCubo;
var cubeRotationMatrix;
var modelViewMatrix;
var viewMatrix;
var positionMatrix;
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
	
	/*
	 * Os vértices de cada face, começando pela frontal
	 */
	
	var coordenadasVertice =  [
	                           /*Face Frontal*/
	                           /*v00*/  1, 1, 1,
	                           /*v01*/ -1, 1, 1,
	                           /*v02*/ -1,-1, 1,
	                           /*v03*/  1,-1, 1,
	                           /*Face Direita*/
	                           /*v04*/  1, 1, 1,
	                           /*v05*/  1,-1, 1,
	                           /*v06*/  1,-1,-1,
	                           /*v07*/  1, 1,-1,
	                           /*Face Superior*/
	                           /*v08*/  1, 1, 1,
	                           /*v09*/  1, 1,-1,
	                           /*v10*/ -1, 1,-1,
	                           /*v11*/ -1, 1, 1,
	                           /*Face Esquerda*/
	                           /*v12*/ -1, 1, 1,
	                           /*v13*/ -1, 1,-1,
	                           /*v14*/ -1,-1,-1,
	                           /*v15*/ -1,-1, 1,
	                           /*Face Inferior*/
	                           /*v16*/ -1,-1, 1,
	                           /*v17*/ -1,-1,-1,
	                           /*v18*/  1,-1,-1,
	                           /*v19*/  1,-1, 1,
	                           /*Face Trazeira*/
	                           /*v20*/ -1, 1,-1,
	                           /*v21*/  1, 1,-1,
	                           /*v22*/  1,-1,-1,
	                           /*v23*/ -1,-1,-1	                           
	                           ];

	var normais = 			[
	                           /*Face Frontal*/
	                           /*v00*/  0, 0, 1,
	                           /*v01*/  0, 0, 1,
	                           /*v02*/  0, 0, 1,
	                           /*v03*/  0, 0, 1,
	                           /*Face Direita*/
	                           /*v04*/  1, 0, 0,
	                           /*v05*/  1, 0, 0,
	                           /*v06*/  1, 0, 0,
	                           /*v07*/  1, 0, 0,
	                           /*Face Superior*/
	                           /*v08*/  0, 1, 0,
	                           /*v09*/  0, 1, 0,
	                           /*v10*/  0, 1, 0,
	                           /*v11*/  0, 1, 0,
	                           /*Face Esquerda*/
	                           /*v12*/ -1, 0, 0,
	                           /*v13*/ -1, 0, 0,
	                           /*v14*/ -1, 0, 0,
	                           /*v15*/ -1, 0, 0,
	                           /*Face Inferior*/
	                           /*v16*/  0,-1, 0,
	                           /*v17*/  0,-1, 0,
	                           /*v18*/  0,-1, 0,
	                           /*v19*/  0,-1, 0,
	                           /*Face Trazeira*/
	                           /*v20*/ 	0, 0,-1,
	                           /*v21*/ 	0, 0,-1,
	                           /*v22*/ 	0, 0,-1,
	                           /*v23*/ 	0, 0,-1                          
	                           ];
	
	var indicesFaces = new Uint8Array([
		                           0, 1, 2,   0, 2, 3,    // Face Frontal
		                           4, 5, 6,   4, 6, 7,    // Face Direita 
		                           8, 9,10,   8,10,11,    // Face Superior
		                          12,13,14,  12,14,15,    // Face Esquerda
		                          17,18,19,  17,19,16,    // face Inferior
		                          22,20,23,  22,21,20     // Face Trazeira
	                    
	                    ]);

	var coresVertices      =  [ 
								/* Face Frontal: Amarelo */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							
	 							/* Face Lateral Direita: Verde */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							
	 							/* Face Superior: Azul */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							
	 							/* Face Lateral Esquerda: Vermelho */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							
	 							/* Face Inferior: Marrom */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							
	 							/* Face Trazeira: Lilás */
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0,
	 							0.38,  0.04,  0.04, 1.0
	 							];
	 							

	verticesCubo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesCubo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(coordenadasVertice), gl.STATIC_DRAW);
    verticesCubo.itemSize = 3;
    verticesCubo.numItems = 8;
    
	normaisCubo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, normaisCubo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(normais), gl.STATIC_DRAW);
    normaisCubo.itemSize = 3;
    normaisCubo.numItems = 8;
    
    indicesCubo = gl.createBuffer();
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesCubo);
    gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(indicesFaces), gl.STATIC_DRAW);
   
    coresCubo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, coresCubo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(coresVertices), gl.STATIC_DRAW);
    coresCubo.itemSize = 4;
    coresCubo.numItens = 24;
    
    modelViewMatrix = mat4.create();
    positionMatrix = mat4.create();
    normalsMatix = mat4.create();

}

function draw() {

	/*
	 * 1 grau = 0,0174532925 radianos
	 */
	positionMatrix = mat4.create();
	viewMatrix = mat4.create();
	cubeRotationMatrix = mat4.create();
	mat4.identity(cubeRotationMatrix);
	mat4.rotate(cubeRotationMatrix, cubeRotationMatrix, angulox, [1,0,0]);
	mat4.rotate(cubeRotationMatrix, cubeRotationMatrix, anguloy, [0,1,0]);
	mat4.rotate(cubeRotationMatrix, cubeRotationMatrix, anguloz, [0,0,1]);

	setupView(viewMatrix,positionMatrix,gl);

	bindMatrices(cubeRotationMatrix, viewMatrix, positionMatrix);
	
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesCubo);
    gl.vertexAttribPointer(glslProgram.vertexPositionAttribute, verticesCubo.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, coresCubo);
    gl.vertexAttribPointer(glslProgram.vertexColorAttribute, coresCubo.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, normaisCubo);
    gl.vertexAttribPointer(glslProgram.normalPositionAttribute, normaisCubo.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER, indicesCubo);
    gl.drawElements(gl.TRIANGLES, 36, gl.UNSIGNED_SHORT, 0);
    
    var agora = (new Date).getTime();
    
    if (ultimaRotacao) {

        var delta = agora - ultimaRotacao;
        
        rotacao += (radianos * delta) / 1000.0;
        if (rotacao >= (radianos * 360)) {
        	rotacao = 0;
        }
        
        angulox = 25 * radianos;
        anguloy = 10 * rotacao;
        anguloz = 5 * radianos;
    }
    ultimaRotacao = agora;
}