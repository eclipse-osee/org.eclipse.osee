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
package org.eclipse.osee.ats.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeSearchItem extends WorldUISearchItem {

   private final IArtifactType artifactType;

   public ArtifactTypeSearchItem(String name, IArtifactType artifactType) {
      super(name, FrameworkImage.FLASHLIGHT);
      this.artifactType = artifactType;
   }

   public ArtifactTypeSearchItem(ArtifactTypeSearchItem artifactTypeSearchItem) {
      super(artifactTypeSearchItem, FrameworkImage.FLASHLIGHT);
      this.artifactType = artifactTypeSearchItem.artifactType;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (artifactType == null) {
         throw new OseeArgumentException("Inavlid search \"" + getName() + "\"");
      }
      return ArtifactQuery.getArtifactListFromType(artifactType, AtsUtil.getAtsBranch());
   }

   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypeSearchItem(this);
   }

}
