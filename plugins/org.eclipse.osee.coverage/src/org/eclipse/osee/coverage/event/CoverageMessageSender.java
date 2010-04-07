/*
 * Created on Mar 12, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.coverage.msgs.EditorSave;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.messages.Synch;

/**
 * @author Donald G. Dunne
 */
public class CoverageMessageSender implements OseeMessagingStatusCallback {

   private final ConnectionNode connection;
   private final SynchListener listener;

   public CoverageMessageSender(ConnectionNode connection) {
      this.connection = connection;
      this.listener = new SynchListener();
      connection.subscribe(BaseMessages.Synch, listener, this);
   }

   @Override
   public void fail(Throwable th) {
      OseeLog.log(CoverageMessageSender.class, Level.SEVERE, th);
   }

   @Override
   public void success() {
   }

   private class SynchListener extends OseeMessagingListener {

      @Override
      public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
         Synch synch = (Synch) message;
         if (synch.getTopic().equals(CoverageMessages.EditorSave.getGuid())) {
            EditorSave editorSave = (EditorSave) message;
            System.out.println("Editor Save " + editorSave.getName());
         }
      }

      @Override
      public Class<?> getClazz() {
         return Synch.class;
      }

   }

}
