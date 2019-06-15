#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
varying vec4 v_color;

void main() {
vec2 pos = mod(v_texCoords.xy,vec2(1.0));
		if ((pos.x > 0.5)&&(pos.y > 0.5)){
			gl_FragColor=vec4(1.0, 1.0, 1.0, 1.0)*v_color;
		}
		if ((pos.x < 0.5)&&(pos.y < 0.5)){
			gl_FragColor=vec4(1.0, 1.0, 1.0, 1.0)*v_color;
		}
		if ((pos.x < 0.5)&&(pos.y > 0.5)){
			gl_FragColor=vec4(0.0, 0.0, 0.0, 1.0)*v_color;
		}
		if ((pos.x > 0.5)&&(pos.y < 0.5)){
			gl_FragColor=vec4(0.0, 0.0, 0.0, 1.0)*v_color;
		}
}