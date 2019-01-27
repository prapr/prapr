#ifdef GL_ES
precision mediump float;
#endif
varying vec2 pos_in;
varying vec4 v_col;
void main() {
vec2 pos = mod(pos_in.xy,vec2(1.0));
		if ((pos.x > 0.5)&&(pos.y > 0.5)){
			gl_FragColor=vec4(1.0, 1.0, 1.0, 1.0)*v_col;
		}
		if ((pos.x < 0.5)&&(pos.y < 0.5)){
			gl_FragColor=vec4(1.0, 1.0, 1.0, 1.0)*v_col;
		}
		if ((pos.x < 0.5)&&(pos.y > 0.5)){
			gl_FragColor=vec4(0.0, 0.0, 0.0, 1.0)*v_col;
		}
		if ((pos.x > 0.5)&&(pos.y < 0.5)){
			gl_FragColor=vec4(0.0, 0.0, 0.0, 1.0)*v_col;
		}
}