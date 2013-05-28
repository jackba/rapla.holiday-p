package org.rapla.plugin.freetime;

import java.awt.Color;

import org.rapla.client.ClientServiceContainer;
import org.rapla.client.RaplaClientExtensionPoints;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.DefaultConfiguration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.TypedComponentRole;
import org.rapla.plugin.freetime.client.FreetimeAdminOptions;
import org.rapla.plugin.freetime.client.FreetimeHighlightRenderer;
import org.rapla.plugin.freetime.client.FreetimeReservationSaveCheck;


public class FreetimePlugin implements PluginDescriptor<ClientServiceContainer>
{
    private static final String FOREGROUND_COLOR_KEY = "foreground";
    private static final String BACKGROUND_COLOR_KEY = "background";
    public static boolean ENABLE_BY_DEFAULT = false;
    public static final TypedComponentRole<I18nBundle> RESOURCE_FILE = new TypedComponentRole<I18nBundle>(FreetimePlugin.class.getPackage().getName() + ".FreetimeResources");

    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0x18, 0x74, 0xCD);
    public static Color BACKGROUND_COLOR = DEFAULT_BACKGROUND_COLOR;
    public static final Color DEFAULT_FOREGROUND_COLOR = Color.white;
    public static Color FOREGROUND_COLOR = DEFAULT_FOREGROUND_COLOR;

    public static final String DEFAULT_FREETIME_RESOURCE = "freetime";

    public void provideServices(ClientServiceContainer container, Configuration config) 
    {
        container.addContainerProvidedComponent(RaplaClientExtensionPoints.PLUGIN_OPTION_PANEL_EXTENSION, FreetimeAdminOptions.class);
        container.addContainerProvidedComponent(RESOURCE_FILE, I18nBundleImpl.class, I18nBundleImpl.createConfig(RESOURCE_FILE.getId()));

        if (!config.getAttributeAsBoolean("enabled", ENABLE_BY_DEFAULT))
            return;

        container.addContainerProvidedComponent(RaplaClientExtensionPoints.RESERVATION_SAVE_CHECK, FreetimeReservationSaveCheck.class);
        container.addContainerProvidedComponent(DateRenderer.class, FreetimeHighlightRenderer.class );
    }

    public static void loadConfigParameters(Configuration config) {
        //Integer.toHexString( aColor.getRGB() )
        FOREGROUND_COLOR = new Color((config.getChild(FOREGROUND_COLOR_KEY).getValueAsInteger(DEFAULT_FOREGROUND_COLOR.getRGB())));
        BACKGROUND_COLOR = new Color((config.getChild(BACKGROUND_COLOR_KEY).getValueAsInteger(DEFAULT_BACKGROUND_COLOR.getRGB())));
     }

    public static void storeParametersToConfig(DefaultConfiguration newConfig)  {
        newConfig.getMutableChild(FOREGROUND_COLOR_KEY, true).setValue(FOREGROUND_COLOR.getRGB());
        newConfig.getMutableChild(BACKGROUND_COLOR_KEY, true).setValue(BACKGROUND_COLOR.getRGB());
    }
}
