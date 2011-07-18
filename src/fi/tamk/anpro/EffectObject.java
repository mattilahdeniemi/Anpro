package fi.tamk.anpro;

import javax.microedition.khronos.opengles.GL10;

/**
 *  Sis�lt�� yhden efektin toiminnot.
 */
public class EffectObject extends GameObject
{
	// Efektin tyyppi
	private byte effectType;
	
	// Effektin tila
	public boolean activated = false;

	// Wrapper
	private Wrapper wrapper;
	
	// Peliobjekti, jota seurataan
	private GameObject parentObject;
	
	/**
	 * Alustaa luokan muuttujat.
	 * 
	 * @param _speed	  Efektin nopeus (otetaan vastaan l�hinn� vain siksi, ett� efektit ovat GameObjecteja)
	 * @param _effectType Efektin tyyppi
	 */
	public EffectObject(int _speed, byte _effectType)
	{
		super(_speed);
		
		effectType = _effectType;
    
        // Haetaan animaatioiden pituudet
        animationLength = new int[GLRenderer.AMOUNT_OF_EFFECT_ANIMATIONS];
        
        if (GLRenderer.effectAnimations[_effectType] != null) {
            animationLength[_effectType] = GLRenderer.effectAnimations[_effectType].length;
        }
		
		wrapper = Wrapper.getInstance();
		
		listId = wrapper.addToList(this, Wrapper.CLASS_TYPE_EFFECT, 0);
	}
	
	/**
	 * Aktivoi peliobjektin efektin.
	 * 
	 * @param _object Peliobjekti
	 */
	public void activate(GameObject _object)
	{
		parentObject = _object;
		x            = _object.x;
		y            = _object.y;
		
		setActive();
		
		setAction(effectType, 1, 1, GfxObject.ACTION_DESTROYED, 0, 0);
		updatePosition(); // TODO: T�t� pit�� kutsua muualta, sill� activate-funktiota kutsutaan
						  // vain kerran efektin aktivoituessa. T�t� olisi parempi kutsua GameThreadista,
						  // jota suoritetaan koko ajan.
	}

	/**
	 * Aktivoi peliobjektin efektin.
	 * 
	 * @param _x Objektin X-koordinaatti
	 * @param _y Objektin Y-koordinaatti
	 */
	public void activate(float _x, float _y)
	{
		x = _x;
		y = _y;
		
		setActive();
		
		setAction(effectType, 1, 1, GfxObject.ACTION_DESTROYED, 0, 0);
	}
	
	/**
	 * P�ivitt�� efektien sijainnit
	 */
	public void updatePosition()
	{
		if (parentObject != null) {
			x = parentObject.x;
			y = parentObject.y;
		}
	}
	
	/**
     * M��ritt�� objektin aktiiviseksi.
     */
    @Override
    public final void setActive()
    {
		activated = true;
        wrapper.effectStates.set(listId, Wrapper.FULL_ACTIVITY);
    }

    /**
     * M��ritt�� objektin ep�aktiiviseksi. Sammuttaa my�s teko�lyn jos se on tarpeen.
     */
    @Override
    public final void setUnactive()
    {
    	activated = false;
        wrapper.effectStates.set(listId, Wrapper.INACTIVE);
    }
    
    /**
     * Piirt�� objektin k�yt�ss� olevan tekstuurin tai animaation ruudulle.
     * 
     * @param _gl OpenGL-konteksti
     */
	@Override
	public void draw(GL10 _gl)
	{
		// Tarkistaa onko animaatio p��ll� ja kutsuu oikeaa animaatiota tai tekstuuria
        if (usedAnimation >= 0){
            GLRenderer.effectAnimations[usedAnimation].draw(_gl, x, y, 90, currentFrame);
        }
	}

    /**
     * K�sittelee jonkin toiminnon p��ttymisen. Kutsutaan animaation loputtua, mik�li
     * <i>actionActivated</i> on TRUE.<br /><br />
     * 
     * K�ytet��n seuraavasti:<br />
     * <ul>
     *   <li>1. Objekti kutsuu funktiota <b>setAction</b>, jolle annetaan parametreina haluttu animaatio,
     *     animaation toistokerrat, animaation nopeus, toiminnon tunnus (vakiot <b>GfxObject</b>issa).
     *     Toiminnon tunnus tallennetaan <i>actionId</i>-muuttujaan.
     *     		<ul><li>-> Lis�ksi voi antaa my�s jonkin animaation ruudun j�rjestysnumeron (alkaen 0:sta)
     *     		   ja ajan, joka siin� ruudussa on tarkoitus odottaa.</li></ul></li>
     *  <li>2. <b>GfxObject</b>in <b>setAction</b>-funktio kutsuu startAnimation-funktiota (sis�lt�� my�s
     *     <b>GfxObject</b>issa), joka k�ynnist�� animaation asettamalla <i>usedAnimation</i>-muuttujan arvoksi
     *     kohdassa 1 annetun animaation tunnuksen.</li>
     *  <li>3. <b>GLRenderer</b> p�ivitt�� animaatiota kutsumalla <b>GfxObject</b>in <b>update</b>-funktiota.</li>
     *  <li>4. Kun animaatio on loppunut, kutsuu <b>update</b>-funktio koko ketjun aloittaneen objektin
     *     <b>triggerEndOfAction</b>-funktiota (funktio on abstrakti, joten alaluokat luovat siit� aina
     *     oman toteutuksensa).</li>
     *  <li>5. <b>triggerEndOfAction</b>-funktio tulkitsee <i>actionId</i>-muuttujan arvoa, johon toiminnon tunnus
     *     tallennettiin, ja toimii sen mukaisesti.</li>
     * </ul>
     * 
     * Funktiota k�ytet��n esimerkiksi objektin tuhoutuessa, jolloin se voi asettaa itsens�
     * "puoliaktiiviseen" tilaan (esimerkiksi 2, eli ONLY_ANIMATION) ja k�ynnist�� yll� esitetyn
     * tapahtumaketjun. Objekti tuhoutuu asettumalla tilaan 0 (INACTIVE) vasta ketjun p��tytty�.
     * Tuhoutuminen toteutettaisiin triggerEndOfAction-funktion sis�ll�.
     * 
     * Toimintojen vakiot l�ytyv�t GfxObject-luokan alusta.
     */
	@Override
	protected void triggerEndOfAction() 
	{
		if (actionId == GfxObject.ACTION_DESTROYED) {
			setUnactive();
		}
	}
}
