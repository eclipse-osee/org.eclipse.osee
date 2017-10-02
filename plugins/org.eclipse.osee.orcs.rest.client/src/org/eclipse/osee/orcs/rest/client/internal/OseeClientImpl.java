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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.define.report.api.DataRightsEndpoint;
import org.eclipse.osee.define.report.api.MSWordEndpoint;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
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
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;
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

   private PredicateFactory predicateFactory;
   private volatile JaxRsClient client;
   private volatile URI orcsUri;
   private volatile URI defineUri;
   private URI baseUri;

   public void start(Map<String, Object> properties) {
      predicateFactory = new PredicateFactoryImpl();

      update(properties);
   }

   public void stop() {
      client = null;
      orcsUri = null;
      predicateFactory = null;
      defineUri = null;
   }

   public void update(Map<String, Object> properties) {
      client = JaxRsClient.newBuilder().properties(properties).build();
      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address = System.getProperty(OSEE_APPLICATION_SERVER, "http://localhost:8089");
      }
      if (Strings.isValid(address)) {
         baseUri = UriBuilder.fromUri(address).build();
         orcsUri = UriBuilder.fromUri(address).path("orcs").build();
         defineUri = UriBuilder.fromUri(address).path("define").build();
      }
   }

   private JaxRsWebTarget newTarget(String path, Object... values) {
      URI uri = UriBuilder.fromUri(orcsUri).path(path).build(values);
      return client.target(uri);
   }

   @Override
   public QueryBuilder createQueryBuilder(BranchId branch) {
      QueryOptions options = new QueryOptions();
      List<Predicate> predicates = new ArrayList<>();
      return new QueryBuilderImpl(branch, predicates, options, predicateFactory, this);
   }

   @Override
   public int getCount(BranchId branch, List<Predicate> predicates, QueryOptions options)  {
      SearchResponse result = performSearch(RequestType.COUNT, branch, predicates, options);
      return result.getTotal();
   }

   @Override
   public SearchResult getResults(RequestType request, BranchId branch, List<Predicate> predicates, QueryOptions options)  {
      SearchResponse result = performSearch(request, branch, predicates, options);
      return result;
   }

   private SearchResponse performSearch(RequestType requestType, BranchId branch, List<Predicate> predicates, QueryOptions options)  {
      Conditions.checkNotNull(requestType, "RequestType");
      int fromTx = 0;
      if (options.isHistorical()) {
         fromTx = options.getFromTransaction().getId().intValue();
      }

      boolean includeDeleted = false;
      if (options.areDeletedIncluded()) {
         includeDeleted = true;
      }

      SearchRequest params = new SearchRequest(branch, predicates, requestType, fromTx, includeDeleted);

      JaxRsWebTarget resource = newTarget("branch/{branch-uuid}/artifact/search/v1", branch.getIdString());
      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(params), SearchResponse.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public void executeScript(String script, Properties properties, boolean debug, MediaType mediaType, Writer writer) {
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
         URI uri = UriBuilder.fromUri(orcsUri).path("script").build();
         String result = JaxRsClient.newClient().target(uri).request(mediaType).post(Entity.form(form), String.class);
         writer.write(result);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public BranchEndpoint getBranchEndpoint() {
      return client.targetProxy(orcsUri, BranchEndpoint.class);
   }

   @Override
   public TransactionEndpoint getTransactionEndpoint() {
      return client.targetProxy(orcsUri, TransactionEndpoint.class);
   }

   @Override
   public TypesEndpoint getTypesEndpoint() {
      return client.targetProxy(orcsUri, TypesEndpoint.class);
   }

   @Override
   public IndexerEndpoint getIndexerEndpoint() {
      return client.targetProxy(orcsUri, IndexerEndpoint.class);
   }

   @Override
   public ClientEndpoint getClientEndpoint() {
      URI uri = UriBuilder.fromUri(baseUri).path("ide").build();
      return client.targetProxy(uri, ClientEndpoint.class);
   }

   @Override
   public ResourcesEndpoint getResourcesEndpoint() {
      JaxRsClient newClient = JaxRsClient.newBuilder(client.getConfig()).followRedirects(false).build();
      return newClient.targetProxy(orcsUri, ResourcesEndpoint.class);
   }

   @Override
   public DatastoreEndpoint getDatastoreEndpoint() {
      return client.targetProxy(orcsUri, DatastoreEndpoint.class);
   }

   @Override
   public MSWordEndpoint getWordUpdateEndpoint() {
      return client.targetProxy(defineUri, MSWordEndpoint.class);
   }

   @Override
   public DataRightsEndpoint getDataRightsEndpoint() {
      return client.targetProxy(defineUri, DataRightsEndpoint.class);
   }

   @Override
   public OrcsWriterEndpoint getOrcsWriterEndpoint() {
      return client.targetProxy(orcsUri, OrcsWriterEndpoint.class);
   }

   @Override
   public ApplicabilityEndpoint getApplicabilityEndpoint(BranchId branch) {
      URI uri = UriBuilder.fromUri(orcsUri).path("branch/{branch}").build(branch.getId());
      return client.targetProxy(uri, ApplicabilityEndpoint.class);
   }

   @Override
   public ActivityLogEndpoint getActivityLogEndpoint() {
      return client.targetProxy(baseUri, ActivityLogEndpoint.class);
   }

   @Override
   public boolean isLocalHost() {
      return orcsUri.toString().contains("localhost");
   }

   @Override
   public String getBaseUri() {
      return orcsUri.toString();
   }
}