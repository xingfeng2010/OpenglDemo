attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
uniform mat4 u_Matrix;
uniform mat4 u_CoordMatrix;
varying vec2 textureCoordinate;

void main(){
    gl_Position = u_Matrix*a_Position;
    textureCoordinate = (u_CoordMatrix*vec4(a_TextureCoordinates,0,1)).xy;
}