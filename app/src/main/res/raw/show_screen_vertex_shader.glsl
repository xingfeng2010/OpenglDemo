attribute vec4 a_Position;//顶点位置
attribute vec2 a_TextureCoordinates;//纹理位置
varying vec2 textureCoordinate;//纹理位置  与fragment_shader交互

void main() {
    textureCoordinate = a_TextureCoordinates;
    gl_Position = a_Position;
}

