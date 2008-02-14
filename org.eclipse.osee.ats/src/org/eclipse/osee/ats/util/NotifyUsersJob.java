/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import javax.mail.Message;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ActionDebug;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.Overview.PreviewStyle;
import org.eclipse.osee.framework.jdk.core.util.AEmail;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import com.sun.org.apache.xerces.internal.impl.xpath.regex.REUtil;

/**
 * @author Donald G. Dunne
 */
public class NotifyUsersJob extends Job {
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private final StateMachineArtifact sma;
   private final SMAManager smaMgr;
   private final Set<NotifyType> types = new HashSet<NotifyType>();
   private ActionDebug debug = new ActionDebug(false, "NotifyUsersJob");
   public static enum NotifyType {
      Subscribers, Completed, Assignee, Originator
   };
   // Set to true to test emailing without actually sending email; set to false for production
   private boolean testing = false;

   String stateName;
   private final Set<User> notifyUsers = new HashSet<User>();;

   public NotifyUsersJob(StateMachineArtifact sma, NotifyType type, NotifyType... types) throws IllegalArgumentException, SQLException {
      this(sma, null, type, types);
   }

   public NotifyUsersJob(StateMachineArtifact sma, Collection<User> notifyUsers, NotifyType type, NotifyType... types) throws IllegalArgumentException, SQLException {
      super("Notifying ATS Users");
      if (testing) OSEELog.logException(AtsPlugin.class,
            "NotifyUsersJob is in testing mode; don't release in this state.", null, false);
      this.sma = sma;
      if (notifyUsers != null) this.notifyUsers.addAll(notifyUsers);
      // Never email current user
      this.notifyUsers.remove(skynetAuth.getAuthenticatedUser());
      this.types.add(type);
      if (types != null) for (NotifyType nt : types)
         this.types.add(nt);
      stateName = sma.getCurrentStateName();
      smaMgr = new SMAManager(sma);
   }

   public IStatus run(IProgressMonitor monitor) {
      try {
         if (!testing && !AtsPlugin.isAtsAlwaysEmailMe()) {
            if (!AtsPlugin.isEmailEnabled()) {
               // System.out.println("Email programatically disabled; not sending.");
               monitor.done();
               return Status.OK_STATUS;
            } else if (!AtsPlugin.isProductionDb()) {
               AtsPlugin.getLogger().log(Level.INFO, "Test DB; Not Sending Email");
               monitor.done();
               return Status.OK_STATUS;
            } else if (AtsPlugin.isAtsDisableEmail()) {
               AtsPlugin.getLogger().log(Level.INFO, "ATS Email Disabled; Not Sending");
               monitor.done();
               return Status.OK_STATUS;
            }
         }

         String html = sma.getPreviewHtml(PreviewStyle.HYPEROPEN, PreviewStyle.NO_SUBSCRIBE_OR_FAVORITE);
         if (types.contains(NotifyType.Originator)) notifyOriginator(monitor, html);
         if (types.contains(NotifyType.Assignee)) notifyAssignees(monitor, html);
         if (types.contains(NotifyType.Subscribers)) notifySubscribers(monitor, html);
         if (types.contains(NotifyType.Completed)) if ((sma instanceof TeamWorkFlowArtifact) && (smaMgr.isCompleted() || smaMgr.isCancelled())) notifyCompletion(
               monitor, html);

         monitor.done();
         return Status.OK_STATUS;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      }
   }

