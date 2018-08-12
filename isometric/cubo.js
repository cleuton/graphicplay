function Cubo(centro, lado) {
  // centro é um objeto com 3 propriedades: x, y e z
  // lado é a medida de cada lado do cubo
  var pontos = [];
  var metade = lado / 2;
  this.vertices = [];
  for (var x=0; x<8; x++) {
    var incrx = (x % 2) == 0 ? -1 : 1;
    var incry = (x == 2 || x == 6 || x == 3 || x == 7) ? -1 : 1;
    var incrz = (x < 4) ? -1 : 1;
    var vertice = {
                    x : centro.x + incrx * metade,
                    y : centro.y + incry * metade,
                    z : centro.z + incrz * metade
                  };
    this.vertices.push(vertice);                      
  }
}