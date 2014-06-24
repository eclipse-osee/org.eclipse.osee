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
package org.eclipse.osee.mail.internal.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.activation.DataHandler;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.SendMailStatus;

/**
 * @author Roberto E. Escobar
 */
@Path("send")
public class MailResource {

   private static long STATUS_WAIT_TIME = 60;
   private static int testEmailCount = 0;

   private final MailService mailService;

   public MailResource(MailService mailService) {
      super();
      this.mailService = mailService;
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public MailMessage getTestMailMessage() {
      return mailService.createSystemTestMessage(++testEmailCount);
   }

   @POST
   @Path("test")
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public SendMailStatus sendTestMail() throws Exception {
      MailMessage message = mailService.createSystemTestMessage(++testEmailCount);

      List<SendMailStatus> results = sendMail(message);
      return results.iterator().next();
   }

   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public List<SendMailStatus> sendXmlMail(MailMessage mailMessage) throws Exception {
      Collection<? extends DataHandler> handlers = mailMessage.getAttachments();
      for (DataHandler handler : handlers) {
         System.out.println(handler.getName() + " " + handler.getContentType());
      }
      return sendMail(mailMessage);
   }

   private List<SendMailStatus> sendMail(MailMessage... messages) throws InterruptedException, ExecutionException {
      List<Callable<SendMailStatus>> calls = mailService.createSendCalls(STATUS_WAIT_TIME, TimeUnit.SECONDS, messages);
      List<Future<SendMailStatus>> futures = new ArrayList<Future<SendMailStatus>>();

      if (messages.length > 0) {
         ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
         for (Callable<SendMailStatus> task : calls) {
            Future<SendMailStatus> future = executor.submit(task);
            futures.add(future);
         }
         executor.shutdown();
         executor.awaitTermination(100, TimeUnit.MINUTES);
      }
      List<SendMailStatus> results = new ArrayList<SendMailStatus>();
      for (Future<SendMailStatus> future : futures) {
         results.add(future.get());
      }
      return results;
   }
}
