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
package org.eclipse.osee.orcs;

import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsAdmin {

   Callable<OrcsMetaData> createFetchOrcsMetaData();

   void createDatastoreAndSystemBranches(String typeModel);

   Callable<OrcsMetaData> migrateDatastore();

   boolean isDataStoreInitialized();

   void createDemoBranches();

   void requireRole(UserId user, ArtifactId role);

   void createUsers(TransactionBuilder tx, Iterable<UserToken> users);

   void createUser(TransactionBuilder tx, UserToken userToken);

   void createSynonymsAndGrants();

   void changeArtifactTypeOutsideofHistory(ArtifactTypeId artifactType, List<? extends ArtifactId> artifacts);

}