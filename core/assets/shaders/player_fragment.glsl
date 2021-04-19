varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

//custom uniforms used as flags
uniform int hasArmor;
uniform int hasHolyLance;

//Walfrit red rgb values
float Walfrit_red_r = 237.0/255.0;
float Walfrit_red_g = 28.0/255.0;
float Walfrit_red_b = 36.0/255.0;
vec3 Walfrit_red_color = vec3(Walfrit_red_r, Walfrit_red_g, Walfrit_red_b);

void main() {

    //extract current pixel color
    vec4 color = texture2D(u_texture, v_texCoords).rgba;

    //If has armor, the red is shifted to gray
    if (hasArmor == 1 && color.xyz == Walfrit_red_color){
        color.xyz=vec3(0.5, 0.5, 0.5);
    } else if (hasHolyLance == 0 && color.xyz == vec3(1, 1, 1)){
        //If holy lance hasn't benne found, the white is set to black
        color.xyz=vec3(0,0,0);
    }

    //Set colors, with modifications if present
    gl_FragColor = color;
}
