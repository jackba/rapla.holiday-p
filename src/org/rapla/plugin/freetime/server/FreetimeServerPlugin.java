package org.rapla.plugin.freetime.server;

import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.server.RaplaServerExtensionPoints;
import org.rapla.server.ServerServiceContainer;


public class FreetimeServerPlugin  implements PluginDescriptor<ServerServiceContainer> {

        /* (non-Javadoc)
      * @see org.rapla.framework.PluginDescriptor#provideServices(org.rapla.framework.Container, org.apache.avalon.framework.configuration.Configuration)
      */
        public void provideServices(ServerServiceContainer container, Configuration config) throws RaplaContextException {
            container.addContainerProvidedComponent(FreetimePlugin.RESOURCE_FILE, I18nBundleImpl.class, I18nBundleImpl.createConfig(FreetimePlugin.RESOURCE_FILE.getId()));
            if (!config.getAttributeAsBoolean("enabled", FreetimePlugin.ENABLE_BY_DEFAULT)) {
                return;
            }
            FreetimeServerPlugin.loadConfigParameters(config);
            container.addContainerProvidedComponent(RaplaServerExtensionPoints.SERVER_EXTENSION, FreetimeCache.class);
            container.addRemoteMethodFactory(FreetimeServiceRemote.class, FreetimeServiceRemoteObject.class);
        }

        private static void loadConfigParameters(Configuration config) {

        }
}
