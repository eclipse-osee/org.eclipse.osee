/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.CrossReference;

/**
 * @author Luciano Vaglienti
 */
public interface CrossReferenceApi {

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   CrossReference get(BranchId branch, ArtifactId artId);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, ArtifactId viewId);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter,
      AttributeTypeId orderByAttribute, ArtifactId viewId);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, ArtifactId viewId);

   Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId);

   int getCount(BranchId branch, ArtifactId connectionId, String filter, ArtifactId viewId);
}
