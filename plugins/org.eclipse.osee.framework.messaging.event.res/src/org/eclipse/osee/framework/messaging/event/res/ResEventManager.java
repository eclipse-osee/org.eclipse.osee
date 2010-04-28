/*
 * Created on Mar 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res;

import java.rmi.RemoteException;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;

/**
 * @author Donald G. Dunne
 */
public class ResEventManager implements OseeMessagingStatusCallback {

   private static ResEventManager instance;
   private ConnectionNode connectionNode;
   private ResMessagingTracker resMessagingTracker;
   private IFrameworkEventListener frameworkEventListener;

   private ResEventManager() {
   }

   public static ResEventManager getInstance() {
      if (instance == null) {
         instance = new ResEventManager();
      }
      return instance;
   }

   public static void dispose() {
      if (instance != null) {
         instance.stopListeningForRemoteCoverageEvents();
         instance.resMessagingTracker.close();
         instance = null;
      }
   }

   private void startListeningForRemoteCoverageEvents() {
      if (resMessagingTracker == null) {
         System.out.println("Registering Client for Remote Events\n");
         resMessagingTracker = new ResMessagingTracker();
         resMessagingTracker.open(true);
         addingRemoteEventService(resMessagingTracker.getConnectionNode());
      }
   }

   private void stopListeningForRemoteCoverageEvents() {
      System.out.println("De-Registering Client for Remote Events\n");
      resMessagingTracker.close();
      resMessagingTracker = null;
   }

   public boolean isConnected() {
      return resMessagingTracker != null;
   }

   public void addingRemoteEventService(ConnectionNode connectionNode) {
      this.connectionNode = connectionNode;
      connectionNode.subscribe(ResMessages.RemoteTransactionEvent1, new RemoteTransactionEvent1Listener(), instance);
   }

   public void kick(RemoteEvent remoteEvent) throws Exception {
      if (remoteEvent instanceof RemoteTransactionEvent1) {
         sendRemoteEvent(ResMessages.RemoteTransactionEvent1, remoteEvent);
      } else {
         System.out.println("ResEventManager: Unhandled remote event " + remoteEvent);
      }
   }

   public void sendRemoteEvent(ResMessages resMessage, RemoteEvent remoteEvent) throws Exception {
      System.out.println(String.format(getClass().getSimpleName() + " - sending [%s]",
            remoteEvent.getClass().getSimpleName()));
      if (connectionNode != null) {
         connectionNode.send(resMessage, remoteEvent, instance);
      }
   }

   public void start(IFrameworkEventListener frameworkEventListener) throws OseeCoreException {
      this.frameworkEventListener = frameworkEventListener;
      startListeningForRemoteCoverageEvents();
   }

   public void stop() throws OseeCoreException {
      this.frameworkEventListener = null;
      stopListeningForRemoteCoverageEvents();
   }

   @Override
   public void fail(Throwable th) {
      System.err.println(getClass().getSimpleName() + " - fail: " + th.getLocalizedMessage());
      th.printStackTrace();
   }

   @Override
   public void success() {
   }

   public class RemoteTransactionEvent1Listener extends OseeMessagingListener {

      public RemoteTransactionEvent1Listener() {
         super(RemoteTransactionEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         RemoteTransactionEvent1 remoteTransactionEvent1 = (RemoteTransactionEvent1) message;
         System.err.println(String.format(getClass().getSimpleName() + " - received [%s]",
               message.getClass().getSimpleName()));
         try {
            frameworkEventListener.onEvent(remoteTransactionEvent1);
         } catch (RemoteException ex) {
            System.err.println(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
         }
      }
   }

}
