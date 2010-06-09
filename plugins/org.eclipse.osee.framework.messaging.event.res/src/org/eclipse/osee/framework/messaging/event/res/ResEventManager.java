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
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAccessControlEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePersistEvent1;
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
      connectionNode.subscribe(ResMessages.RemotePersistEvent1, new RemotePersistEvent1Listener(), instance);
      connectionNode.subscribe(ResMessages.RemoteBranchEvent1, new RemoteBranchEvent1Listener(), instance);
      connectionNode.subscribe(ResMessages.RemoteBroadcastEvent1, new RemoteBroadcastEvent1Listener(), instance);
      connectionNode.subscribe(ResMessages.RemoteAccessControlEvent1, new RemoteAccessControlEvent1Listener(), instance);
   }

   public void kick(RemoteEvent remoteEvent) throws Exception {
      if (remoteEvent instanceof RemotePersistEvent1) {
         sendRemoteEvent(ResMessages.RemotePersistEvent1, remoteEvent);
      } else if (remoteEvent instanceof RemoteTransactionEvent1) {
         sendRemoteEvent(ResMessages.RemoteTransactionEvent1, remoteEvent);
      } else if (remoteEvent instanceof RemoteBranchEvent1) {
         sendRemoteEvent(ResMessages.RemoteBranchEvent1, remoteEvent);
      } else if (remoteEvent instanceof RemoteBroadcastEvent1) {
         sendRemoteEvent(ResMessages.RemoteBroadcastEvent1, remoteEvent);
      } else if (remoteEvent instanceof RemoteAccessControlEvent1) {
         sendRemoteEvent(ResMessages.RemoteAccessControlEvent1, remoteEvent);
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

   public class RemoteAccessControlEvent1Listener extends OseeMessagingListener {

      public RemoteAccessControlEvent1Listener() {
         super(RemoteAccessControlEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         RemoteAccessControlEvent1 remoteTransactionEvent1 = (RemoteAccessControlEvent1) message;
         System.err.println(String.format(getClass().getSimpleName() + " - received [%s]",
               message.getClass().getSimpleName()));
         try {
            frameworkEventListener.onEvent(remoteTransactionEvent1);
         } catch (RemoteException ex) {
            System.err.println(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
         }
      }
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

   public class RemotePersistEvent1Listener extends OseeMessagingListener {

      public RemotePersistEvent1Listener() {
         super(RemotePersistEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         RemotePersistEvent1 remotePersistEvent1 = (RemotePersistEvent1) message;
         System.err.println(String.format(getClass().getSimpleName() + " - received [%s]",
               message.getClass().getSimpleName()));
         try {
            frameworkEventListener.onEvent(remotePersistEvent1);
         } catch (RemoteException ex) {
            System.err.println(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
         }
      }
   }

   public class RemoteBranchEvent1Listener extends OseeMessagingListener {

      public RemoteBranchEvent1Listener() {
         super(RemoteBranchEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         RemoteBranchEvent1 remoteBranchEvent1 = (RemoteBranchEvent1) message;
         System.err.println(String.format(getClass().getSimpleName() + " - received [%s]",
               message.getClass().getSimpleName()));
         try {
            frameworkEventListener.onEvent(remoteBranchEvent1);
         } catch (RemoteException ex) {
            System.err.println(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
         }
      }
   }

   public class RemoteBroadcastEvent1Listener extends OseeMessagingListener {

      public RemoteBroadcastEvent1Listener() {
         super(RemoteBroadcastEvent1.class);
      }

      @Override
      public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         RemoteBroadcastEvent1 remoteBroadcastEvent1 = (RemoteBroadcastEvent1) message;
         System.err.println(String.format(getClass().getSimpleName() + " - received [%s]",
               message.getClass().getSimpleName()));
         try {
            frameworkEventListener.onEvent(remoteBroadcastEvent1);
         } catch (RemoteException ex) {
            System.err.println(getClass().getSimpleName() + " - process: " + ex.getLocalizedMessage());
         }
      }
   }

}
