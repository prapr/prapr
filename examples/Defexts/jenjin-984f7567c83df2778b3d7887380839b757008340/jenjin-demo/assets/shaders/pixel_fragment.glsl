#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main()
{
 float dx = 15.*(1./128.);
 float dy = 10.*(1./128.);
 vec2 coord = vec2(dx*floor(v_texCoords.x/dx),
                   dy*floor(v_texCoords.y/dy));
 gl_FragColor = v_color * texture2D(u_texture, coord);
}