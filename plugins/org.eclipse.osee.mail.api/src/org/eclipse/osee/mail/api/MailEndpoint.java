/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.mail.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.util.MailStatus;

/**
 * @author Roberto E. Escobar
 */
@Path("send")
public interface MailEndpoint {

   @Path("test")
   @POST
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   MailStatus sendTestMail();

   @POST
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   MailStatus[] sendMail(MailMessage mailMessage);

}
