#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_col;
void main() {
   gl_FragColor = vec4(0.5,0.5,1.0,1.0);
}