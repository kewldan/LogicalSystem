attribute vec4 a_position; //позиция вершины
uniform mat4 u_projTrans;  //матрица, которая содержим данные для преобразования проекции и вида

void main(){
   gl_Position =  u_projTrans * a_position;
}