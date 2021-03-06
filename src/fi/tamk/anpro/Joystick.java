package fi.tamk.anpro;

/**
 * Sisältää käyttöliittymän joystickin toiminnallisuudet.
 */
public class Joystick extends GuiObject
{
	public static int 	  joystickX;
	public static int 	  joystickY;
	public static boolean joystickInUse;
	public static boolean joystickDown;
	
    /**
     * Alustaa luokan muuttujat.
     * 
     * @param int Objektin X-koordinaatti
     * @param int Objektin Y-koordinaatti
     */
    public Joystick(int _x, int _y)
    {
        super(_x, _y);
        
        // Määritetään tila
        joystickX     = _x;
        joystickY     = _y;
        joystickDown  = false;
        joystickInUse = false;
        
        // Määritetään käytettävä tekstuuri
        usedTexture = GLRenderer.TEXTURE_JOYSTICK;
    }

	/* =======================================================
	 * Uudet funktiot
	 * ======================================================= */
    /**
     * Määrittää pelaajan kulkusuunnan ollessaan käytössä
     * 
     * @param _xClickOffset Painalluksen X-koordinaatti
     * @param _yClickOffset Painalluksen Y-koordinaatti
     * @param _getX			Painalluksen alkuperäinen X-koordinaatti
     * @param _getY			Painalluksen alkuperäinen Y-koordinaatti
     * @param _wrapper		Wrapperi
     * 
     * @return Totuus
     */
    public static boolean useJoystick(int _xClickOffset, int _yClickOffset, int _getX, int _getY,
    						          Wrapper _wrapper)
    {
    	_xClickOffset = _getX - Options.screenWidth / 2;
        _yClickOffset = Options.screenHeight / 2 - _getY;
    	
        // Määritetään sormen ja joystickin välinen kulma
        int angle = Utility.getAngle(joystickX, joystickY, _xClickOffset, _yClickOffset);
        
        // Muutetaan pelaajan suunta ja nopeus
        _wrapper.player.movementTargetDirection = angle;
        _wrapper.player.movementAcceleration    = 0;
        _wrapper.player.setMovementSpeed(1.0f);
        _wrapper.player.setMovementDelay(1.0f);
        
        return true;
    }
}
