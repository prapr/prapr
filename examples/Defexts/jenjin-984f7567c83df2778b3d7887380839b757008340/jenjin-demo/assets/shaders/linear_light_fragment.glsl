#ifdef GL_ES
#define LOWP lowp
    precision mediump float;
#else
    #define LOWP
#endif

float BlendLinearDodgef(float base, float blend)
{
    return min(base + blend, 1.0);
}

float BlendLinearBurnf(float base, float blend)
{
    return max(base + blend - 1.0, 0.0);
}

float BlendLinearLightf(float base, float blend)
{
    return (blend < 0.5 ? BlendLinearBurnf(base, (2.0 * blend)) : BlendLinearDodgef(base, (2.0 * (blend - 0.5))));
}

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main()
{
     vec4 light = texture2D(u_texture, v_texCoords);
     gl_FragColor = vec4(BlendLinearLightf(v_color.r,light.r),BlendLinearLightf(v_color.g,light.g),BlendLinearLightf(v_color.b,light.b),light.a);
}