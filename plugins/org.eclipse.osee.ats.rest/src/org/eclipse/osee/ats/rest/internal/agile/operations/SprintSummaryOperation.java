/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.agile.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.ats.rest.internal.agile.AgileEndpointImpl;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;

/**
 * @author Donald G. Dunne
 */
public class SprintSummaryOperation implements IAgileSprintHtmlOperation {

   private final AtsApi atsApi;
   private final IResourceRegistry registry;

   public SprintSummaryOperation(AtsApi atsApi, IResourceRegistry registry) {
      this.atsApi = atsApi;
      this.registry = registry;
   }

   @Override
   public String getReportHtml(long teamId, long sprintId) {
      AgileEndpointImpl agileEp = new AgileEndpointImpl((IAtsServer) atsApi, registry, null);
      return agileEp.getSprintSummary(teamId, sprintId);
   }

   @Override
   public AgileReportType getReportType() {
      return AgileReportType.Summary;
   }

}
