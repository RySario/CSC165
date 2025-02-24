package myGame;
import tage.*;
import tage.shapes.*;

public class manualTent extends ManualObject{
    private float[] vertices = new float[]{
        -1.0f, 0.0f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, 0.0f, // front triangle
        -1.0f, 0.0f, -1.0f,  0.0f, 1.0f, -1.0f,  1.0f, 0.0f, -1.0f, // back triangle 
         1.0f, 0.0f, 0.0f,   0.0f, 1.0f, 0.0f,   1.0f, 0.0f, -1.0f, // right rect. left triangle
         0.0f, 1.0f, 0.0f,   0.0f, 1.0f, -1.0f,  1.0f, 0.0f, -1.0f, // right rect. right triangle
        -1.0f, 0.0f, 0.0f,   0.0f, 1.0f, 0.0f,   0.0f, 1.0f, -1.0f,  // left rect. left triangle
        -1.0f, 0.0f, 0.0f,   0.0f, 1.0f, -1.0f, -1.0f, 0.0f, -1.0f, // left rect. right triangle
        -1.0f, 0.0f, 0.0f,  -1.0f, 0.0f, -1.0f,  1.0f, 0.0f, -1.0f,  // bottom rect. left triangle
        -1.0f, 0.0f, 0.0f,   1.0f, 0.0f, 0.0f,   1.0f, 0.0f, -1.0f,  // bottom rect. right triangle
        
    };

    private float[] texcoords = new float[]{
        0.0f, 0.0f,  0.5f, 1.0f,  1.0f, 0.0f, // front triangle
        0.0f, 0.0f,  0.5f, 1.0f,  1.0f, 0.0f, // back triangle
        0.0f, 0.0f,  0.0f, 1.0f,  1.0f, 0.0f, // right rect. left triangle
        0.0f, 1.0f,  1.0f, 1.0f,  1.0f, 0.0f, // right rect. right triangle
        1.0f, 0.0f,  1.0f, 1.0f,  0.0f, 1.0f, // left rect. left triangle
        1.0f, 0.0f,  0.0f, 1.0f,  0.0f, 0.0f, // left rect. right triangle
        0.0f, 1.0f,  1.0f, 1.0f,  1.0f, 0.0f, // bottom rect. left triangle
        0.0f, 1.0f,  0.0f, 0.0f,  1.0f, 0.0f, // bottom rect. right triangle
    };

    private float[] normals = new float[]{
        -3.0f, 0.0f, 0.0f,  -3.0f, 3.0f, 1.5f,  -3.0f, 0.0f, 3.0f, // front triangle
        -6.0f, 0.0f, 0.0f,  -6.0f, 3.0f, 1.5f,  -6.0f, 0.0f, 3.0f, // back triangle 
        -3.0f, 0.0f, 3.0f,  -3.0f, 3.0f, 1.5f,  -6.0f, 0.0f, 3.0f, // right rect. left triangle
        -3.0f, 3.0f, 1.5f,  -6.0f, 0.0f, 3.0f   -6.0f, 3.0f, 1.5f, // right rect. right triangle
        -3.0f, 0.0f, 0.0f,  -6.0f, 0.0f, 0.0f,  -3.0f, 3.0f, 1.5f, // left rect. left triangle
        -3.0f, 3.0f, 1.5f,  -6.0f, 3.0f, 1.5f,  -6.0f, 0.0f, 0.0f, // left rect. right triangle
        -3.0f, 0.0f, 0.0f,  -3.0f, 0.0f, 3.0f,  -6.0f, 0.0f, 3.0f, // bottom rect. left triangle
        -3.0f, 0.0f, 0.0f,  -6.0f, 0.0f, 0.0f,  -6.0f, 0.0f, 3.0f  // bottom rect. right triangle
    };

   

    public manualTent(){
        super();

        setNumVertices(24);
        setVertices(vertices);
        setTexCoords(texcoords); 
        setNormals(normals);
    }
}