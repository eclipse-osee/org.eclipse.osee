/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Angel Avila
 */
public interface DispoQuery {

   ArtifactReadable findUser();

   ArtifactReadable findUser(String userId);

   ArtifactReadable findUnassignedUser();

   boolean isUniqueSetName(String programId, String name);

   boolean isUniqueItemName(String programId, String setId, String name);

   ResultSet<DispoSet> findDispoSets(String programId);

   DispoSet findDispoSetsById(String programId, String id);

   ResultSet<DispoItem> findDipoItems(String programId, String setId);

   DispoItem findDispoItemById(String programId, String itemId);

   ResultSet<? extends IOseeBranch> findBaselineBranches();

   IOseeBranch findProgramId(String programID);
}