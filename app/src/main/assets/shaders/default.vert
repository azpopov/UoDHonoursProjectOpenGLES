uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;
attribute vec4 a_Colour;

varying vec4 v_Color;

void main()
 {
    v_Color = a_Colour;
    gl_Position = u_MVPMatrix   	* a_Position;

}