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

package org.eclipse.osee.orcs;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsAdmin {

   Callable<OrcsMetaData> createFetchOrcsMetaData();

   TransactionId createDatastoreAndSystemBranches(String typeModel);

   Callable<OrcsMetaData> migrateDatastore();

   boolean isDataStoreInitialized();

   void createDemoBranches();

   void requireRole(UserId user, ArtifactId role);

   void createSynonymsAndGrants();

   void changeArtifactTypeOutsideofHistory(ArtifactTypeId artifactType, List<? extends ArtifactId> artifacts);

   void updateBootstrapUser(UserId accountId);

   void registerMissingOrcsTypeJoins();

}