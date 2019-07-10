precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D uTexture;
void main() {
    gl_FragColor = texture2D( uTexture, textureCoordinate );
}