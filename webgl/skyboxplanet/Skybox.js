/*
 * (c) 2013 by Cleuton Sampaio
 */

function Skybox (gl, glslProgram, size, textureNames) {
	this.size = size;
	this.gl = gl;
	this.glslProgram = glslProgram;
	this.vertexPositionData = [];
    this.indexData = [];
    this.textureCoordData = [];
    this.textureHandlers = [];
    this.normalData = [];
    this.carregado = false;
    this.intervalo = null;
    
    /*
     * Para funcionar, as texturas devem ser informadas nessa ordem:
     * 0 = Frontal; 1 = Direita; 2 = Superior; 3 = Esquerda; 4 = Inferior
     * 5 = Trazeira.
     */
    this.textureNames = textureNames;
    
    this.gerar = function() {
    	
    	/*
    	 * Os vértices de cada face, começando pela frontal
    	 */
    	
    	this.vertexPositionData  =  [
    	                             // Front face
    	                             -1.0, -1.0,  1.0,
    	                              1.0, -1.0,  1.0,
    	                              1.0,  1.0,  1.0,
    	                             -1.0,  1.0,  1.0,
    	                             
    	                             // Back face
    	                             -1.0, -1.0, -1.0,
    	                             -1.0,  1.0, -1.0,
    	                              1.0,  1.0, -1.0,
    	                              1.0, -1.0, -1.0,
    	                             
    	                             // Top face
    	                             -1.0,  1.0, -1.0,
    	                             -1.0,  1.0,  1.0,
    	                              1.0,  1.0,  1.0,
    	                              1.0,  1.0, -1.0,
    	                             
    	                             // Bottom face
    	                             -1.0, -1.0, -1.0,
    	                              1.0, -1.0, -1.0,
    	                              1.0, -1.0,  1.0,
    	                             -1.0, -1.0,  1.0,
    	                             
    	                             // Right face
    	                              1.0, -1.0, -1.0,
    	                              1.0,  1.0, -1.0,
    	                              1.0,  1.0,  1.0,
    	                              1.0, -1.0,  1.0,
    	                             
    	                             // Left face
    	                             -1.0, -1.0, -1.0,
    	                             -1.0, -1.0,  1.0,
    	                             -1.0,  1.0,  1.0,
    	                             -1.0,  1.0, -1.0
    	                           
    	                           ];
    	
    	this.normalData = 			[
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
    	
    	for (var x=0; x < this.vertexPositionData.length; x++) {
    		this.vertexPositionData[x] = this.vertexPositionData[x] * this.size;
    	}
    	
    	this.textureCoordData = [
    	                         // Front
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0,
    	                         // Back
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0,
    	                         // Top
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0,
    	                         // Bottom
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0,
    	                         // Right
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0,
    	                         // Left
    	                         0.0,  0.0,
    	                         1.0,  0.0,
    	                         1.0,  1.0,
    	                         0.0,  1.0
    	                    
    	                   ];

    	this.indexData = new Uint8Array([
    	                                 0,  1,  2,      0,  2,  3,    // front
    	                                 4,  5,  6,      4,  6,  7,    // back
    	                                 8,  9,  10,     8,  10, 11,   // top
    	                                 12, 13, 14,     12, 14, 15,   // bottom
    	                                 16, 17, 18,     16, 18, 19,   // right
    	                                 20, 21, 22,     20, 22, 23    // left
    	                    
    	                    ]);
    	
        // Carregar texturas
    	
    	var gl = this.gl;
    	this.textureHandlers = [null,null,null,null,null,null];
    	for (var x=0; x < this.textureNames.length; x++) {
    		this.textureHandlers[x] = this.gl.createTexture();
    		this.textureHandlers[x].image = new Image();
    		this.textureHandlers[x].loaded = false;
            this.loadImage(this.gl, this.textureNames[x], this.textureHandlers[x]);
    	}
    	
    	this.intervalo = setInterval(function() {
    		skybox.verificar();
    		if (skybox.carregado) {
   	    		clearInterval(this.intervalo);
    		}
    	}
    	, 100);
   	
        // Criar buffers
        
        this.vertices = this.gl.createBuffer();
        this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.vertices);
        this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array(this.vertexPositionData), this.gl.STATIC_DRAW);
        this.vertices.itemSize = 3;
        this.vertices.numItems = this.vertexPositionData.length;
        
        this.indices = this.gl.createBuffer();
        this.gl.bindBuffer(this.gl.ELEMENT_ARRAY_BUFFER, this.indices);
        this.gl.bufferData(this.gl.ELEMENT_ARRAY_BUFFER, new Uint16Array(this.indexData), this.gl.STATIC_DRAW);
        this.indices.itemSize = 3;
        this.indices.numItems = this.indexData.length;
        
        this.normais = this.gl.createBuffer();
        this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.normais);
        this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array(this.normalData), this.gl.STATIC_DRAW);
        this.normais.itemSize = 3;
        this.normais.numItems = this.normalData.length;
       
        this.texturas = this.gl.createBuffer();
        this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.texturas);
        this.gl.bufferData(this.gl.ARRAY_BUFFER, new Float32Array(this.textureCoordData), this.gl.STATIC_DRAW);
        this.texturas.itemSize = 2;
        this.texturas.numItems = this.textureCoordData.length;
    };
    
    this.renderizar = function() {
    	
    	for (var face=0; face < 6; face++) {
    		/*
    		 * Temos que trabalhar cada face do cubo, já que cada uma tem a sua
    		 * própria textura
    		 */
            // Ativou o container de textura
            this.gl.activeTexture(this.gl.TEXTURE0);
            
            // Associou a textura ao parâmetro do Shader
            this.gl.bindTexture(this.gl.TEXTURE_2D, this.textureHandlers[face]);
            this.gl.uniform1i(this.glslProgram.textureUnitAttribute, 0);
            
            // Informou o atributo de skybox: zero = normal, um = skybox - sem shading
            this.gl.uniform1i(this.glslProgram.skyBoxAttribute, 1);
            
            // Informou o atributo de vértices
            this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.vertices);
            this.gl.vertexAttribPointer(this.glslProgram.vertexPositionAttribute, this.vertices.itemSize, this.gl.FLOAT, false, 0, 0);
            
            // Informou o atributo de coordenadas de textura    
            this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.texturas);
            this.gl.vertexAttribPointer(this.glslProgram.textureCoordAttribute, this.texturas.itemSize, this.gl.FLOAT, false, 0, 0);
            
            // Informou o atributo de normais das faces
            /*
             * Nós não usamos normais para Skybox, mas temos que informar qualquer coisa
             */
            this.gl.bindBuffer(this.gl.ARRAY_BUFFER, this.normais);
            this.gl.vertexAttribPointer(this.glslProgram.normalPositionAttribute, this.normais.itemSize, this.gl.FLOAT, false, 0, 0);

            
            // Informou o atributo de índices dos vértices
            this.gl.bindBuffer(this.gl.ELEMENT_ARRAY_BUFFER, this.indices);
            
            // Unsigned Short = 16 bytes
            
            this.gl.drawElements(this.gl.TRIANGLES, 6, this.gl.UNSIGNED_SHORT, face * 6 * 2);
            
    	}
    	
    };
    
    this.loadImage = function(gl, nomeTextura, handlerTextura) {

    	// Carregar textura
    	
    	handlerTextura.image.onload = function () {
        	gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
        	gl.bindTexture(gl.TEXTURE_2D, handlerTextura);
        	gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, handlerTextura.image);
        	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
        	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
        	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);
        	gl.generateMipmap(gl.TEXTURE_2D);
        	gl.bindTexture(gl.TEXTURE_2D, null);
        	handlerTextura.loaded = true;
        };

        handlerTextura.image.src = nomeTextura;

    };
    
    this.verificar = function() {
    	this.carregado = true;
    	for (var x = 0; x < this.textureHandlers.length; x++) {
    		if (!this.textureHandlers[x].loaded) {
    			this.carregado = false;
    		}
    	}

    };
}