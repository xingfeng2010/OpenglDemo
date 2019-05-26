attribute vec4 aPosition;
attribute vec2 aTextureCoord;
varying vec2 vTextureCoord;
uniform mat4 uMatrix;

void main() {
    vTextureCoord = aTextureCoord;
    gl_Position = aPosition * uMatrix;
}
