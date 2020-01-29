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
package org.eclipse.osee.ats.ide.internal;

import org.eclipse.osee.ats.api.agile.AgileEndpointApi;
import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageEndpointApi;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.api.workflow.AtsWorldEndpointApi;
import org.eclipse.osee.ats.ide.util.IAtsClient;
import org.eclipse.osee.ats.ide.workflow.WorkItemJsonReader;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.orcs.rest.model.TupleEndpoint;

/**
 * @author Donald G. Dunne
 */
public class AtsClientService {

   private static IAtsClient atsClient;
   private static JaxRsWebTarget target;
   private static AtsTaskEndpointApi taskEp;
   private static AtsConfigEndpointApi configEp;
   private static AgileEndpointApi agileEp;
   private static AtsWorldEndpointApi worldEp;
   private static AtsWorkPackageEndpointApi workPackageEp;
   private static TupleEndpoint atsBranchTupleEndpoint;
   private static AtsNotifyEndpointApi notifyEp;
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
      }
      return target;
   }

   public static AtsNotifyEndpointApi getNotifyEndpoint() {
      if (notifyEp == null) {
         notifyEp = getAtsTarget().newProxy(AtsNotifyEndpointApi.class);
      }
      return notifyEp;
   }

   public static AtsTaskEndpointApi getTaskEp() {
      if (taskEp == null) {
         taskEp = getAtsTarget().newProxy(AtsTaskEndpointApi.class);
      }
      return taskEp;
   }

   public static AtsConfigEndpointApi getConfigEndpoint() {
      if (configEp == null) {
         configEp = getAtsTarget().newProxy(AtsConfigEndpointApi.class);
      }
      return configEp;
   }

   public static AgileEndpointApi getAgileEndpoint() {
      if (agileEp == null) {
         agileEp = getAtsTarget().newProxy(AgileEndpointApi.class);
      }
      return agileEp;
   }

   public static AtsWorkPackageEndpointApi getWorkPackageEndpoint() {
      if (workPackageEp == null) {
         workPackageEp = getAtsTarget().newProxy(AtsWorkPackageEndpointApi.class);
      }
      return workPackageEp;
   }

   public static TupleEndpoint getAtsBranchTupleEndpoint() {
      String appServer = OseeClientProperties.getOseeApplicationServer();
      String commonTupleUrl =
         String.format("%s/orcs/branch/%s/tuples", appServer, atsClient.getAtsBranch().getIdString());
      JaxRsClient jaxRsClient = JaxRsClient.newBuilder().createThreadSafeProxyClients(true).build();
      atsBranchTupleEndpoint = jaxRsClient.target(commonTupleUrl).newProxy(TupleEndpoint.class);
      return atsBranchTupleEndpoint;
   }

   public static AtsActionEndpointApi getActionEndpoint() {
      if (actionEp == null) {
         actionEp = getAtsTarget().newProxy(AtsActionEndpointApi.class);
      }
      return actionEp;
   }

   public static AtsWorldEndpointApi getWorldEndpoint() {
      if (worldEp == null) {
         worldEp = getAtsTarget().newProxy(AtsWorldEndpointApi.class);
      }
      return worldEp;
   }

}