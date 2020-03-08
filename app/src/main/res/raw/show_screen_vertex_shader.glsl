attribute vec4 a_Position;//顶点位置
attribute vec2 a_TextureCoordinates;//纹理位置
varying vec2 textureCoordinate;//纹理位置  与fragment_shader交互

uniform mat4 u_Matrix;

void main() {
    gl_Position = u_Matrix*a_Position;
    textureCoordinate = a_TextureCoordinates;
}

