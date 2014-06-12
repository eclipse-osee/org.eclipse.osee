/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client.internal;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactory;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactoryImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryBuilderImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryExecutorV1;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryOptions;
import org.eclipse.osee.orcs.rest.model.Client;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class OseeClientImpl implements OseeClient {

   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";

   private volatile PredicateFactory predicateFactory;
   private volatile QueryExecutorV1 executor;
   private volatile WebTarget baseTarget;

   public void start(Map<String, Object> properties) {
      predicateFactory = new PredicateFactoryImpl();

      update(properties);
   }

   public void stop() {
      executor = null;
      baseTarget = null;
      predicateFactory = null;
   }

   public void update(Map<String, Object> properties) {
      JaxRsClient client = JaxRsClient.newBuilder().properties(properties).build();

      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address = System.getProperty(OSEE_APPLICATION_SERVER, "");
      }

      URI uri = UriBuilder.fromUri(address).path("orcs").build();
      baseTarget = client.target(uri);
      executor = new QueryExecutorV1(baseTarget);
   }

   @Override
   public QueryBuilder createQueryBuilder(IOseeBranch branch) {
      QueryOptions options = new QueryOptions();
      List<Predicate> predicates = new ArrayList<Predicate>();
      return new QueryBuilderImpl(branch, predicates, options, predicateFactory, executor);
   }

   @Override
   public boolean isClientVersionSupportedByApplicationServer() {
      boolean result = false;
      WebTarget resource = baseTarget.path("client");
      Client clientResult = null;
      try {
         clientResult = resource.request(MediaType.APPLICATION_JSON).get(Client.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
      if (clientResult != null) {
         result = clientResult.getSupportedVersions().contains(OseeCodeVersion.getVersion());
      }
      return result;
   }

   @Override
   public boolean isApplicationServerAlive() {
      boolean alive = false;
      try {
         isClientVersionSupportedByApplicationServer();
         alive = true;
      } catch (Exception ex) {
         alive = false;
      }
      return alive;
   }
}
