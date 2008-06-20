/*
 * Created on Jun 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.connection.jini;

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.HashMap;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.Network;

/**
 * @author b1529404
 */
public abstract class JiniConnector implements IServiceConnector {
   private final HashMap<Object, Exporter> exports = new HashMap<Object, Exporter>();

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#export(java.lang.Object)
    */
   @Override
   public Object export(Object callback) throws Exception {
      Exporter exporter = createExporter();
      exports.put(callback, exporter);
      return exporter.export((Remote) callback);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#unexport(java.lang.Object)
    */
   @Override
   public void unexport(Object callback) throws Exception {
      Exporter exporter = exports.remove(callback);
      if (exporter != null) {
         exporter.unexport(false);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#stop()
    */
   @Override
   public void stop() throws Exception {
      for (Exporter exporter : exports.values()) {
         exporter.unexport(false);
      }
      exports.clear();
   }

   private Exporter createExporter() throws UnknownHostException {
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
            new BasicILFactory(null, null, Activator.getDefault().getExportClassLoader()), false, false);
   }
}
