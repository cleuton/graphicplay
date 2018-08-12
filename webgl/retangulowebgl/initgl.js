/*
 * (c) 2013 by Cleuton Sampaio
 */

function getGLcontext(canvas) {
	
	/*
	 * Alguns navegadores não implementam o contexto "webgl", então, 
	 * testamos também o "experimental-webgl"
	 */
	
	var glCtx = null;
	try {
	    glCtx = canvas.getContext("webgl");
	    if (!glCtx) {
	    	glCtx = canvas.getContext("experimental-webgl");	
	    }
	}
	catch(excep) {
		
	}
	return glCtx;
}

function compileGLSL() {
	
	/*
	 * Precisamos obter o código-fonte dos dois shaders, compilá-los, 
	 * linkeditarmos o programa contendo os dois, e obter o ponteiro 
	 * para os atributos dos dois shaders.
	 */
	
	var vertexShader = getShader(gl,"vertex-shader");
	var fragmentShader = getShader(gl, "fragment-shader");
	
	glslProgram = gl.createProgram();
	
	 gl.attachShader(glslProgram, vertexShader);
     gl.attachShader(glslProgram, fragmentShader);
     gl.linkProgram(glslProgram);

     if (!gl.getProgramParameter(glslProgram, gl.LINK_STATUS)) {
         alert("Could not initialise shaders");
     }

     gl.useProgram(glslProgram);
     
     setAttributes();
}

function setAttributes() {
	
	/*
	 * Pegamos ponteiros para os atributos dos dois Shaders, incluindo as matrizes.
	 */
	
	glslProgram.vertexPositionAttribute = gl.getAttribLocation(glslProgram, "aVertexPosition");
    gl.enableVertexAttribArray(glslProgram.vertexPositionAttribute);

    glslProgram.vertexColorAttribute = gl.getAttribLocation(glslProgram, "aVertexColor");
    gl.enableVertexAttribArray(glslProgram.vertexColorAttribute);

    glslProgram.pMatrixUniform = gl.getUniformLocation(glslProgram, "uPMatrix");
    glslProgram.mvMatrixUniform = gl.getUniformLocation(glslProgram, "uMVMatrix");
}

function getShader(gl, id) {
	
	/*
	 * Essa função foi sugerida pelo Mozilla, para obter os shaders diretamente do 
	 * código HTML 5 (veja em: https://developer.mozilla.org/en-US/docs/Web/WebGL/Adding_2D_content_to_a_WebGL_context)
	 */
	
    var shaderScript = document.getElementById(id);
    if (!shaderScript) {
        return null;
    }

    var str = "";
    var k = shaderScript.firstChild;
    while (k) {
        if (k.nodeType == 3) {
            str += k.textContent;
        }
        k = k.nextSibling;
    }

    var shader;
    if (shaderScript.type == "x-shader/x-fragment") {
        shader = gl.createShader(gl.FRAGMENT_SHADER);
    } else if (shaderScript.type == "x-shader/x-vertex") {
        shader = gl.createShader(gl.VERTEX_SHADER);
    } else {
        return null;
    }

    gl.shaderSource(shader, str);
    gl.compileShader(shader);

    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
        alert(gl.getShaderInfoLog(shader));
        return null;
    }

    return shader;
}

function setupView (mvMatrix, pMatrix, gl) {
	
	/*
	 * Aqui, ajustamos a janela (viewport) e as matrizes de visão e projeção
	 * que vamos utilizar. 
	 */
	
	mvMatrix = mat4.create();
    pMatrix = mat4.create();
    gl.viewport(0, 0, webglCanvas.width, webglCanvas.height);

    /*
     * Ajusta a matriz de visão (câmera), posicionando atrás da origem:
     */
    
    var proporcao = webglCanvas.width / webglCanvas.height;
    var esquerda = -proporcao;
    var direita = proporcao;
    var baixo = -1.0;
    var cima = 1.0;
    var perto = 1.0;
    var longe = 10.0;

    mat4.frustum(pMatrix, esquerda, direita, baixo, cima, perto, longe);

    /*
     * Ajusta a matriz de projeção. Queremos uma projeção 
     */
    
    
    var olho = vec3.fromValues(0,0,7);
    var horizonte = vec3.fromValues(0,0,-5);
    var cima = vec3.fromValues(0,1,0);

    mat4.lookAt(mvMatrix, olho, horizonte, cima);

    gl.uniformMatrix4fv(glslProgram.pMatrixUniform, false, pMatrix);
    gl.uniformMatrix4fv(glslProgram.mvMatrixUniform, false, mvMatrix);    
}