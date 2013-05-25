package org.rapla.plugin.freetime.server;

import org.rapla.components.xmlbundle.impl.I18nBundleImpl;
import org.rapla.framework.Configuration;
import org.rapla.framework.PluginDescriptor;
import org.rapla.framework.RaplaContextException;
import org.rapla.plugin.freetime.FreetimePlugin;
import org.rapla.server.RaplaServerExtensionPoints;
import org.rapla.server.ServerExtension;
import org.rapla.server.ServerServiceContainer;

/**
 * Created with IntelliJ IDEA.
 * User: rku
 * Date: 25.05.13
 * Time: 16:19
 * To change this template use File | Settings | File Templates.
 */
public class FreetimeServerPlugin  implements PluginDescriptor<ServerServiceContainer>, ServerExtension {

        /* (non-Javadoc)
      * @see org.rapla.framework.PluginDescriptor#provideServices(org.rapla.framework.Container, org.apache.avalon.framework.configuration.Configuration)
      */
        public void provideServices(ServerServiceContainer container, Configuration config) throws RaplaContextException {
            container.addContainerProvidedComponent(FreetimePlugin.RESOURCE_FILE, I18nBundleImpl.class, I18nBundleImpl.createConfig(FreetimePlugin.RESOURCE_FILE.getId()));
            if (!config.getAttributeAsBoolean("enabled", FreetimePlugin.ENABLE_BY_DEFAULT)) {
                return;
            }
            FreetimeServerPlugin.loadConfigParameters(config);
            container.addContainerProvidedComponent(RaplaServerExtensionPoints.SERVER_EXTENSION, FreetimeServerPlugin.class);
            container.addRemoteMethodFactory(FreetimeServiceRemote.class, FreetimeServiceRemoteObject.class);
        }

        private static void loadConfigParameters(Configuration config) {
            //To change body of created methods use File | Settings | File Templates.
        }
}
