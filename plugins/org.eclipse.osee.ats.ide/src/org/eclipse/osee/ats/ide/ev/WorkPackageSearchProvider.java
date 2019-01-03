/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.ev;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.ide.column.WorkPackageFilterTreeDialog.IWorkPackageProvider;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
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
         AtsArtifactTypes.WorkPackage, AtsAttributeTypes.Active, AtsClientService.get().getAtsBranch());
      Collection<IAtsWorkPackage> items = new LinkedList<>();
      for (Artifact art : ArtifactQuery.getArtifactListFrom(selectableWorkPackageTokens,
         AtsClientService.get().getAtsBranch())) {
         items.add(AtsClientService.get().getEarnedValueService().getWorkPackage(art));
      }
      return items;
   }

   @Override
   public Collection<IAtsWorkPackage> getAllWorkPackages() {
      Collection<ArtifactToken> selectableWorkPackageTokens = ArtifactQuery.getArtifactTokenListFromType(
         AtsArtifactTypes.WorkPackage, AtsClientService.get().getAtsBranch());
      Collection<IAtsWorkPackage> items = new LinkedList<>();
      for (Artifact art : ArtifactQuery.getArtifactListFrom(selectableWorkPackageTokens,
         AtsClientService.get().getAtsBranch())) {
         items.add(AtsClientService.get().getEarnedValueService().getWorkPackage(art));
      }
      return items;
   }

}
