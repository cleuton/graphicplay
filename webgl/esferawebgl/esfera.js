/*
 * (c) 2013 by Cleuton Sampaio
 */

function Esfera (gl, cor, raio) {
    this.vertexPositionData = [];
    this.normalData = [];
    this.indexData = [];
    this.vertexColors = [];
    
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
    
    
}