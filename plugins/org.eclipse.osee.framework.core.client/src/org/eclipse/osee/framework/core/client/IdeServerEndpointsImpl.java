/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *     Boeing - add SynchronizationEndpoint
 **********************************************************************/

package org.eclipse.osee.framework.core.client;

import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.define.rest.api.DefineBranchEndpointApi;
import org.eclipse.osee.define.rest.api.git.GitEndpoint;
import org.eclipse.osee.define.rest.api.importing.ImportEndpoint;
import org.eclipse.osee.define.rest.api.publisher.datarights.DataRightsEndpoint;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingEndpoint;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.TemplateManagerEndpoint;
import org.eclipse.osee.define.rest.api.synchronization.SynchronizationEndpoint;
import org.eclipse.osee.define.rest.api.toggles.TogglesEndpoint;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.orcs.rest.model.ApiKeyEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.ExceptionRegistryEndpoint;
import org.eclipse.osee.orcs.rest.model.GridCommanderEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.KeyValueEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;
import org.eclipse.osee.orcs.rest.model.UserEndpoint;
import org.eclipse.osee.orcs.rest.model.search.builder.QueryEndpoint;

/**
 * Single entry point for all Server Endpoint services for IDE Client. All code should migrate to use this and not all
 * the may ways it's being done.
 *
 * @author Donald G. Dunne
 */
public class IdeServerEndpointsImpl implements IdeServerEndpoints {

   private static IdeServerEndpointsImpl instance;
   private static JaxRsApi jaxRsApi;
   private QueryEndpoint queryEp;

   public IdeServerEndpointsImpl() {
      // for jax-rs
      if (instance == null) {
         instance = this;
      }
   }

   public static IdeServerEndpointsImpl getInstance() {
      if (instance == null) {
         instance = new IdeServerEndpointsImpl();
      }
      return instance;
   }

   private JaxRsApi getJaxRs() {
      return jaxRsApi;
   }

   private <T> T getOrcsBranchEndpoint(Class<T> clazz, BranchId branch) {
      return getJaxRs().newProxy("orcs/branch/" + branch.getIdString(), clazz);
   }

   private <T> T getDefineEndpoint(Class<T> clazz) {
      return getJaxRs().newProxy("define", clazz);
   }

   private <T> T getOrcsEndpoint(Class<T> clazz) {
      return getJaxRs().newProxy("orcs", clazz);
   }

   @Override
   public QueryEndpoint getQueryEndpoint() {
      if (queryEp == null) {
         queryEp = getOrcsEndpoint(QueryEndpoint.class);
      }
      return queryEp;
   }

   @Override
   public BranchEndpoint getBranchEndpoint() {
      return getOrcsEndpoint(BranchEndpoint.class);
   }

   @Override
   public ApiKeyEndpoint getApiKeyEndpoint() {
      return getOrcsEndpoint(ApiKeyEndpoint.class);
   }

   @Override
   public ExceptionRegistryEndpoint getExceptionRegistryEndpoint() {
      return this.getOrcsEndpoint(ExceptionRegistryEndpoint.class);
   }

   @Override
   public TransactionEndpoint getTransactionEndpoint() {
      return getOrcsEndpoint(TransactionEndpoint.class);
   }

   @Override
   public RelationEndpoint getRelationEndpoint(BranchId branch) {
      return getOrcsBranchEndpoint(RelationEndpoint.class, branch);
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
      return getJaxRs().newProxy("ide", ClientEndpoint.class);
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
      return getJaxRs().newProxy("", ActivityLogEndpoint.class);
   }

   @Override
   public SessionEndpoint getSessionEndpoint() {
      return getJaxRs().newProxy("ide", SessionEndpoint.class);
   }

   @Override
   public DefineBranchEndpointApi getDefineBranchEndpoint() {
      return this.getDefineEndpoint(DefineBranchEndpointApi.class);
   }

   @Override
   public GitEndpoint getGitEndpoint() {
      return this.getDefineEndpoint(GitEndpoint.class);
   }

   @Override
   public GridCommanderEndpoint getGridCommanderEndpoint(BranchId branch) {
      return getOrcsBranchEndpoint(GridCommanderEndpoint.class, branch);
   }

   @Override
   public ImportEndpoint getImportEndpoint() {
      return this.getDefineEndpoint(ImportEndpoint.class);
   }

   @Override
   public PublishingEndpoint getPublishingEndpoint() {
      return this.getDefineEndpoint(PublishingEndpoint.class);
   }

   @Override
   public SynchronizationEndpoint getSynchronizationEndpoint() {
      return this.getDefineEndpoint(SynchronizationEndpoint.class);
   }

   @Override
   public TemplateManagerEndpoint getTemplateManagerEndpoint() {
      return this.getDefineEndpoint(TemplateManagerEndpoint.class);
   }

   @Override
   public TogglesEndpoint getTogglesEndpoint() {
      return this.getDefineEndpoint(TogglesEndpoint.class);
   }

   @Override
   public UserEndpoint getOrcsUserEndpoint() {
      return getOrcsEndpoint(UserEndpoint.class);
   }

   @Override
   public DataRightsEndpoint getDataRightsEndpoint() {
      return null;
   }

   @Override
   public KeyValueEndpoint getKeyValueEp() {
      return getOrcsEndpoint(KeyValueEndpoint.class);
   }

   public void bindJaxRsApi(JaxRsApi jaxRsApi) {
      IdeServerEndpointsImpl.jaxRsApi = jaxRsApi;
   }

}