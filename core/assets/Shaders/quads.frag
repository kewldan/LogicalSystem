uniform sampler2D u_texture; //Not used
uniform float zoom, ox, oy;

void main(){
    if (mod((gl_FragCoord.x - ox) / zoom, 2) < 1 ^^ mod((gl_FragCoord.y - oy) / zoom, 2) < 1){
        gl_FragColor = vec4(0.2, 0.202, 0.23, 1);
    } else {
        gl_FragColor = vec4(0.17, 0.18, 0.2, 1);
    }
}