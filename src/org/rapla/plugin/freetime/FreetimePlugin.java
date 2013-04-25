package org.rapla.plugin.freetime;

import org.rapla.client.ClientService;
import org.rapla.client.ClientServiceContainer;
import org.rapla.client.RaplaClientExtensionPoints;
import org.rapla.components.calendar.DateRenderer;
import org.rapla.components.xmlbundle.I18nBundle;
import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.Container;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.TypedComponentRole;


public class FreetimePlugin implements PluginDescriptor<ClientServiceContainer>
{
    static boolean ENABLE_BY_DEFAULT = false;
    public static final TypedComponentRole<I18nBundle> RESOURCE_FILE = new TypedComponentRole<I18nBundle>(FreetimePlugin.class.getPackage().getName() + ".FreetimeResources");
    
    public void provideServices(ClientServiceContainer container, Configuration config) 
    {
        if (!config.getAttributeAsBoolean("enabled", ENABLE_BY_DEFAULT))
            return;

        container.addContainerProvidedComponent(RESOURCE_FILE, I18nBundleImpl.class, I18nBundleImpl.createConfig(RESOURCE_FILE.getId()));
        container.addContainerProvidedComponent(RaplaClientExtensionPoints.RESERVATION_SAVE_CHECK, FreetimeReservationSaveCheck.class);
        container.addContainerProvidedComponent( DateRenderer.class, FreetimeHighlightRenderer.class );
    }
    
    public String toString() {
        return "Freetime";
    }

}
