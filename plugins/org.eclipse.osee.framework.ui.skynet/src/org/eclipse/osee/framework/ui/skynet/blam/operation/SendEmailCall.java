/*
 * Created on Jun 4, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.concurrent.Callable;
import javax.mail.MessagingException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.OseeEmail;

public final class SendEmailCall implements Callable<String> {
   private final Artifact user;
   private final OseeEmail emailMessage;
   private final String emailAddress;

   SendEmailCall(Artifact user, OseeEmail emailMessage, String emailAddress) {
      this.user = user;
      this.emailMessage = emailMessage;
      this.emailAddress = emailAddress;
   }

   @Override
   public String call() throws Exception {
      try {
         emailMessage.sendLocalThread();
      } catch (MessagingException ex) {
         return String.format("An exception occured with sending the email for address \"%s\" for user %s.  %s",
                  emailAddress, user.getName(), ex);
      }
      return null;
   }
}