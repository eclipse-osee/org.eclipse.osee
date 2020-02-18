/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util;

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
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.core.workflow.util.WorkItemJsonReader;
import org.eclipse.osee.ats.core.workflow.util.WorkItemsJsonReader;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;

/**
 * @author Donald G. Dunne
 */
public class AtsServerEndpointProviderImpl implements IAtsServerEndpointProvider {

   private JaxRsWebTarget target;
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
   private TupleEndpoint tupleEp;

   public AtsServerEndpointProviderImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public static JaxRsWebTarget getAtsTargetSt() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      String atsUri = String.format("%s/ats", appServer);
      JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
      JaxRsWebTarget target = jaxRsClient.target(atsUri).register(WorkItemJsonReader.class);
      target.register(WorkItemsJsonReader.class);
      return target;
   }

   protected JaxRsWebTarget getAtsTarget() {
      if (target == null) {
         target = getAtsTargetSt();
      }
      return target;
   }

   @Override
   public TupleEndpoint getTupleEp() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      String commonTupleUrl = String.format("%s/orcs/branch/%s/tuples", appServer, atsApi.getAtsBranch().getIdString());
      JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
      tupleEp = jaxRsClient.target(commonTupleUrl).newProxy(TupleEndpoint.class);
      return tupleEp;
   }

   @Override
   public AtsWorldEndpointApi getWorldEndpoint() {
      if (worldEp == null) {
         worldEp = getAtsTarget().newProxy(AtsWorldEndpointApi.class);
      }
      return worldEp;
   }

   @Override
   public AtsNotifyEndpointApi getNotifyEndpoint() {
      if (notifyEp == null) {
         notifyEp = getAtsTarget().newProxy(AtsNotifyEndpointApi.class);
      }
      return notifyEp;
   }

   @Override
   public AtsTaskEndpointApi getTaskEp() {
      if (taskEp == null) {
         taskEp = getAtsTarget().newProxy(AtsTaskEndpointApi.class);
      }
      return taskEp;
   }

   /**
    * This should not be used unless configurations are being updated. Use AtsApi.getConfigurations
    */
   @Override
   public AtsConfigEndpointApi getConfigEndpoint() {
      if (configEp == null) {
         configEp = getAtsTarget().newProxy(AtsConfigEndpointApi.class);
      }
      return configEp;
   }

   @Override
   public AgileEndpointApi getAgileEndpoint() {
      if (agileEp == null) {
         agileEp = getAtsTarget().newProxy(AgileEndpointApi.class);
      }
      return agileEp;
   }

   @Override
   public AtsWorkPackageEndpointApi getWorkPackageEndpoint() {
      if (workPackageEp == null) {
         workPackageEp = getAtsTarget().newProxy(AtsWorkPackageEndpointApi.class);
      }
      return workPackageEp;
   }

   @Override
   public AtsActionEndpointApi getActionEndpoint() {
      if (actionEp == null) {
         actionEp = getAtsTarget().newProxy(AtsActionEndpointApi.class);
      }
      return actionEp;
   }

   @Override
   public AtsCpaEndpointApi getCpaEndpoint() {
      if (cpaEp == null) {
         cpaEp = getAtsTarget().newProxy(AtsCpaEndpointApi.class);
      }
      return cpaEp;
   }

   @Override
   public AgileEndpointApi getAgile() {
      if (agile == null) {
         agile = getAtsTarget().newProxy(AgileEndpointApi.class);
      }
      return agile;
   }

   @Override
   public CountryEndpointApi getCountryEp() {
      if (countryEp == null) {
         countryEp = getAtsTarget().newProxy(CountryEndpointApi.class);
      }
      return countryEp;
   }

   @Override
   public ProgramEndpointApi getProgramEp() {
      if (programEp == null) {
         programEp = getAtsTarget().newProxy(ProgramEndpointApi.class);
      }
      return programEp;
   }

   @Override
   public InsertionEndpointApi getInsertionEp() {
      if (insertionEp == null) {
         insertionEp = getAtsTarget().newProxy(InsertionEndpointApi.class);
      }
      return insertionEp;
   }

   @Override
   public InsertionActivityEndpointApi getInsertionActivityEp() {
      if (insertionActivityEp == null) {
         insertionActivityEp = getAtsTarget().newProxy(InsertionActivityEndpointApi.class);
      }
      return insertionActivityEp;
   }

   @Override
   public AtsHealthEndpointApi getHealthEndpoint() {
      if (healthEp == null) {
         healthEp = getAtsTarget().newProxy(AtsHealthEndpointApi.class);
      }
      return healthEp;
   }

}
