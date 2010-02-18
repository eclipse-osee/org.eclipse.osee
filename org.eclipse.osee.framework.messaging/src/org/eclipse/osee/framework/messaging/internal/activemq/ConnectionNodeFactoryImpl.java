/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.activemq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import javax.jms.JMSException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.future.NodeInfo;
import org.eclipse.osee.framework.messaging.internal.FailoverConnectionNode;

/**
 * @author b1122182
 */
public class ConnectionNodeFactoryImpl implements ConnectionNodeFactory {

   private final ExecutorService executor;
   private final ScheduledExecutorService scheduledExecutor;
   private final String version;
   private final String sourceId;

   public ConnectionNodeFactoryImpl(String version, String sourceId, ExecutorService executor) {
      this.version = version;
      this.sourceId = sourceId;
      this.executor = executor;
      this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
   }

   @Override
   public ConnectionNode create(NodeInfo nodeInfo) throws OseeCoreException {
      try {
         ConnectionNodeActiveMq node = new ConnectionNodeActiveMq(version, sourceId, nodeInfo, executor);
         try{
            node.start();
         } catch (OseeCoreException ex){
            OseeLog.log(ConnectionNodeFactoryImpl.class, Level.SEVERE, ex);  
         }
         return new FailoverConnectionNode(node, scheduledExecutor);
      } catch (JMSException ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
