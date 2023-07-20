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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.InterfaceEnumeration;
import org.eclipse.osee.mim.types.InterfaceStructureElementToken;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfaceEnumerationApi extends QueryCapableMIMAPI<InterfaceEnumeration>, AffectedArtifactMIMAPI<InterfaceStructureElementToken> {

   ArtifactAccessor<InterfaceEnumeration> getAccessor();

   InterfaceEnumeration get(BranchId branch, ArtifactId enumId, List<FollowRelation> relations);

   List<InterfaceEnumeration> get(BranchId branch, List<ArtifactId> enumIds, List<FollowRelation> relations);
}
