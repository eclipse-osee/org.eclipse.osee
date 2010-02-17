/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.concurrent.ExecutorService;
import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

/**
 * @author b1122182
 */
public class ConnectionNodeFactoryImpl implements ConnectionNodeFactory {

   private final ExecutorService executor;
   private final String version;
   private final String sourceId;

   public ConnectionNodeFactoryImpl(String version, String sourceId, ExecutorService executor) {
      this.version = version;
      this.sourceId = sourceId;
      this.executor = executor;
   }

   @Override
   public ConnectionNode create(NodeInfo nodeInfo) throws OseeCoreException {
      ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, nodeInfo.getUri().toASCIIString());
      try {
         Connection connection = factory.createConnection();
         connection.start();
         return new ConnectionNodeActiveMq(version, sourceId, nodeInfo, executor, connection);
      } catch (JMSException ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
