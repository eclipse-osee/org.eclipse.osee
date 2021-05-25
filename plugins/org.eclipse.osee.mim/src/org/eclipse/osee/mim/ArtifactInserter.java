/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class used for storing/presenting artifact information
 */
public interface ArtifactInserter<T> {

   XResultData addArtifact(T newArtifact, UserId account, BranchId branch);

   XResultData relateArtifact(ArtifactId artifactToRelate, ArtifactId artifactToRelateTo, RelationTypeToken relation, BranchId branch, UserId account);

   XResultData unrelateArtifact(ArtifactId artifactToUnRelate, ArtifactId artifactToUnRelateFrom, RelationTypeToken relation, BranchId branch, UserId account);

   XResultData replaceArtifact(T newArtifact, UserId account, BranchId branch);

   XResultData patchArtifact(T newArtifact, UserId account, BranchId branch);

   XResultData removeArtifact(ArtifactId artifactToRemove, UserId account, BranchId branch);
}
