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
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsServerEndpointProvider;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;

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

   public AtsServerEndpointProviderImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   private JaxRsWebTarget getAtsTarget() {
      if (target == null) {
         String appServer = atsApi.getApplicationServerBase();
         String atsUri = String.format("%s/ats", appServer);
         JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
         // TBD target = jaxRsClient.target(atsUri).register(WorkItemJsonReader.class);
         target = jaxRsClient.target(atsUri);
      }
      return target;
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

}
