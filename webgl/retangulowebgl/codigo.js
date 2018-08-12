/*
 * (c) 2013 by Cleuton Sampaio
 */

var webglCanvas = document.getElementById("webgl");
var gl;
var verticesRetangulo;
var coresRetangulo;
var modelViewMatrix;
var positionMatrix;
var glslProgram = null;

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
		draw();
	}
}

function setupObject() {
	var coordenadasVertice =  [-1.0,  1.0,  0.0,
	                           -1.0, -1.0,  0.0,
	                            1.0,  1.0,  0.0,
	                            1.0, -1.0,  0.0];

	var coresVertices      =  [ 0.99, 0.180, 0.97, 1.0,
	                            0.99, 0.180, 0.97, 1.0,
	                            0.99, 0.180, 0.97, 1.0,
	                            0.99, 0.180, 0.97, 1.0,
	                            ];

	verticesRetangulo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesRetangulo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(coordenadasVertice), gl.STATIC_DRAW);
    verticesRetangulo.itemSize = 3;
    verticesRetangulo.numItems = 4;
    
    coresRetangulo = gl.createBuffer();
    gl.bindBuffer(gl.ARRAY_BUFFER, coresRetangulo);
    gl.bufferData(gl.ARRAY_BUFFER, new Float32Array(coresVertices), gl.STATIC_DRAW);
    coresRetangulo.itemSize = 4;
    coresRetangulo.numItens = 4;
    
    modelViewMatrix = mat4.create();
    positionMatrix = mat4.create();

}

function draw() {
	setupView(modelViewMatrix,positionMatrix,gl);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);
    gl.bindBuffer(gl.ARRAY_BUFFER, verticesRetangulo);
    gl.vertexAttribPointer(glslProgram.vertexPositionAttribute, verticesRetangulo.itemSize, gl.FLOAT, false, 0, 0);
    gl.bindBuffer(gl.ARRAY_BUFFER, coresRetangulo);
    gl.vertexAttribPointer(glslProgram.vertexColorAttribute, coresRetangulo.numItens, gl.FLOAT, false, 0, 0);    
    gl.drawArrays(gl.TRIANGLE_STRIP, 0, verticesRetangulo.numItems);
}