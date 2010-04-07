/*
 * Created on Mar 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.msgs.EditorSave;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionListener;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.messages.Synch;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class CoverageMessageListener extends OseeMessagingListener implements OseeMessagingStatusCallback, ConnectionListener {

   public CoverageMessageListener() {
      super(EditorSave.class);
   }

   private ConnectionNode connectionNode;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.OseeMessagingListener#process(java.lang.Object, java.util.Map, org.eclipse.osee.framework.messaging.ReplyConnection)
    */
   @Override
   public void process(final Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
         @Override
         public void run() {
            EditorSave editorSave = (EditorSave) message;
            System.out.println("Editor Save " + editorSave.getName());
         }
      });

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback#fail(java.lang.Throwable)
    */
   @Override
   public void fail(Throwable th) {
      OseeLog.log(CoverageMessageListener.class, Level.SEVERE, "notified of messaging failure", th);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback#success()
    */
   @Override
   public void success() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.ConnectionListener#connected(org.eclipse.osee.framework.messaging.ConnectionNode)
    */
   @Override
   public void connected(ConnectionNode node) {
      Synch synch = new Synch();
      synch.setTopic(CoverageMessages.EditorSave.getGuid());
      try {
         node.send(BaseMessages.Synch, synch, this);
      } catch (OseeCoreException ex) {
         OseeLog.log(CoverageMessageListener.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.ConnectionListener#notConnected(org.eclipse.osee.framework.messaging.ConnectionNode)
    */
   @Override
   public void notConnected(ConnectionNode node) {
   }

}
