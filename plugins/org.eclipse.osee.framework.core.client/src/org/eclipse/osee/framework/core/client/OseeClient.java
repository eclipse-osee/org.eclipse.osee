/*********************************************************************
 * Copyright (c) 2012, 2022 Boeing
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
import org.eclipse.osee.framework.core.OseeApi;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.server.ide.api.SessionEndpoint;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.ExceptionRegistryEndpoint;
import org.eclipse.osee.orcs.rest.model.GridCommanderEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author John Misinco
 */
public interface OseeClient extends OseeApi {

   String OSEE_APPLICATION_SERVER = org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;

   QueryBuilder createQueryBuilder(BranchId branch);

   BranchEndpoint getBranchEndpoint();

   RelationEndpoint getRelationEndpoint(BranchId branch);

   TransactionEndpoint getTransactionEndpoint();

   TypesEndpoint getTypesEndpoint();

   IndexerEndpoint getIndexerEndpoint();

   ClientEndpoint getClientEndpoint();

   ResourcesEndpoint getResourcesEndpoint();

   DatastoreEndpoint getDatastoreEndpoint();

   /**
    * Obtains an object that implements the {@link DataRightsEndpoint} to process REST API requests to analyze the data
    * rights for a sequence of artifacts.
    *
    * @return an implementation of the {@link DataRightsEndpoint} interface.
    */

   DataRightsEndpoint getDataRightsEndpoint();

   /**
    * Gets a JAX-RS proxy implementation of the {@link ExceptionRegistryEndpoint} interface for making REST API calls to
    * the Exception Registry service.
    *
    * @return JAX-RS proxy implementation of the {@link ExceptionRegistryEndpoint} interface.
    */

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

   /**
    * Obtains an object that implements the {@link PublishingEndpoint} to process REST API requests for publishing
    * artifacts.
    *
    * @return an implementation of the {@link PublishingEndpoint} interface.
    */

   PublishingEndpoint getPublishingEndpoint();

   /**
    * Obtains an object that implements the {@link SynchronizationEndpoint} to process REST API requests for
    * synchronization artifacts.
    *
    * @return an implementation of the {@link SynchronizationEndpoint} interface.
    */

   SynchronizationEndpoint getSynchronizationEndpoint();

   /**
    * Obtains an object that implements the {@link TemplateManagerEndpoint} to process REST API requests for publishing
    * templates.
    *
    * @return an implementation of the {@link TemplateManagerEndpoint} interface.
    */

   TemplateManagerEndpoint getTemplateManagerEndpoint();

   /**
    * Obtains an object that implements the {@link TogglesEndpoint} to process REST API requests for toggle values.
    *
    * @return an implementation of the {@link TogglesEndpoint} interface.
    */

   TogglesEndpoint getTogglesEndpoint();

   TogglesClientImpl getTogglesClient();

   @Deprecated
   String loadAttributeValue(AttributeId attrId, TransactionId transactionId, ArtifactToken artifact);
}
