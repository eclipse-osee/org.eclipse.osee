/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeSearchItem extends WorldUISearchItem {

   private final ArtifactTypeToken artifactType;

   public ArtifactTypeSearchItem(String name, ArtifactTypeToken artifactType) {
      super(name, AtsImage.FLASHLIGHT);
      this.artifactType = artifactType;
   }

   public ArtifactTypeSearchItem(ArtifactTypeSearchItem artifactTypeSearchItem) {
      super(artifactTypeSearchItem, AtsImage.FLASHLIGHT);
      this.artifactType = artifactTypeSearchItem.artifactType;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      Conditions.checkNotNull(artifactType, getName());
      return ArtifactQuery.getArtifactListFromType(artifactType, AtsApiService.get().getAtsBranch());
   }

   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypeSearchItem(this);
   }

}
