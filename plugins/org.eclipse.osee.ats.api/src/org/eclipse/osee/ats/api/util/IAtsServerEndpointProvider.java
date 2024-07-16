/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.api.util;

import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.agile.jira.JiraEndpoint;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.metrics.MetricsEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.util.health.AtsHealthEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionUiEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.define.api.GitEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;

/**
 * Client provider for server endpoints.
 *
 * @author Donald G. Dunne
 */
public interface IAtsServerEndpointProvider {

   public AtsNotifyEndpointApi getNotifyEndpoint();

   public AtsTaskEndpointApi getTaskEp();

   public AtsTeamWfEndpointApi getTeamWfEp();

   /**
    * This should not be used unless configurations are being updated. Use AtsApi.getConfigurations
    */
   public AtsConfigEndpointApi getConfigEndpoint();

   public AgileEndpointApi getAgileEndpoint();

   public AtsWorkPackageEndpointApi getWorkPackageEndpoint();

   public AtsActionEndpointApi getActionEndpoint();

   public AtsActionUiEndpointApi getActionUiEndpoint();

   public AtsWorldEndpointApi getWorldEndpoint();

   AtsHealthEndpointApi getHealthEndpoint();

   InsertionActivityEndpointApi getInsertionActivityEp();

   InsertionEndpointApi getInsertionEp();

   ProgramEndpointApi getProgramEp();

   CountryEndpointApi getCountryEp();

   AgileEndpointApi getAgile();

   TupleEndpoint getTupleEp();

   ResourcesEndpoint getResourcesEp();

   GitEndpoint getGitEndpoint();

   JiraEndpoint getJiraEndpoint();

   MetricsEndpointApi getMetricsEp();

}
