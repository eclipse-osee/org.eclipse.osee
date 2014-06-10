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

import static org.eclipse.osee.jaxrs.client.JaxRsClientConstants.JAXRS_CLIENT_SERVER_ADDRESS;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsClientFactory;
import org.eclipse.osee.jaxrs.client.JaxRsClientUtils;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactory;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactoryImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryBuilderImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryExecutorV1;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryExecutorV1.BaseUriBuilder;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryOptions;
import org.eclipse.osee.orcs.rest.model.Client;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class OseeClientImpl implements OseeClient, BaseUriBuilder {

   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";

   private volatile PredicateFactory predicateFactory;
   private volatile QueryExecutorV1 executor;
   private volatile JaxRsClient client;

   public void start(Map<String, Object> properties) {
      predicateFactory = new PredicateFactoryImpl();

      update(properties);
   }

   public void stop() {
      executor = null;
      client = null;
      predicateFactory = null;
   }

   public void update(Map<String, Object> properties) {
      Map<String, Object> propsToUse = properties;
      String newServerAddress = JaxRsClientUtils.get(propsToUse, JAXRS_CLIENT_SERVER_ADDRESS, null);
      if (newServerAddress == null) {
         propsToUse = new HashMap<String, Object>(properties);
         propsToUse.put(JAXRS_CLIENT_SERVER_ADDRESS, System.getProperty(OSEE_APPLICATION_SERVER, ""));
      }
      client = JaxRsClientFactory.createClient(propsToUse);
      executor = new QueryExecutorV1(client, this);
   }

   @Override
   public UriBuilder newBuilder() {
      return UriBuilder.fromPath("orcs");
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
      URI uri = newBuilder().path("client").build();
      WebResource resource = client.createResource(uri);
      Client clientResult = null;
      try {
         clientResult = resource.accept(MediaType.APPLICATION_XML).get(Client.class);
         if (clientResult != null) {
            result = clientResult.getSupportedVersions().contains(OseeCodeVersion.getVersion());
         }
      } catch (UniformInterfaceException ex) {
         throw client.handleException(ex);
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
