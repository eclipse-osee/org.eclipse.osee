/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.mail.api.MailService;

/**
 * @author Donald G. Dunne
 */
@Path("health")
public final class HealthResource {

   private AtsApi atsApi;
   private JdbcService jdbcService;
   private static MailService mailService;

   public void setMailService(MailService mailService) {
      HealthResource.mailService = mailService;
   }

   public HealthResource() {
      // for osgi instantiation; this optionally sets the mail service if available
   }

   public HealthResource(AtsApi atsApi, JdbcService jdbcService) {
      this.atsApi = atsApi;
      this.jdbcService = jdbcService;
   }

   /**
    * @return html representation of ATS Health Checks
    */
   @GET
   @Produces(MediaType.TEXT_HTML)
   public Response get() throws Exception {
      AtsHealthCheckOperation validate = new AtsHealthCheckOperation(atsApi, jdbcService, mailService);
      XResultData rd = validate.run();
      return Response.ok(rd.toString().replaceAll("\n", "</br>")).build();
   }

}
