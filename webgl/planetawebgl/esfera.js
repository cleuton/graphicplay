/*
 * (c) 2013 by Cleuton Sampaio
 */

function Esfera (gl, cor, raio) {
    this.vertexPositionData = [];
    this.normalData = [];
    this.indexData = [];
    this.vertexColors = [];
    this.texture = null;
    this.textureCoordData = [];
    
    /*
     * Estamos usando uma esfera com muitos polígonos. Se o desempenho ficar ruim, 
     * devemos reduzir o número de vértices, o que pode ser feito reduzindo as
     * quantidades de faixas de latitude e longitude (por exemplo: 10)
     */
    
    this.latitudeBands = 20;  // Hi Poly
    this.longitudeBands = 20; // Hi Poly
    this.cor = cor;
    this.raio = raio;
    
    this.gerar = function () {
    	
    	// Calcular vértices e normais: 
    	
        for (var latNumber = 0; latNumber <= this.latitudeBands; latNumber++) {
            var theta = latNumber * Math.PI / this.latitudeBands;
            var sinTheta = Math.sin(theta);
            var cosTheta = Math.cos(theta);

            for (var longNumber = 0; longNumber <= this.longitudeBands; longNumber++) {
              var phi = longNumber * 2 * Math.PI / this.longitudeBands;
              var sinPhi = Math.sin(phi);
              var cosPhi = Math.cos(phi);

              var x = cosPhi * sinTheta;
              var y = cosTheta;
              var z = sinPhi * sinTheta;
              var u = 1 - (longNumber / this.longitudeBands);
              var v = 1 - (latNumber / this.latitudeBands);
              
              this.textureCoordData.push(u);
              this.textureCoordData.push(v);

              this.normalData.push(x);
              this.normalData.push(y);
              this.normalData.push(z);
              
              this.vertexPositionData.push(this.raio * x);
              this.vertexPositionData.push(this.raio * y);
              this.vertexPositionData.push(this.raio * z);
              
              // Cor da esfera:
              
              this.vertexColors.push(this.cor[0]);
              this.vertexColors.push(this.cor[1]);
              this.vertexColors.push(this.cor[2]);
              this.vertexColors.push(this.cor[3]);
            }
          }	
        
        // Calcular índices dos triângulos de cada face:
        
        for (var latNumber = 0; latNumber < this.latitudeBands; latNumber++) {
            for (var longNumber = 0; longNumber < this.longitudeBands; longNumber++) {
              var first = (latNumber * (this.longitudeBands + 1)) + longNumber;
              var second = first + this.longitudeBands + 1;
              this.indexData.push(first);
              this.indexData.push(second);
              this.indexData.push(first + 1);

              this.indexData.push(second);
              this.indexData.push(second + 1);
              this.indexData.push(first + 1);
            }
          }

    };
    
    // Carregar textura
    var textura = gl.createTexture();
    textura.image = new Image();
    textura.image.onload = function () {
        gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
        gl.bindTexture(gl.TEXTURE_2D, textura);
        gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, textura.image);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
        gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR_MIPMAP_NEAREST);
        gl.generateMipmap(gl.TEXTURE_2D);
        gl.bindTexture(gl.TEXTURE_2D, null);
    };

    textura.image.src = "./terra.jpg";
    this.texture = textura;
    
}

