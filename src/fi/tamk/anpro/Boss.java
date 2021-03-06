package fi.tamk.anpro;

import javax.microedition.khronos.opengles.GL10;

public class Boss extends GameObject
{
	private Wrapper wrapper;
	
	public Boss(int _speed)
	{
		super(0);

        /* Haetaan tarvittavat luokat k�ytt��n */
		wrapper = Wrapper.getInstance();
		
    	/* Alustetaan muuttujat */
	    z = 8;
	    
        // Haetaan k�ytett�vien animaatioiden pituudet
        animationLength = new int[GLRenderer.AMOUNT_OF_MOTHERSHIP_ANIMATIONS];
        
        for (int i = 0; i < GLRenderer.AMOUNT_OF_MOTHERSHIP_ANIMATIONS; ++i) {
            if (GLRenderer.mothershipAnimations[i] != null) {
                animationLength[i] = GLRenderer.mothershipAnimations[i].length;
            }
        }

        /* M��ritet��n objektin tila (piirtolista ja teko�ly) */
		wrapper.addToDrawables(this);
	}

	/* =======================================================
	 * Perityt funktiot
	 * ======================================================= */
	@Override
	public void draw(GL10 _gl)
	{
        // Tarkistaa onko animaatio p��ll� ja kutsuu oikeaa animaatiota tai tekstuuria
        if (usedAnimation >= 0){
            GLRenderer.mothershipAnimations[usedAnimation].draw(_gl, x, y, direction, currentFrame);
        }
        else{
            GLRenderer.mothershipTextures[usedTexture].draw(_gl, x, y, direction, 0);
        }
	}

	@Override
	protected void triggerEndOfAction()
	{
		// ...
	}

}
