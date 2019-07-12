#extension GL_OES_EGL_image_external : require //申明使用扩展纹理
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES uTexture;
void main() {
    gl_FragColor = texture2D(uTexture, textureCoordinate );
}