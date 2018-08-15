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
package org.eclipse.osee.orcs.rest.client;

import java.io.Writer;
import java.util.Properties;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.define.api.DataRightsEndpoint;
import org.eclipse.osee.define.api.MSWordEndpoint;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.server.ide.api.client.ClientEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.rest.model.ApplicabilityUiEndpoint;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.DatastoreEndpoint;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.rest.model.OrcsWriterEndpoint;
import org.eclipse.osee.orcs.rest.model.ResourcesEndpoint;
import org.eclipse.osee.orcs.rest.model.TransactionEndpoint;
import org.eclipse.osee.orcs.rest.model.TypesEndpoint;

/**
 * @author John Misinco
 */
public interface OseeClient {

   String OSEE_APPLICATION_SERVER = org.eclipse.osee.framework.core.data.OseeClient.OSEE_APPLICATION_SERVER;

   QueryBuilder createQueryBuilder(BranchId branch);

   void executeScript(String script, Properties properties, boolean debug, MediaType media, Writer writer);

   BranchEndpoint getBranchEndpoint();

   TransactionEndpoint getTransactionEndpoint();

   TypesEndpoint getTypesEndpoint();

   IndexerEndpoint getIndexerEndpoint();

   ClientEndpoint getClientEndpoint();

   ResourcesEndpoint getResourcesEndpoint();

   DatastoreEndpoint getDatastoreEndpoint();

   MSWordEndpoint getWordUpdateEndpoint();

   DataRightsEndpoint getDataRightsEndpoint();

   OrcsWriterEndpoint getOrcsWriterEndpoint();

   ApplicabilityEndpoint getApplicabilityEndpoint(BranchId branch);

   ActivityLogEndpoint getActivityLogEndpoint();

   boolean isLocalHost();

   String getBaseUri();

   ArtifactEndpoint getArtifactEndpoint(BranchId branch);

   ApplicabilityUiEndpoint getApplicabilityUiEndpoint();
}