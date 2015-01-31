package org.rapla.plugin.freetime.server;

import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.plugin.freetime.FreetimeServiceRemote;
import org.rapla.server.ServerServiceContainer;


public class FreetimeServerPlugin  implements PluginDescriptor<ServerServiceContainer> {

 
        public void provideServices(ServerServiceContainer container, Configuration config) throws RaplaContextException {
            container.addResourceFile(FreetimePlugin.RESOURCE_FILE);
            if (!config.getAttributeAsBoolean("enabled", FreetimePlugin.ENABLE_BY_DEFAULT)) {
                return;
            }
            container.addRemoteMethodFactory(FreetimeServiceRemote.class, FreetimeService.class, config);
            container.addContainerProvidedComponent(FreetimeService.class, FreetimeService.class,config);
        }

}
