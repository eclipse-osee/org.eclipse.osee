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

import java.util.List;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceEnumerationSetApi extends QueryCapableMIMAPI<InterfaceEnumerationSet>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<InterfaceEnumerationSet> getAccessor();

   InterfaceEnumerationSet get(BranchId branch, ArtifactId enumSetId);

   List<InterfaceEnumerationSet> getAll(BranchId branch);

   List<InterfaceEnumerationSet> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   List<InterfaceEnumerationSet> getAll(BranchId branch, long pageNum, long pageSize);

   List<InterfaceEnumerationSet> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);
}
