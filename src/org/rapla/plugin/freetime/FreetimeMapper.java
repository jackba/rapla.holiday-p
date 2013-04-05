package org.rapla.plugin.freetime;

import java.awt.Color;

import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.framework.RaplaContext;
import org.rapla.framework.RaplaContextException;

public class FreetimeMapper {
	
	private Color[] colorArrayBackground = {new Color(0x18, 0x74, 0xCD),new Color(0xfa, 0x96, 0x96),new Color(0x7C, 0xCD, 0x7C),new Color(0xEE, 0xEE, 0x00), new Color(0x69, 0x59, 0xCD), new Color(0xD3, 0xD3, 0xD3), new Color(0xFF, 0x7F, 0x00), new Color(0x8B, 0x47, 0x26),new Color(0xFF, 0x82, 0xAB), new Color(0xFF, 0xE7, 0xBA)};
	private Color[] colorArrayText = {Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK};
	private String[] colorNames = {"colorBlue","colorRed","colorGreen","colorYellow","colorPurple","colorGrey","colorOrange","colorBrown","colorPink","colorBeige"};
	public FreetimeMapper(){

	}
	/**
	 * Mappt den in den preferences gespeicherten Integer Wert auf eine Color
	 * @param configValue
	 * @return
	 */
	public Color getFreetimeColorBackground(int configValue){
		return colorArrayBackground[configValue];
	}
	
	/**
	 * Gibt zu der entsprechenden Background Color die Foreground Color
	 * @param configValue
	 * @return
	 */
	public Color getForegroundColor(int configValue){
		return colorArrayText[configValue];
	}
	
	/**
	 * Gibt den String für die Ausgabe in der CalenderOptionsAnsicht aus
	 * @param configValue
	 * @return
	 */
	public String getFreetimeColorString(int configValue){
		return colorNames[configValue];
	}
	/**
	 * Gibt die Namen für die Farben zurück
	 * @param sm
	 * @return
	 * @throws RaplaContextException
	 */
	public String[] getColorNames(RaplaContext sm) throws RaplaContextException{
		I18nBundle i18n = (I18nBundle) sm.lookup(I18nBundle.ROLE + "/org.rapla.RaplaResources");
		String[] temp = new String[colorNames.length];
		for(int i=0;i<temp.length;i++){
			temp[i] = i18n.getString(colorNames[i]);
		}
		
		return temp;
	}
}
