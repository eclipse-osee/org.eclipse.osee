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

package org.eclipse.osee.ats.ide.util;

import javax.ws.rs.client.WebTarget;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.cpa.AtsCpaEndpointApi;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.util.health.AtsHealthEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionUiEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.core.workflow.util.WorkItemJsonReader;
import org.eclipse.osee.ats.core.workflow.util.WorkItemsJsonReader;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;

/**
 * @author Donald G. Dunne
 */
public class AtsServerEndpointProviderImpl implements IAtsServerEndpointProvider {

   private final JaxRsApi jaxRsApi;
   private WebTarget target;
   private AtsTaskEndpointApi taskEp;
   private AtsConfigEndpointApi configEp;
   private AgileEndpointApi agileEp;
   private AtsWorkPackageEndpointApi workPackageEp;
   private AtsNotifyEndpointApi notifyEp;
   private AtsActionEndpointApi actionEp;
   private final AtsApi atsApi;
   private AtsWorldEndpointApi worldEp;
   private AtsCpaEndpointApi cpaEp;
   private AgileEndpointApi agile;
   private CountryEndpointApi countryEp;
   private ProgramEndpointApi programEp;
   private InsertionEndpointApi insertionEp;
   private InsertionActivityEndpointApi insertionActivityEp;
   private AtsHealthEndpointApi healthEp;
   private ResourcesEndpoint resourcesEp;
   private AtsActionUiEndpointApi actionUiEp;

   public AtsServerEndpointProviderImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
      jaxRsApi = atsApi.jaxRsApi();
   }

   @Override
   public ResourcesEndpoint getResourcesEp() {
      if (resourcesEp == null) {
         WebTarget target = jaxRsApi.newTarget(String.format("orcs/resources"));
         resourcesEp = jaxRsApi.newProxy(target, ResourcesEndpoint.class);
      }
      return resourcesEp;
   }

   public static WebTarget createAtsTarget(JaxRsApi jaxRsApi) {
      WebTarget target = jaxRsApi.newTarget("ats");
      target.register(WorkItemJsonReader.class);
      target.register(WorkItemsJsonReader.class);
      return target;
   }

   private WebTarget getAtsTarget() {
      if (target == null) {
         target = createAtsTarget(jaxRsApi);
      }
      return target;
   }

   @Override
   public TupleEndpoint getTupleEp() {
      WebTarget target =
         jaxRsApi.newTarget(String.format("orcs/branch/%s/tuples", atsApi.getAtsBranch().getIdString()));
      return jaxRsApi.newProxy(target, TupleEndpoint.class);
   }

   @Override
   public AtsWorldEndpointApi getWorldEndpoint() {
      if (worldEp == null) {
         worldEp = jaxRsApi.newProxy(getAtsTarget(), AtsWorldEndpointApi.class);
      }
      return worldEp;
   }

   @Override
   public AtsNotifyEndpointApi getNotifyEndpoint() {
      if (notifyEp == null) {
         notifyEp = jaxRsApi.newProxy(getAtsTarget(), AtsNotifyEndpointApi.class);
      }
      return notifyEp;
   }

   @Override
   public AtsTaskEndpointApi getTaskEp() {
      if (taskEp == null) {
         taskEp = jaxRsApi.newProxy("ats", AtsTaskEndpointApi.class);
      }
      return taskEp;
   }

   /**
    * This should not be used unless configurations are being updated. Use AtsApi.getConfigurations
    */
   @Override
   public AtsConfigEndpointApi getConfigEndpoint() {
      if (configEp == null) {
         configEp = jaxRsApi.newProxy(getAtsTarget(), AtsConfigEndpointApi.class);
      }
      return configEp;
   }

   @Override
   public AgileEndpointApi getAgileEndpoint() {
      if (agileEp == null) {
         agileEp = jaxRsApi.newProxy(getAtsTarget(), AgileEndpointApi.class);
      }
      return agileEp;
   }

   @Override
   public AtsWorkPackageEndpointApi getWorkPackageEndpoint() {
      if (workPackageEp == null) {
         workPackageEp = jaxRsApi.newProxy(getAtsTarget(), AtsWorkPackageEndpointApi.class);
      }
      return workPackageEp;
   }

   @Override
   public AtsActionEndpointApi getActionEndpoint() {
      if (actionEp == null) {
         actionEp = jaxRsApi.newProxy(getAtsTarget(), AtsActionEndpointApi.class);
      }
      return actionEp;
   }

   @Override
   public AtsActionUiEndpointApi getActionUiEndpoint() {
      if (actionUiEp == null) {
         actionUiEp = jaxRsApi.newProxy(getAtsTarget(), AtsActionUiEndpointApi.class);
      }
      return actionUiEp;
   }

   @Override
   public AtsCpaEndpointApi getCpaEndpoint() {
      if (cpaEp == null) {
         cpaEp = jaxRsApi.newProxy(getAtsTarget(), AtsCpaEndpointApi.class);
      }
      return cpaEp;
   }

   @Override
   public AgileEndpointApi getAgile() {
      if (agile == null) {
         agile = jaxRsApi.newProxy(getAtsTarget(), AgileEndpointApi.class);
      }
      return agile;
   }

   @Override
   public CountryEndpointApi getCountryEp() {
      if (countryEp == null) {
         countryEp = jaxRsApi.newProxy(getAtsTarget(), CountryEndpointApi.class);
      }
      return countryEp;
   }

   @Override
   public ProgramEndpointApi getProgramEp() {
      if (programEp == null) {
         programEp = jaxRsApi.newProxy(getAtsTarget(), ProgramEndpointApi.class);
      }
      return programEp;
   }

   @Override
   public InsertionEndpointApi getInsertionEp() {
      if (insertionEp == null) {
         insertionEp = jaxRsApi.newProxy(getAtsTarget(), InsertionEndpointApi.class);
      }
      return insertionEp;
   }

   @Override
   public InsertionActivityEndpointApi getInsertionActivityEp() {
      if (insertionActivityEp == null) {
         insertionActivityEp = jaxRsApi.newProxy(getAtsTarget(), InsertionActivityEndpointApi.class);
      }
      return insertionActivityEp;
   }

   @Override
   public AtsHealthEndpointApi getHealthEndpoint() {
      if (healthEp == null) {
         healthEp = jaxRsApi.newProxy(getAtsTarget(), AtsHealthEndpointApi.class);
      }
      return healthEp;
   }

}