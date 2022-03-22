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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.InterfaceEnumerationSet;
import org.eclipse.osee.mim.types.PlatformTypeToken;

/**
 * @author Luciano T. Vaglienti
 */
public interface InterfacePlatformTypeApi extends QueryCapableMIMAPI<PlatformTypeToken> {

   ArtifactAccessor<PlatformTypeToken> getAccessor();

   PlatformTypeToken get(BranchId branch, ArtifactId platformTypeId);

   PlatformTypeToken getWithRelations(BranchId branch, ArtifactId platformTypeId, List<RelationTypeSide> relationTypes);

   PlatformTypeToken getWithAllParentRelations(BranchId branch, ArtifactId platformTypeId);

   PlatformTypeToken getWithElementRelations(BranchId branch, ArtifactId platformTypeId);

   List<PlatformTypeToken> getAllFromEnumerationSet(InterfaceEnumerationSet enumSet);
}
