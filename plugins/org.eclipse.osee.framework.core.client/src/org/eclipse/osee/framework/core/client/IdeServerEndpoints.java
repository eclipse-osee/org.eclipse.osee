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

/**
 * @author Donald G. Dunne
 */
public interface IdeServerEndpoints {

   BranchEndpoint getBranchEndpoint();

   ApiKeyEndpoint getApiKeyEndpoint();

   RelationEndpoint getRelationEndpoint(BranchId branch);

   TransactionEndpoint getTransactionEndpoint();

   TypesEndpoint getTypesEndpoint();

   IndexerEndpoint getIndexerEndpoint();

   ClientEndpoint getClientEndpoint();

   ResourcesEndpoint getResourcesEndpoint();

   DatastoreEndpoint getDatastoreEndpoint();

   DataRightsEndpoint getDataRightsEndpoint();

   ExceptionRegistryEndpoint getExceptionRegistryEndpoint();

   OrcsWriterEndpoint getOrcsWriterEndpoint();

   ApplicabilityEndpoint getApplicabilityEndpoint(BranchId branch);

   ActivityLogEndpoint getActivityLogEndpoint();

   ArtifactEndpoint getArtifactEndpoint(BranchId branch);

   ApplicabilityUiEndpoint getApplicabilityUiEndpoint();

   DefineBranchEndpointApi getDefineBranchEndpoint();

   SessionEndpoint getSessionEndpoint();

   ImportEndpoint getImportEndpoint();

   GitEndpoint getGitEndpoint();

   GridCommanderEndpoint getGridCommanderEndpoint(BranchId branch);

   PublishingEndpoint getPublishingEndpoint();

   SynchronizationEndpoint getSynchronizationEndpoint();

   TemplateManagerEndpoint getTemplateManagerEndpoint();

   TogglesEndpoint getTogglesEndpoint();

   UserEndpoint getOrcsUserEndpoint();

   KeyValueEndpoint getKeyValueEp();
}
