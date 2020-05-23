/*********************************************************************
 * Copyright (c) 2012 Boeing
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
import org.eclipse.osee.define.api.DataRightsEndpoint;
import org.eclipse.osee.define.api.DefineBranchEndpointApi;
import org.eclipse.osee.define.api.RenderEndpoint;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsScriptEndpoint;
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
   private UriBuilder searchUriBuilder;
   private JaxRsApi jaxRsApi;

   public void setJaxRsApi(JaxRsApi jaxRsApi) {
      this.jaxRsApi = jaxRsApi;
   }

   public void start(Map<String, Object> properties) {
      predicateFactory = new PredicateFactoryImpl();
      update(properties);
   }

   public void stop() {
      client = null;
      orcsUri = null;
      predicateFactory = null;
   }

   public void update(Map<String, Object> properties) {
      client = JaxRsClient.newBuilder().properties(properties).build();
      String address = properties != null ? (String) properties.get(OSEE_APPLICATION_SERVER) : null;
      if (address == null) {
         address =
            System.getProperty(OSEE_APPLICATION_SERVER, org.eclipse.osee.framework.core.data.OseeClient.DEFAULT_URL);
      }
      if (Strings.isValid(address)) {
         orcsUri = UriBuilder.fromUri(address).path("orcs").build();
         searchUriBuilder = UriBuilder.fromUri(address).path("orcs/branch/{branch-uuid}/artifact/search/v1");
      }
   }

   @Override
   public QueryBuilder createQueryBuilder(BranchId branch) {
      QueryOptions options = new QueryOptions();
      List<Predicate> predicates = new ArrayList<>();
      return new QueryBuilderImpl(branch, predicates, options, predicateFactory, this);
   }

   @Override
   public int getCount(BranchId branch, List<Predicate> predicates, QueryOptions options) {
      SearchResponse result = performSearch(RequestType.COUNT, branch, predicates, options);
      return result.getTotal();
   }

   @Override
   public SearchResult getResults(RequestType request, BranchId branch, List<Predicate> predicates, QueryOptions options) {
      SearchResponse result = performSearch(request, branch, predicates, options);
      return result;
   }

   private SearchResponse performSearch(RequestType requestType, BranchId branch, List<Predicate> predicates, QueryOptions options) {
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
      JaxRsWebTarget resource = client.target(searchUriBuilder.build(branch.getIdString()));

      try {
         return resource.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(params), SearchResponse.class);
      } catch (Exception ex) {
         throw JaxRsExceptions.asOseeException(ex);
      }
   }

   @Override
   public String runOrcsScript(String script, Object... data) {
      for (int i = 0; i < data.length; i++) {
         if (data[i] instanceof Id) {
            data[i] = ((Id) data[i]).getIdString();
         }
      }
      return getOrcsScriptEndpoint().getScriptResult(String.format(script, data));
   }

   @Override
   public void runOrcsScript(String script, Properties properties, boolean debug, MediaType mediaType, Writer writer) {
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
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   @Override
   public BranchEndpoint getBranchEndpoint() {
      return getOrcsEndpoint(BranchEndpoint.class);
   }

   @Override
   public TransactionEndpoint getTransactionEndpoint() {
      return getOrcsEndpoint(TransactionEndpoint.class);
   }

   @Override
   public TypesEndpoint getTypesEndpoint() {
      return getOrcsEndpoint(TypesEndpoint.class);
   }

   @Override
   public IndexerEndpoint getIndexerEndpoint() {
      return getOrcsEndpoint(IndexerEndpoint.class);
   }

   @Override
   public ClientEndpoint getClientEndpoint() {
      return jaxRsApi.newProxy("ide", ClientEndpoint.class);
   }

   @Override
   public ResourcesEndpoint getResourcesEndpoint() {
      return getOrcsEndpoint(ResourcesEndpoint.class);
   }

   @Override
   public DatastoreEndpoint getDatastoreEndpoint() {
      return getOrcsEndpoint(DatastoreEndpoint.class);
   }

   @Override
   public RenderEndpoint getRenderEndpoint() {
      return getDefineEndpoint(RenderEndpoint.class);
   }

   @Override
   public DataRightsEndpoint getDataRightsEndpoint() {
      return getDefineEndpoint(DataRightsEndpoint.class);
   }

   @Override
   public OrcsWriterEndpoint getOrcsWriterEndpoint() {
      return getOrcsEndpoint(OrcsWriterEndpoint.class);
   }

   @Override
   public ApplicabilityEndpoint getApplicabilityEndpoint(BranchId branch) {
      return getOrcsBranchEndpoint(ApplicabilityEndpoint.class, branch);
   }

   @Override
   public ApplicabilityUiEndpoint getApplicabilityUiEndpoint() {
      return getOrcsEndpoint(ApplicabilityUiEndpoint.class);
   }

   @Override
   public ArtifactEndpoint getArtifactEndpoint(BranchId branch) {
      return getOrcsBranchEndpoint(ArtifactEndpoint.class, branch);
   }

   @Override
   public ActivityLogEndpoint getActivityLogEndpoint() {
      return jaxRsApi.newProxy("", ActivityLogEndpoint.class);
   }

   @Override
   public OrcsScriptEndpoint getOrcsScriptEndpoint() {
      return getOrcsEndpoint(OrcsScriptEndpoint.class);
   }

   @Override
   public DefineBranchEndpointApi getDefineBranchEndpoint() {
      return getDefineEndpoint(DefineBranchEndpointApi.class);
   }

   private <T> T getOrcsBranchEndpoint(Class<T> clazz, BranchId branch) {
      return jaxRsApi.newProxy("orcs/branch/" + branch.getIdString(), clazz);
   }

   private <T> T getDefineEndpoint(Class<T> clazz) {
      return jaxRsApi.newProxy("define", clazz);
   }

   private <T> T getOrcsEndpoint(Class<T> clazz) {
      return jaxRsApi.newProxy("orcs", clazz);
   }
}