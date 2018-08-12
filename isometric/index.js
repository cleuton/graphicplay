var c = document.getElementById("canvas");
var sizeWidth = c.getContext("2d").canvas.clientWidth;
var sizeHeight = c.getContext("2d").canvas.clientHeight;
var r3 = Math.sqrt(3);
var r2 = Math.sqrt(2);
var oner6 = 1 / Math.sqrt(6);

function Matrix(ary) {
    this.mtx = ary
    this.height = ary.length;
    this.width = ary[0].length;
}


Matrix.prototype.mult = function(other) {
    if (this.width != other.height) {
        throw "error: incompatible sizes";
    }
 
    var result = [];
    for (var i = 0; i < this.height; i++) {
        result[i] = [];
        for (var j = 0; j < other.width; j++) {
            var sum = 0;
            for (var k = 0; k < this.width; k++) {
                sum += this.mtx[i][k] * other.mtx[k][j];
            }
            result[i][j] = sum;
        }
    }
    return new Matrix(result); 
}


var projMatrix = new Matrix([[r3 * oner6,0,-1*r3 * oner6],
                  [1 * oner6, 2 * oner6, 1 * oner6],
                  [r2 * oner6, -1*r2 * oner6,r2 * oner6]]);

var redMatrix = new Matrix([[1,0,0],[0,1,0],[0,0,0]]);
       
 

function setupCanvas() {
  var ctx = c.getContext("2d");
  var centro = {x : 0, y : 0, z : 0};
  var cubo = new Cubo(centro, 50);
  var cuboProjetado = transform(cubo.vertices);
  desenhar(ctx, cuboProjetado);
}  

function transform(pontos) {
  var novosPontos = [];
  for (var x=0; x<pontos.length; x++) {
    novosPontos.push(project(pontos[x]));
  }
  return novosPontos;
}

function project(ponto) {
  var novoPonto = {x : 0, y : 0};
  var mPonto = new Matrix([[ponto.x],[ponto.y],[ponto.z]]);
  var res1 = projMatrix.mult(mPonto);
  var res2 = redMatrix.mult(res1);
  novoPonto.x = res2.mtx[0][0];
  novoPonto.y = res2.mtx[1][0];
  return novoPonto;
}

function desenhar(ctx,vertices) {
  var novosVertices = [];
  for (var x=0; x<vertices.length; x++) {
    novosVertices.push(reposicionar(vertices[x]));
  }
  console.log(JSON.stringify(novosVertices));
  // Face da frente:

  ctx.moveTo(novosVertices[0].x,novosVertices[0].y);
  ctx.lineTo(novosVertices[1].x,novosVertices[1].y);
  ctx.lineTo(novosVertices[3].x,novosVertices[3].y);
  ctx.lineTo(novosVertices[2].x,novosVertices[2].y);
  ctx.lineTo(novosVertices[0].x,novosVertices[0].y);
  ctx.stroke();
  
  // Face dos fundos:

  ctx.moveTo(novosVertices[4].x,novosVertices[4].y);
  ctx.lineTo(novosVertices[5].x,novosVertices[5].y);
  ctx.lineTo(novosVertices[7].x,novosVertices[7].y);
  ctx.lineTo(novosVertices[6].x,novosVertices[6].y);
  ctx.lineTo(novosVertices[4].x,novosVertices[4].y);
  ctx.stroke();

  // Face de cima:

  ctx.moveTo(novosVertices[4].x,novosVertices[4].y);
  ctx.lineTo(novosVertices[5].x,novosVertices[5].y);
  ctx.lineTo(novosVertices[1].x,novosVertices[1].y);
  ctx.lineTo(novosVertices[0].x,novosVertices[0].y);
  ctx.lineTo(novosVertices[4].x,novosVertices[4].y);
  ctx.stroke();
  
  // Face de baixo:

  ctx.moveTo(novosVertices[6].x,novosVertices[6].y);
  ctx.lineTo(novosVertices[7].x,novosVertices[7].y);
  ctx.lineTo(novosVertices[3].x,novosVertices[3].y);
  ctx.lineTo(novosVertices[2].x,novosVertices[2].y);
  ctx.lineTo(novosVertices[6].x,novosVertices[6].y);
  ctx.stroke();
  
  // Face direita:

  ctx.moveTo(novosVertices[1].x,novosVertices[1].y);
  ctx.lineTo(novosVertices[5].x,novosVertices[5].y);
  ctx.lineTo(novosVertices[7].x,novosVertices[7].y);
  ctx.lineTo(novosVertices[3].x,novosVertices[3].y);
  ctx.lineTo(novosVertices[1].x,novosVertices[1].y);
  ctx.stroke();
  
  // Face esquerda:

  ctx.moveTo(novosVertices[0].x,novosVertices[0].y);
  ctx.lineTo(novosVertices[4].x,novosVertices[4].y);
  ctx.lineTo(novosVertices[6].x,novosVertices[6].y);
  ctx.lineTo(novosVertices[2].x,novosVertices[2].y);
  ctx.lineTo(novosVertices[0].x,novosVertices[0].y);
  ctx.stroke();
  
    
}

function reposicionar(ponto) {
  var nova = { x : 
                  ponto.x + sizeWidth / 2,
               y : 
                  (ponto.y * -1) + sizeHeight / 2
           }
  return nova;
}


 

