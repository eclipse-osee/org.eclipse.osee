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
package org.eclipse.osee.ats.client.integration.tests;

import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpoint;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsRuleEndpointApi;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.workflow.WorkItemJsonReader;
import org.eclipse.osee.ats.core.client.workflow.WorkItemsJsonReader;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

/**
 * @author Donald G. Dunne
 */
public class AtsClientService {

   private static IAtsClient atsClient;
   private static AgileEndpointApi agile;
   private static JaxRsWebTarget target;
   private static CountryEndpointApi countryEp;
   private static ProgramEndpointApi programEp;
   private static InsertionEndpointApi insertionEp;
   private static InsertionActivityEndpointApi insertionActivityEp;
   private static AtsTaskEndpointApi taskEp;
   private static AtsRuleEndpointApi ruleEp;
   private static AtsNotifyEndpointApi notifyEp;
   private static AtsConfigEndpoint configEp;
   private static AtsWorkPackageEndpointApi workPackageEp;
   private static AtsActionEndpointApi actionEp;

   public void setAtsClient(IAtsClient atsClient) {
      AtsClientService.atsClient = atsClient;
   }

   public static IAtsClient get() {
      return atsClient;
   }

   private static JaxRsWebTarget getAtsTarget() {
      if (target == null) {
         String appServer = OseeClientProperties.getOseeApplicationServer();
         String atsUri = String.format("%s/ats", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
         target = jaxRsClient.target(atsUri).register(WorkItemJsonReader.class);
         target = target.register(WorkItemsJsonReader.class);
      }
      return target;
   }

   public static AgileEndpointApi getAgile() {
      if (agile == null) {
         agile = getAtsTarget().newProxy(AgileEndpointApi.class);
      }
      return agile;
   }

   public static CountryEndpointApi getCountryEp() {
      if (countryEp == null) {
         countryEp = getAtsTarget().newProxy(CountryEndpointApi.class);
      }
      return countryEp;
   }

   public static ProgramEndpointApi getProgramEp() {
      if (programEp == null) {
         programEp = getAtsTarget().newProxy(ProgramEndpointApi.class);
      }
      return programEp;
   }

   public static InsertionEndpointApi getInsertionEp() {
      if (insertionEp == null) {
         insertionEp = getAtsTarget().newProxy(InsertionEndpointApi.class);
      }
      return insertionEp;
   }

   public static InsertionActivityEndpointApi getInsertionActivityEp() {
      if (insertionActivityEp == null) {
         insertionActivityEp = getAtsTarget().newProxy(InsertionActivityEndpointApi.class);
      }
      return insertionActivityEp;
   }

   public static AtsTaskEndpointApi getTaskEp() {
      if (taskEp == null) {
         taskEp = getAtsTarget().newProxy(AtsTaskEndpointApi.class);
      }
      return taskEp;
   }

   public static AtsRuleEndpointApi getRuleEp() {
      if (ruleEp == null) {
         ruleEp = getAtsTarget().newProxy(AtsRuleEndpointApi.class);
      }
      return ruleEp;
   }

   public static AtsNotifyEndpointApi getNotifyEndpoint() {
      if (notifyEp == null) {
         notifyEp = getAtsTarget().newProxy(AtsNotifyEndpointApi.class);
      }
      return notifyEp;
   }

   public static AtsConfigEndpoint getConfigEndpoint() {
      if (configEp == null) {
         configEp = getAtsTarget().newProxy(AtsConfigEndpoint.class);
      }
      return configEp;
   }

   public static AtsWorkPackageEndpointApi getWorkPackageEndpoint() {
      if (workPackageEp == null) {
         workPackageEp = getAtsTarget().newProxy(AtsWorkPackageEndpointApi.class);
      }
      return workPackageEp;
   }

   public static AtsActionEndpointApi getActionEndpoint() {
      if (actionEp == null) {
         actionEp = getAtsTarget().newProxy(AtsActionEndpointApi.class);
      }
      return actionEp;
   }
}