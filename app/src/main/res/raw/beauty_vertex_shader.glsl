attribute vec4 a_Position;
attribute vec2 a_TextureCoordinates;
varying vec2 textureCoordinate;
varying vec2 blurCoord1s[14];
const highp float mWidth=720.0;
const highp float mHeight=1280.0;
uniform mat4 u_Matrix;
void main( )
{
    gl_Position = u_Matrix * a_Position;
    textureCoordinate = a_TextureCoordinates;

    highp float mul_x = 2.0 / mWidth;
    highp float mul_y = 2.0 / mHeight;

    // 14个采样点
    blurCoord1s[0] = a_TextureCoordinates + vec2( 0.0 * mul_x, -10.0 * mul_y );
    blurCoord1s[1] = a_TextureCoordinates + vec2( 8.0 * mul_x, -5.0 * mul_y );
    blurCoord1s[2] = a_TextureCoordinates + vec2( 8.0 * mul_x, 5.0 * mul_y );
    blurCoord1s[3] = a_TextureCoordinates + vec2( 0.0 * mul_x, 10.0 * mul_y );
    blurCoord1s[4] = a_TextureCoordinates + vec2( -8.0 * mul_x, 5.0 * mul_y );
    blurCoord1s[5] = a_TextureCoordinates + vec2( -8.0 * mul_x, -5.0 * mul_y );
    blurCoord1s[6] = a_TextureCoordinates + vec2( 0.0 * mul_x, -6.0 * mul_y );
    blurCoord1s[7] = a_TextureCoordinates + vec2( -4.0 * mul_x, -4.0 * mul_y );
    blurCoord1s[8] = a_TextureCoordinates + vec2( -6.0 * mul_x, 0.0 * mul_y );
    blurCoord1s[9] = a_TextureCoordinates + vec2( -4.0 * mul_x, 4.0 * mul_y );
    blurCoord1s[10] = a_TextureCoordinates + vec2( 0.0 * mul_x, 6.0 * mul_y );
    blurCoord1s[11] = a_TextureCoordinates + vec2( 4.0 * mul_x, 4.0 * mul_y );
    blurCoord1s[12] = a_TextureCoordinates + vec2( 6.0 * mul_x, 0.0 * mul_y );
    blurCoord1s[13] = a_TextureCoordinates + vec2( 4.0 * mul_x, -4.0 * mul_y );
}