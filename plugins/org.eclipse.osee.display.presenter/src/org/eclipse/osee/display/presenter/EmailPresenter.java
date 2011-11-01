/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.display.api.EmailView;
import org.eclipse.osee.display.api.EmailView.EmailSendStatus;
import org.eclipse.osee.display.api.EmailView.SendListener;
import org.eclipse.osee.display.api.EmailView.Validator;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.MailUtils;
import org.eclipse.osee.mail.SendMailStatus;

/**
 * @author Roberto E. Escobar
 */
public class EmailPresenter implements SendListener, Validator {
   private Log logger;
   private EmailView view;
   private MailService mailService;
   private ExecutorAdmin executorAdmin;

   private final List<Future<?>> futures = new ArrayList<Future<?>>();

   public EmailPresenter() {
      // 
   }

   public Log getLogger() {
      return logger;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setView(EmailView view) {
      this.view = view;
   }

   public EmailView getView() {
      return view;
   }

   public void setMailService(MailService mailService) {
      this.mailService = mailService;
   }

   public MailService getMailService() {
      return mailService;
   }

   public void bind() {
      view.addEmailValidator(this);
      view.addOnSendListener(this);
   }

   public void setEmailBody(String body) {
      view.setBody(body);
   }

   public void setFrom(String email) {
      checkEmails(Collections.singleton(email));
      view.setFrom(email);
   }

   public void setReplyTo(Collection<String> emails) {
      checkEmails(emails);
      view.setReplyTo(emails);
   }

   public void setRecipients(Collection<String> emails) {
      checkEmails(emails);
      view.setRecipients(emails);
   }

   public void setSubject(String subject) {
      view.setSubject(subject);
   }

   public void setBody(String body) {
      view.setBody(body);
   }

   public String getMessage(String message, Object... args) {
      return String.format(message, args);
   }

   public void showNotification(String caption) {
      view.displayMessage(caption);
   }

   public void showNotification(String caption, String description) {
      view.displayMessage(caption, description);
   }

   public void cancelSendEmail() {
      if (!futures.isEmpty()) {
         for (Future<?> future : futures) {
            future.cancel(true);
         }
      }
   }

   public void sendEmail(MailMessage message) {
      List<Callable<SendMailStatus>> callables = getMailService().createSendCalls(3, TimeUnit.SECONDS, message);
      view.setEmailStatus(EmailSendStatus.SEND_IN_PROGRESS);
      view.displayMessage("Email", "Sending...");
      EmailExecutionCallback callback = new EmailExecutionCallback();
      for (Callable<SendMailStatus> callable : callables) {
         try {
            futures.add(executorAdmin.schedule(callable, callback));
         } catch (Exception ex) {
            getLogger().error(ex, "Error sending emails");
         }
      }
   }

   @Override
   public void onSend() {
      String from = view.getFrom();
      Collection<String> recepients = view.getRecipients();
      Collection<String> replyTos = view.getReplyTos();
      String subject = view.getSubject();
      String body = view.getBody();

      MailMessage message = new MailMessage();
      message.setFrom(from);
      message.setReplyTo(replyTos);
      message.setRecipients(recepients);
      message.setSubject(subject);
      message.addAttachment(MailUtils.createFromString(subject, body));
      sendEmail(message);
   }

   private void checkEmails(Collection<String> emails) {
      List<String> badEmails = new ArrayList<String>();
      for (String email : emails) {
         if (!accept(email)) {
            badEmails.add(email);
         }
      }
   }

   @Override
   public boolean accept(String toValidate) {
      return MailUtils.isValidEmail(toValidate);
   }

   private final class EmailExecutionCallback implements ExecutionCallback<SendMailStatus> {

      @Override
      public void onSuccess(SendMailStatus result) {
         view.setEmailStatus(EmailSendStatus.SUCCESS);
         futures.clear();
      }

      @Override
      public void onCancelled() {
         view.setEmailStatus(EmailSendStatus.CANCELLED);
         futures.clear();
      }

      @Override
      public void onFailure(Throwable throwable) {
         view.setEmailStatus(EmailSendStatus.FAILED);
         view.displayMessage("Email", getMessage("Error while sending email:\n%s", Lib.exceptionToString(throwable)));
         futures.clear();
      }
   }
}
