/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Collection;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeWithInheritenceSearchItem extends WorldUISearchItem {
   private final ArtifactTypeToken artifactType;

   public ArtifactTypeWithInheritenceSearchItem(String name, ArtifactTypeToken artifactType) {
      super(name, FrameworkImage.FLASHLIGHT);
      this.artifactType = artifactType;
   }

   private ArtifactTypeWithInheritenceSearchItem(ArtifactTypeWithInheritenceSearchItem artifactTypesSearchItem) {
      super(artifactTypesSearchItem, FrameworkImage.FLASHLIGHT);
      this.artifactType = artifactTypesSearchItem.artifactType;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) {
      return ArtifactQuery.getArtifactListFromTypeWithInheritence(artifactType, AtsClientService.get().getAtsBranch(),
         EXCLUDE_DELETED);
   }

   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypeWithInheritenceSearchItem(this);
   }
}