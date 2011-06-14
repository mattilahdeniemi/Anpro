package fi.tamk.anpro;

import java.lang.Math;

import javax.microedition.khronos.opengles.GL10;

public class ProjectileLaser extends GameObject {
	/** Vakioita ammuksen tietoja varten */
	// Efektit
	public static final int MULTIPLY_ON_TIMER = 1;
	public static final int MULTIPLY_ON_TOUCH = 2;
	public static final int EXPLODE_ON_TIMER  = 3;
	public static final int EXPLODE_ON_TOUCH  = 4;
	public static final int DAMAGE_ON_TOUCH   = 5;
	
	// Aseiden tiedot
	public int damageOnTouch   = 2;
	public int damageOnExplode = 0;
	
	public int damageType = DAMAGE_ON_TOUCH; // EXPLODE_ON_TIMER, EXPLODE_ON_TOUCH tai DAMAGE_ON_TOUCH
	
	public int armorPiercing = 0;
	
	public boolean causePassiveDamage = false;
	public int     damageOnRadius     = 0;
	public int     damageRadius       = 0; // Passiiviselle AoE-vahingolle
	
	public int     explodeTime  = 0;
	public long    startTime    = 0;
	public long    currentTime  = 0;
	
	// Wrapper
	private Wrapper wrapper;
	
	// Kohteen tiedot
	private int targetX;
	private int targetY;
	
	public boolean active = false;
	
	int listId;

	/*
	 * Rakentaja
	 */
	public ProjectileLaser() {
		super();
		
		wrapper = Wrapper.getInstance();
        
        listId = wrapper.addToList(this);
	}

	/*
	 * Aktivoidaan ammus
	 */
	public void setActive() {
		wrapper.projectileLaserStates.set(listId, 1);
		active = true;
	}

	/*
	 * Poistetaan vihollinen k�yt�st�
	 */
	public void setUnactive() {
		wrapper.projectileLaserStates.set(listId, 0);
		active = false;
	}
	
	/*
	 * Aktivoidaan ammus
	 */
	public void activate(int _xTouchPosition, int _yTouchPosition) {
		// Tarkistetaan ajastus
		if (explodeTime > 0) {
			startTime = android.os.SystemClock.uptimeMillis();
		}
		
		// Asetetaan aloituspiste
		x = wrapper.player.x;
		y = wrapper.player.y;
		
		// Tallennetaan kohteen koordinaatit
		targetX = _xTouchPosition;
		targetY = _yTouchPosition;

		// Valitaan suunta
		double xDiff = Math.abs((double)(x - targetX));
		double yDiff = Math.abs((double)(y - targetY));
		
		if (x < targetX) {
			if (y < targetY) {
				direction = (int) ((Math.atan(yDiff/xDiff)*180)/Math.PI);
			}
			else if (y > targetY) {
				direction = (int) (360 - (Math.atan(yDiff/xDiff)*180)/Math.PI);
			}
			else {
				direction = 0;
			}
		}
		else if (x > targetX) {
			if (y > targetY) {
				direction = (int) (180 + (Math.atan(yDiff/xDiff)*180)/Math.PI);
			}
			else if (y < targetY) {
				direction = (int) (180 - (Math.atan(yDiff/xDiff)*180)/Math.PI);
			}
			else {
				direction = 180;
			}
		}
		else {
			if (y > targetY) {
				direction = 270;
			}
			else {
				direction = 90;
			}
		}
		
		// Aktivoidaan ammus
		wrapper.projectileLaserStates.set(listId, 1);
		active = true;
	}
	
	/*
	 * K�sitell��n ammuksen teko�ly.
	 */
	public void handleAi() {
		// Tarkistetaan osumatyyppi ja et�isyydet ja kutsutaan osumatarkistuksia tarvittaessa
		for (int i = wrapper.enemies.size()-1; i >= 0; --i) {
			
			double distance = Math.sqrt(Math.pow(x - wrapper.enemies.get(i).x,2) + Math.pow(y - wrapper.enemies.get(i).y, 2));
			
			if (distance - wrapper.enemies.get(i).collisionRadius - collisionRadius <= 0) {
				// Osuma ja r�j�hdys
				if (damageType == ProjectileLaser.DAMAGE_ON_TOUCH) {
					wrapper.enemies.get(i).triggerCollision(GameObject.COLLISION_WITH_PROJECTILE, damageOnTouch, armorPiercing);
				}
				else if (damageType == ProjectileLaser.EXPLODE_ON_TOUCH) {
					causeExplosion();
				}

				setUnactive();
				break;
			}
			
			// Passiivinen vahinko
			if (distance - wrapper.enemies.get(i).collisionRadius - damageRadius <= 0) {
				wrapper.enemies.get(i).health -= (damageOnRadius * (1 - 0.15 * wrapper.enemies.get(i).defence));
			}
		}
		
		// Tarkistetaan r�j�hdykset (ajastus)
		if (explodeTime > 0) {
			currentTime = android.os.SystemClock.uptimeMillis();
			
			if (currentTime - startTime >= explodeTime) {
				causeExplosion();
				setUnactive();
			}
		}
		
		// Tarkistetaan suunta ja k��ntyminen
		//...
	}
	
	/*
	 * Kutsutaan triggerImpact-funktiota muista objekteista, jotka ovat r�j�hdyksen vaikutusalueella.
	 */
	public void causeExplosion() {
		// Tarkistetaan et�isyydet
		// Kutsutaan osumatarkistuksia tarvittaessa
		for (int i = wrapper.enemies.size(); i >= 0; --i) {
			int distance = (int) Math.sqrt(((int)(x - wrapper.enemies.get(i).x))^2 + ((int)(y - wrapper.enemies.get(i).y))^2);
			if (distance - wrapper.enemies.get(i).collisionRadius - collisionRadius <= 0) {
				// Osuma ja r�j�hdys
				wrapper.enemies.get(i).triggerImpact(damageOnTouch);
			}
		}
	}
	
	/*
	 * K�sitell��n r�j�hdykset
	 */
	public void triggerImpact(int _damage) {
		// R�j�hdykset eiv�t vaikuta t�h�n ammukseen
	}

	/*
	 * K�sitell��n osumat
	 */
	public void triggerCollision(int _eventType, int _damage, int _armorPiercing) {
		// Osumat eiv�t vaikuta t�h�n ammukseen
	}

	public void draw(GL10 _gl) {
        if (usedAnimation >= 0){
        	GLRenderer.projectileAnimations.get(usedAnimation).draw(_gl, x, y, direction, currentFrame);
            //animations.get(usedAnimation).draw(_gl, x, y, direction, currentFrame);
        }
        else{
        	GLRenderer.projectileTextures.get(usedTexture).draw(_gl, x, y, direction);
            //textures.get(usedTexture).draw(_gl, x, y, direction);
        }
	}
}
