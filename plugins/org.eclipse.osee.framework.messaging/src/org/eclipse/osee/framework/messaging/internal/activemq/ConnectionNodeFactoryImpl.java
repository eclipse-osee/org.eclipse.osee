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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.ConnectionNodeFactory;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.FailoverConnectionNode;

/**
 * @author Roberto E. Escobar
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
   public synchronized ConnectionNode create(NodeInfo nodeInfo) {
	  OseeExceptionListener exceptionListener = new OseeExceptionListener();
      final ConnectionNodeActiveMq node = new ConnectionNodeActiveMq(version, sourceId, nodeInfo, executor, exceptionListener);
      OseeLog.log(Activator.class, Level.FINEST, "Going to start a connection node.");
      try {
         node.start();
      } catch (OseeCoreException ex) {
         OseeLog.log(ConnectionNodeFactoryImpl.class, Level.SEVERE, ex);
      }
      OseeLog.log(Activator.class, Level.FINE, "Started a connection node.");
      return new FailoverConnectionNode(node, scheduledExecutor, exceptionListener);
   }

}
