/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.rule.JaxRuleDefinitions;
import org.eclipse.osee.ats.api.workdef.AddRuleData;
import org.eclipse.osee.ats.api.workdef.RunRuleData;
import org.eclipse.osee.ats.api.workdef.RunRuleResults;

/**
 * @author Mark Joy
 */
public interface AtsRuleEndpointApi {

   @GET
   @Path("rule")
   @Produces({MediaType.APPLICATION_JSON})
   JaxRuleDefinitions get() throws Exception;

   @POST
   @Path("rule")
   @Consumes({MediaType.APPLICATION_JSON})
   Response addRuleToConfig(AddRuleData setRuleData);

   @POST
   @Path("rulerun")
   @Consumes({MediaType.APPLICATION_JSON})
   @Produces({MediaType.APPLICATION_JSON})
   RunRuleResults runWorkflowRules(RunRuleData runRuleData);

}
