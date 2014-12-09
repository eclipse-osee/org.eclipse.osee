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

import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jaxrs.client.JaxRsClient;
import org.eclipse.osee.jaxrs.client.JaxRsExceptions;
import org.eclipse.osee.jaxrs.client.JaxRsWebTarget;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactory;
import org.eclipse.osee.orcs.rest.client.internal.search.PredicateFactoryImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryBuilderImpl;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryExecutor;
import org.eclipse.osee.orcs.rest.client.internal.search.QueryOptions;
import org.eclipse.osee.orcs.rest.model.IdeVersion;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class OseeClientImpl implements OseeClient, QueryExecutor {

   private static final String OSEE_APPLICATION_SERVER = "osee.application.server";

   private PredicateFactory predicateFactory;
   private volatile JaxRsClient client;
   private volatile URI baseUri;

   public void start(Map<String, Object> properties) {
      predicateFactory = new PredicateFactoryImpl();

      update(properties);
   }

   public void stop() {
      client = null;
      baseUri = null;
      predicateFactory = null;
   }

   public void update(Map<String, Object> properties) {
      client = JaxRsClient.newBuilder().properties(properties).build();
      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address = System.getProperty(OSEE_APPLICATION_SERVER, "");
      }
      baseUri = UriBuilder.fromUri(address).path("orcs").build();
   }

   private JaxRsWebTarget newTarget(String path, Object... values) {
      URI uri = UriBuilder.fromUri(baseUri).path(path).build(values);
      return client.target(uri);
   }

   @Override
   public QueryBuilder createQueryBuilder(IOseeBranch branch) {
      QueryOptions options = new QueryOptions();
      List<Predicate> predicates = new ArrayList<Predicate>();
      return new QueryBuilderImpl(branch, predicates, options, predicateFactory, this);
   }

   @Override
   public Collection<String> getIdeClientSupportedVersions() {
      IdeVersion clientResult = null;
      try {
         clientResult = newTarget("ide/versions").request(MediaType.APPLICATION_JSON).get(IdeVersion.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
      return clientResult != null ? clientResult.getVersions() : Collections.<String> emptySet();
   }

   @Override
   public boolean isClientVersionSupportedByApplicationServer() {
      return getIdeClientSupportedVersions().contains(OseeCodeVersion.getVersion());
   }

   @Override
   public boolean isApplicationServerAlive() {
      boolean alive = false;
      try {
         getIdeClientSupportedVersions();
         alive = true;
      } catch (Exception ex) {
         alive = false;
      }
      return alive;
   }

   @Override
   public int getCount(IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResponse result = performSearch(RequestType.COUNT, branch, predicates, options);
      return result.getTotal();
   }

   @Override
   public SearchResult getResults(RequestType request, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResponse result = performSearch(request, branch, predicates, options);
      return result;
   }

   private SearchResponse performSearch(RequestType requestType, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      Conditions.checkNotNull(requestType, "RequestType");
      int fromTx = 0;
      if (options.isHistorical()) {
         fromTx = options.getFromTransaction();
      }

      boolean includeDeleted = false;
      if (options.areDeletedIncluded()) {
         includeDeleted = true;
      }

      SearchRequest params = new SearchRequest(branch.getUuid(), predicates, requestType, fromTx, includeDeleted);

      JaxRsWebTarget resource = newTarget("branch/{branch-uuid}/artifact/search/v1", branch.getUuid());
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(params), SearchResponse.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public void executeScript(String script, Properties properties, boolean debug, Writer writer) {
      String props = null;
      try {
         if (properties != null && !properties.isEmpty()) {
            StringWriter strWriter = new StringWriter();
            properties.store(strWriter, "");
            props = strWriter.toString();
         }
         Form form = new Form();
         form.param("script", script);
         form.param("debug", Boolean.toString(debug));
         if (props != null && props.length() > 0) {
            form.param("parameters", props);
         }
         URI uri = UriBuilder.fromUri(baseUri).path("script").build();
         String result =
            JaxRsClient.newClient().target(uri).request(MediaType.APPLICATION_JSON_TYPE).post(Entity.form(form),
               String.class);
         writer.write(result);
      } catch (Exception ex) {
         JaxRsExceptions.asOseeException(ex);
      }
   }

}
