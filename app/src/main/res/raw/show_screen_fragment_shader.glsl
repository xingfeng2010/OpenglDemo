#extension GL_OES_EGL_image_external : require //申明使用扩展纹理
 precision mediump float;//精度 为float
 varying vec2 textureCoordinate;//纹理位置  接收于vertex_shader
 uniform sampler2D  uTexture;
 void main() {
     gl_FragColor=texture2D(uTexture, textureCoordinate);
 }