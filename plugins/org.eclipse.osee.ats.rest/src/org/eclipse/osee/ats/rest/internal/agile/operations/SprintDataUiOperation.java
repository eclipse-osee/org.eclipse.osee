/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.rest.internal.agile.operations;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileReportType;
import org.eclipse.osee.ats.api.agile.IAgileSprintHtmlOperation;
import org.eclipse.osee.ats.rest.internal.agile.AgileEndpointImpl;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;

/**
 * @author Donald G. Dunne
 */
public class SprintDataUiOperation implements IAgileSprintHtmlOperation {

   private final AtsApi atsApi;
   private final IResourceRegistry registry;

   public SprintDataUiOperation(AtsApi atsApi, IResourceRegistry registry) {
      this.atsApi = atsApi;
      this.registry = registry;
   }

   @Override
   public String getReportHtml(long teamId, long sprintId) {
      AgileEndpointImpl agileEp = new AgileEndpointImpl(atsApi, registry, null, null);
      return agileEp.getSprintDataTable(teamId, sprintId);
   }

   @Override
   public AgileReportType getReportType() {
      return AgileReportType.Data_Table;
   }

}
