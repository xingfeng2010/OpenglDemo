attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
uniform mat4 u_Matrix;

varying vec2 textureCoordinate;

void main(){
    gl_Position = u_Matrix * a_Position;
    textureCoordinate = a_TextureCoordinates;
}