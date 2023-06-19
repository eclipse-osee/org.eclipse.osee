/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.ev;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.column.WorkPackageFilterTreeDialog.IWorkPackageProvider;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * Provides work packages from quick search
 *
 * @author Donald G. Dunne
 */
public class WorkPackageSearchProvider implements IWorkPackageProvider {

   public WorkPackageSearchProvider() {
   }

   @Override
   public Collection<IAtsWorkPackage> getActiveWorkPackages() {
      Collection<ArtifactToken> selectableWorkPackageTokens = ArtifactQuery.getArtifactTokenListFromTypeAndActive(
         AtsArtifactTypes.WorkPackage, AtsAttributeTypes.Active, AtsApiService.get().getAtsBranch());
      Collection<IAtsWorkPackage> items = new LinkedList<>();
      for (Artifact art : ArtifactQuery.getArtifactListFrom(selectableWorkPackageTokens,
         AtsApiService.get().getAtsBranch())) {
         items.add(AtsApiService.get().getEarnedValueService().getWorkPackage(art));
      }
      return items;
   }

   @Override
   public Collection<IAtsWorkPackage> getAllWorkPackages() {
      Collection<ArtifactToken> selectableWorkPackageTokens =
         ArtifactQuery.getArtifactTokenListFromType(AtsArtifactTypes.WorkPackage, AtsApiService.get().getAtsBranch());
      Collection<IAtsWorkPackage> items = new LinkedList<>();
      for (Artifact art : ArtifactQuery.getArtifactListFrom(selectableWorkPackageTokens,
         AtsApiService.get().getAtsBranch())) {
         items.add(AtsApiService.get().getEarnedValueService().getWorkPackage(art));
      }
      return items;
   }

}
