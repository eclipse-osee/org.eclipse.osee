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

package org.eclipse.osee.testscript;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.testscript.internal.TestPointToken;

/**
 * @author Stephen J. Molaro
 */
public interface TestPointApi {

   TestPointToken get(BranchId branch, ArtifactId scriptDefTypeId);

   Collection<TestPointToken> getAll(BranchId branch);

   Collection<TestPointToken> getAll(BranchId branch, ArtifactId viewId);

   Collection<TestPointToken> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<TestPointToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize);

   Collection<TestPointToken> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAllByFilter(BranchId branch, String filter);

   Collection<TestPointToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter);

   Collection<TestPointToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize);

   Collection<TestPointToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize);

   Collection<TestPointToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute);

   Collection<TestPointToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute);

   int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter);
}
