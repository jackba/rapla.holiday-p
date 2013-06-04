package org.rapla.plugin.freetime;

import java.awt.Color;

import org.rapla.client.ClientServiceContainer;
import org.rapla.client.RaplaClientExtensionPoints;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.TypedComponentRole;
import org.rapla.plugin.freetime.client.FreetimeAdminOptions;
import org.rapla.plugin.freetime.client.FreetimeHighlightRenderer;
import org.rapla.plugin.freetime.client.FreetimeReservationSaveCheck;


public class FreetimePlugin implements PluginDescriptor<ClientServiceContainer>
{
    public static boolean ENABLE_BY_DEFAULT = false;
    public static final TypedComponentRole<I18nBundle> RESOURCE_FILE = new TypedComponentRole<I18nBundle>(FreetimePlugin.class.getPackage().getName() + ".FreetimeResources");

    public static final Color DEFAULT_BACKGROUND_COLOR = new Color(0x18, 0x74, 0xCD);
    public static final Color DEFAULT_FOREGROUND_COLOR = Color.white;

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

}