   private void notifyCompletion(IProgressMonitor monitor, String html) {
      monitor.subTask("Notifying of Completion/Cancellation");

      String emails = "";
      if (!smaMgr.getOriginator().isMe()) emails = smaMgr.getOriginator().getEmail();
      if (!testing && AtsPlugin.isAtsAlwaysEmailMe()) {
         AtsPlugin.getLogger().log(Level.INFO, "NotifyCompletion: Always Email Me Enabled");
         // For debug purposes, show who would have emailed if email is enabled
         if (AtsPlugin.isEmailEnabled()) debug.report("AtsAlwaysEmailMe but would have notifyCompletion => " + emails);
         emails = skynetAuth.getAuthenticatedUser().getEmail();
      }
      if (emails.equals("")) {
         if (testing) System.out.println("notifyCompletion = not sending; no emails to send to");
         return;
      }
      LogItem cancelledItem = smaMgr.getSma().getLog().getStateEvent(LogType.StateCancelled);

      String subjectStr = sma.getArtifactTypeName() + " - \"" + sma.getDescriptiveName() + "\"";
      String notifyStr = sma.getArtifactTypeName() + " " + stateName + " - " + sma.getDescriptiveName() + "<br><br>";
      String cancelledStr = "";
      if (smaMgr.isCancelled() && cancelledItem != null) cancelledStr =
            String.format(
                  sma.getArtifactTypeName() + " was cancelled from \"%s\" state on \"%s\".<br>Reason: \"%s\"<br><br>",
                  cancelledItem.getState(), cancelledItem.getDate(XDate.MMDDYYHHMM), cancelledItem.getMsg());

      AEmail emailMessage =
            new AEmail(null, skynetAuth.getAuthenticatedUser().getEmail(),
                  skynetAuth.getAuthenticatedUser().getEmail(), "ATS " + stateName + " Alert: " + subjectStr);

      try {
         emailMessage.setRecipients(Message.RecipientType.TO, emails);
         // Remove hyperlinks cause they won't work in email.

         html =
               html.replaceFirst("<body>",
                     REUtil.quoteMeta("<body>" + (smaMgr.isCancelled() ? cancelledStr : notifyStr)));
         emailMessage.addHTMLBody(html);
         if (testing)
            System.out.println("notifyCompletion = sending to " + emails);
         else
            emailMessage.send();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, "Your Email Message could not be sent.", ex, true);
      }
   }

   public void notifyOriginator(IProgressMonitor monitor, String html) {
      monitor.subTask("Notifying Originator");
      String emails = smaMgr.getOriginator().getEmail();
      if (!testing && AtsPlugin.isAtsAlwaysEmailMe()) {
         AtsPlugin.getLogger().log(Level.INFO, "Notifys: Always Email Me Enabled");
         // For debug purposes, show who would have emailed if email is enabled
         if (AtsPlugin.isEmailEnabled()) debug.report("AtsAlwaysEmailMe but would have notifyOriginator => " + emails);
         emails = skynetAuth.getAuthenticatedUser().getEmail();
      }
      // Don't send if originator is this user
      if (!AtsPlugin.isAtsAlwaysEmailMe() && emails.equals(skynetAuth.getAuthenticatedUser().getEmail())) {
         if (testing) System.out.println("notifyOriginator = not sending; originator is me");
         return;
      }
      // Don't try to send if there are no actives
      if (emails.equals("")) {
         if (testing) System.out.println("notifyOriginator = not sending; no emails to send to");
         return;
      }
      String headerStr = "You have been set as the originator of this " + sma.getArtifactTypeName() + ".<br><br>";
      AEmail emailMessage =
            new AEmail(null, skynetAuth.getAuthenticatedUser().getEmail(),
                  skynetAuth.getAuthenticatedUser().getEmail(),
                  "ATS Originator Alert: " + sma.getArtifactTypeName() + " - \"" + sma.getDescriptiveName() + "\"\n");
      try {
         emailMessage.setRecipients(Message.RecipientType.TO, emails);
         emailMessage.setHTMLBody(headerStr);
         emailMessage.addHTMLBody(html);
         if (testing)
            System.out.println("notifyOriginator = sending to " + emails);
         else
            emailMessage.send();
      } catch (Exception ex) {
         MessageDialog.openInformation(null, "Message Could Not Be Sent", "Your Email Message could not be sent.");
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   public void notifyAssignees(IProgressMonitor monitor, String html) {
      monitor.subTask("Notifying Assignees");
      String emails = "";
      try {

         if (notifyUsers != null) {
            notifyUsers.remove(skynetAuth.getAuthenticatedUser());
            if (notifyUsers.size() == 0) return;
            emails = getEmails(notifyUsers);
         } else
            emails = getActiveEmails();
         if (!testing && AtsPlugin.isAtsAlwaysEmailMe()) {
            AtsPlugin.getLogger().log(Level.INFO, "Notifys: Always Email Me Enabled");
            // For debug purposes, show who would have emailed if email is enabled
            if (AtsPlugin.isEmailEnabled()) debug.report("AtsAlwaysEmailMe but would have notifyAssignees => " + emails);
            emails = skynetAuth.getAuthenticatedUser().getEmail();
         }
         // Don't send if transition is to this user
         if (!AtsPlugin.isAtsAlwaysEmailMe() && emails.equals(skynetAuth.getAuthenticatedUser().getEmail())) {
            if (testing) System.out.println("notifyAssignees = not sending; assignee is me");
            return;
         }
         // Don't try to send if there are no actives (Cancel, Hold or
         // Completed states)
         if (emails.equals("")) {
            if (testing) System.out.println("notifyAssignees = not sending; no emails to send");
            return;
         }
         String headerStr =
               "You have been identified as an assignee for this " + sma.getArtifactTypeName() + ".<br><br>";
         AEmail emailMessage =
               new AEmail(null, skynetAuth.getAuthenticatedUser().getEmail(),
                     skynetAuth.getAuthenticatedUser().getEmail(),
                     "ATS Assignee Alert: " + sma.getArtifactTypeName() + " - \"" + sma.getDescriptiveName() + "\"\n");

         emailMessage.setRecipients(Message.RecipientType.TO, emails);
         emailMessage.setHTMLBody(headerStr);
         emailMessage.addHTMLBody(html);
         if (testing)
            System.out.println("notifyAssignees = sending to " + emails);
         else
            emailMessage.send();
      } catch (Exception ex) {
         MessageDialog.openInformation(null, "Message Could Not Be Sent", "Your Email Message could not be sent");
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }

   private String getActiveEmails() throws SQLException {
      Collection<User> emails = smaMgr.getAssignees();
      // Never email current user
      emails.remove(skynetAuth.getAuthenticatedUser());
      return getEmails(emails);
   }

   public static String getEmails(Collection<User> users) {
      StringBuilder builder = new StringBuilder();
      for (User u : users) {
         if (u.getEmail() != null && !u.getEmail().equals("")) builder.append(u.getEmail() + ", ");
      }
      return builder.toString().replaceFirst(", $", "");
   }

   private void notifySubscribers(IProgressMonitor monitor, String html) {
      try {
         monitor.subTask("Notifying Subscribers");
         ArrayList<User> subscribed = sma.getSubscribed();
         if (subscribed.size() == 0) {
            if (testing) System.out.println("notifySubscribers = not sending; no-one is subscribed");
            return;
         }
         String emails = "";
         for (User u : subscribed)
            emails += u.getEmail() + ", ";
         emails = emails.replaceFirst(", $", "");
         if (!testing && AtsPlugin.isAtsAlwaysEmailMe()) {
            AtsPlugin.getLogger().log(Level.INFO, "NotifySubscribers: Always Email Me Enabled");
            // For debug purposes, show who would have emailed if email is enabled
            if (AtsPlugin.isEmailEnabled()) debug.report("AtsAlwaysEmailMe but would have notifySubscribers => " + emails);
            emails = skynetAuth.getAuthenticatedUser().getEmail();
         }
         if (emails.equals("")) {
            if (testing) System.out.println("notifySubscribers = not sending; emails to send");
            return;
         }
         AEmail emailMessage =
               new AEmail(
                     null,
                     skynetAuth.getAuthenticatedUser().getEmail(),
                     skynetAuth.getAuthenticatedUser().getEmail(),
                     "ATS Subscription Alert: " + sma.getArtifactTypeName() + " - \"" + sma.getDescriptiveName() + "\"\n");
         String notifyStr =
               sma.getArtifactTypeName() + " Transitioned to \"" + sma.getCurrentStateName() + "\"<br><br>";
         String unsubscribeStr =
               "<br><br>You are subscribed to receive notification of transition for this " + sma.getArtifactTypeName() + ".  <br>Enter OSEE ATS to un-subscribe.";

         emailMessage.setRecipients(Message.RecipientType.TO, emails);
         // Remove hyperlinks cause they won't work in email.
         html = html.replaceFirst("<body>", REUtil.quoteMeta("<body>" + notifyStr));
         html = html.replaceFirst("</html>", REUtil.quoteMeta(unsubscribeStr + "</html>"));
         emailMessage.addHTMLBody(html);
         if (testing)
            System.out.println("notifySubscribers = sending to " + emails);
         else
            emailMessage.send();
      } catch (Exception ex) {
         MessageDialog.openInformation(null, "Message Could Not Be Sent", "Your Email Message could not be sent.");
         OSEELog.logException(AtsPlugin.class, ex, false);
      }
   }
}
