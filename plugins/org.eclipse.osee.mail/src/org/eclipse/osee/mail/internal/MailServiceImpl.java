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
package org.eclipse.osee.mail.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.mail.api.MailMessage;
import org.eclipse.osee.mail.api.MailService;
import org.eclipse.osee.mail.api.MailStatus;

/**
 * @author Roberto E. Escobar
 */
public class MailServiceImpl implements MailService {
   private ExecutorAdmin executorAdmin;
   private Log logger;

   private MailMessageFactory factory;
   private volatile MailConfiguration config;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start(Map<String, Object> props) {
      logger.trace("Starting [%s]...", getClass().getSimpleName());
      initJavaxMailRuntime();
      AtomicInteger testCounter = new AtomicInteger();
      factory = new MailMessageFactory(logger, testCounter);
      update(props);
   }

   public void stop(Map<String, Object> props) {
      logger.trace("Stopping [%s]...", getClass().getSimpleName());
      factory = null;
      config = null;
   }

   public void update(Map<String, Object> props) {
      logger.trace("Configuring [%s]...", getClass().getSimpleName());
      config = MailConfiguration.newConfig(props);
   }

   private void initJavaxMailRuntime() {
      MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
      mc.addMailcap("text/*;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
      mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
      mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
      mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("multipart/mixed;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
      mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
      mc.addMailcap("image/jpeg;; x-java-content-handler=com.sun.mail.handlers.image_jpeg");
      mc.addMailcap("image/gif;; x-java-content-handler=com.sun.mail.handlers.image_gif");
      CommandMap.setDefaultCommandMap(mc);
   }

   private MailMessage newTestMailMessage() {
      String adminEmail = config.getAdminEmail();
      String subject = config.getTestEmailSubject();
      String body = config.getTestEmailBody();
      return factory.createTestMessage(adminEmail, subject, body);
   }

   @Override
   public MailStatus sendTestMessage() {
      MailMessage message = newTestMailMessage();
      List<MailStatus> results = sendMessages(message);
      return results.isEmpty() ? null : results.iterator().next();
   }

   @Override
   public Future<MailStatus> sendAsyncTestMessage() {
      MailMessage message = newTestMailMessage();
      List<Future<MailStatus>> results = sendAsyncMessages(message);
      return results.isEmpty() ? null : results.iterator().next();
   }

   @Override
   public List<MailStatus> sendMessages(MailMessage... email) {
      return sendMessages(Arrays.asList(email));
   }

   @Override
   public List<MailStatus> sendMessages(Iterable<MailMessage> emails) {
      List<Future<MailStatus>> futures = sendAsyncMessages(emails);
      List<MailStatus> toReturn = new ArrayList<>();
      for (Future<MailStatus> future : futures) {
         toReturn.add(executeAndGetStatus(future));
      }
      return toReturn;
   }

   @Override
   public List<Future<MailStatus>> sendAsyncMessages(MailMessage... email) {
      return sendAsyncMessages(Arrays.asList(email));
   }

   private MailStatus executeAndGetStatus(Future<MailStatus> future) {
      MailStatus toReturn;
      try {
         toReturn = future.get();
      } catch (Exception ex) {
         toReturn = new MailStatus();
         toReturn.setSubject(Lib.exceptionToString(ex));
      }
      return toReturn;
   }

   @Override
   public List<Future<MailStatus>> sendAsyncMessages(Iterable<MailMessage> emails) {
      List<Future<MailStatus>> futures = new ArrayList<>();
      for (MailMessage mail : emails) {
         String uuid = mail.getId();

         Callable<MailStatus> callable = newSendCallable(mail);
         try {
            Future<MailStatus> future = executorAdmin.schedule("Mail Service", callable);
            futures.add(future);
         } catch (Exception ex) {
            logger.error(ex, "Error sending email [%s] ", uuid);
         }
      }
      return futures;
   }

   private Callable<MailStatus> newSendCallable(MailMessage mail) {
      return new SendMailCallable(config, factory, mail);
   }
}