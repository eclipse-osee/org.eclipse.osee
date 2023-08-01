/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.api.agile.jira;

import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

/**
 * @author Stephen J. Molaro
 */

@Path("jira")
public interface JiraEndpoint {

   /**
    * Takes a Json query and uses it to query Jira for all results.
    *
    * @author Stephen J. Molaro
    * @param @Encoded String jsonPayload - Json query to search in Jira. Format can be found at
    * https://docs.atlassian.com/software/jira/docs/api/REST/8.9.1/
    */
   @POST
   @Path("search")
   @Consumes(MediaType.APPLICATION_JSON)
   public String searchJira(@Encoded String jsonPayload);

   /**
    * Takes a Json query and uses it to create an issue in Jira.
    *
    * @author Stephen J. Molaro
    * @param @Encoded String jsonPayload - Json query to create issue in Jira. Format can be found at
    * https://docs.atlassian.com/software/jira/docs/api/REST/8.9.1/
    */
   @POST
   @Path("create")
   @Consumes(MediaType.APPLICATION_JSON)
   public String createJiraIssue(@Encoded String jsonPayload);

   /**
    * Takes a Json query and uses it to transition states of an issue in Jira.
    *
    * @author Stephen J. Molaro
    * @param @Encoded String jsonPayload - Json query to transition an issue in Jira. Format can be found at
    * https://docs.atlassian.com/software/jira/docs/api/REST/8.9.1/
    */
   @POST
   @Path("{issueId}/transition")
   @Consumes(MediaType.APPLICATION_JSON)
   public String transitionJiraIssue(@Encoded String jsonPayload, @PathParam("issueId") String issueId);

   /**
    * Takes a Json query and uses it to query Jira for all results.
    *
    * @author Stephen J. Molaro
    * @param @Encoded String jsonPayload - Json query to edit issue in Jira. Format can be found at
    * https://docs.atlassian.com/software/jira/docs/api/REST/8.9.1/
    */
   @PUT
   @Path("{issueId}/edit")
   @Consumes(MediaType.APPLICATION_JSON)
   public String editJira(@Encoded String jsonPayload, @PathParam("issueId") String issueId);
}