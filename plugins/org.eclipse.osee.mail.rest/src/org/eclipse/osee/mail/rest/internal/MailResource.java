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
package org.eclipse.osee.mail.rest.internal;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.mail.MailMessage;
import org.eclipse.osee.mail.MailService;
import org.eclipse.osee.mail.SendMailOperation;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.MultiPart;

/**
 * @author Roberto E. Escobar
 */
@Path("send")
public class MailResource {

   private static int testEmailCount = 0;

   protected MailService getMailService() {
      return MailApplication.getMailService();
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
   public MailMessage getTestMailMessage() {
      return getMailService().createSystemTestMessage(++testEmailCount);
   }

   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   public String sendTestMail() throws Exception {
      MailMessage message = getMailService().createSystemTestMessage(++testEmailCount);
      List<SendMailOperation> operations = getMailService().createSendOp(message);
      if (!operations.isEmpty()) {
         Operations.executeWorkAndCheckStatus(operations.iterator().next());
      }
      return "Test Email sent successfully";
   }

   @POST
   @Consumes("multipart/mixed")
   public Response sendMail(MultiPart multiPart) throws Exception {
      boolean isProcessed = true;
      MailMessage mailMessage = null;
      int count = 0;

      for (BodyPart part : multiPart.getBodyParts()) {
         if (count == 0) {
            mailMessage = part.getEntityAs(MailMessage.class);
         } else if (mailMessage != null) {
            mailMessage.addAttachment(new BodyPartDataSource(part));
         }
      }
      if (isProcessed) {
         return Response.status(Response.Status.ACCEPTED).entity("Attachements processed successfully.").type(
            MediaType.TEXT_PLAIN).build();
      }
      return Response.status(Response.Status.BAD_REQUEST).entity("Failed to process attachments. Reason : ").type(
         MediaType.TEXT_PLAIN).build();
   }

}
