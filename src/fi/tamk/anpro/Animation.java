package fi.tamk.anpro;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * Sis�lt�� yhden animaation tekstuurit.
 */
public class Animation
{
    /* Kuvaruutujen tiedot */
    public  int[] frames;
    public  int   length;
    private float imageSize = 0;
    
    /* Puskuri ja taulukko vektoreita varten */
    public FloatBuffer vertexBuffer;
    public float[] vertices;

    /* Puskuri ja taulukko tekstuuria varten */
    private FloatBuffer textureBuffer;
    private float texture[] = {
        0.0f, 1.0f,		// yl�vasen
        0.0f, 0.0f,		// alavasen
        1.0f, 1.0f,		// yl�oikea
        1.0f, 0.0f		// alaoikea
    };

    /**
     * Alustaa luokan muuttujat ja kutsuu loadFramea jokaista ruutua varten.
     * 
     * @param GL10    OpenGL-konteksti
     * @param Context Ohjelman konteksti
     * @param String  Tekstuurin tunnus
     * @paran int     Animaation pituus
     */
    public Animation(GL10 _gl, Context context, String _id, int _length)
    {
        frames = new int[_length];
        length = _length;

        // Ladataan tekstuurit
        try {
            String idField = _id + "_anim_";
            for (int i = 0; i < frames.length; ++i) {
                loadFrame(_gl, context, frames, i, R.drawable.class.getField(idField+"_"+i).getInt(getClass()));
            }
        } catch (Exception e) {
            // TODO: K�sittele t�m�
        }
        
        // M��rit� vektorit
        vertices = new float[12];
        vertices[0] = (-1)*imageSize;
        vertices[1] = vertices[0];
        vertices[2] = 0.0f;
        vertices[3] = vertices[0];
        vertices[4] = imageSize;
        vertices[5] = 0.0f;
        vertices[6] = imageSize;
        vertices[7] = vertices[0];
        vertices[8] = 0.0f;
        vertices[9] = imageSize;
        vertices[10] = imageSize;
        vertices[11] = 0.0f;
        
        // Varaa muistia vektoreita varten ja sijoita ne puskureihin
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        
        byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
    }

    /**
     * Lataa yhden tekstuurin muistiin.
     * 
     * @param GL10    OpenGL-konteksti
     * @param Context Ohjelman konteksti
     * @param int[]   Taulukko tekstuuria varten
     * @paran int     Kuvaruudun j�rjestysnumero
     * @param int     Tekstuurin tunnus resursseissa
     */
    public final void loadFrame(GL10 gl, Context context, int[] var, int offset, int id)
    {
    	// Ladataan bitmap muistiin
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), id);
        
        // Asetetaan mitat
        if (imageSize == 0) {
            imageSize = (float)bitmap.getWidth();
        }

        // Muunnetaan bitmap OpenGL-tekstuuriksi
        gl.glGenTextures(1, var, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, var[offset]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Tyhjennet��n bitmap muistista
        bitmap.recycle();
    }
    
    /**
     * Piirt�� framen ruudulle.
     * 
     * @param GL10  OpenGL-konteksti
     * @param float X-koordinaatti
     * @param float Y-koordinaatti
     * @param int   Suunta
     * @param int   Kuvaruudun j�rjestysnumero
     */
    public final void draw(GL10 _gl, float _x, float _y, int _direction, int _frame)
    {
        // Resetoidaan mallimatriisi
        _gl.glLoadIdentity();
        
        // Siirret��n ja k��nnet��n mallimatriisia
        _gl.glTranslatef(_x, _y, 0);
        _gl.glBindTexture(GL10.GL_TEXTURE_2D, frames[_frame]);
        
        // Avataan tekstuuri- ja vektoritaulukot k�ytt��n
        _gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        _gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
        // Valitaan neli�n n�ytett�v� puoli
        _gl.glFrontFace(GL10.GL_CW);
        
        // Otetaan vektori- ja tekstuuripuskurit k�ytt��n
        _gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        _gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
    
        // Piirret��n neli� ja tekstuuri
        _gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length/3);
    
        // Lukitaan tekstuuri- ja vektoritaulukot pois k�yt�st�
        _gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        _gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }
}
