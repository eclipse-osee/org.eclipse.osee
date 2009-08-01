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
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypesSearchItem extends WorldUISearchItem {

   private final Collection<String> artifactTypeNames;

   public ArtifactTypesSearchItem(String name, Collection<String> artifactTypeNames) {
      super(name, FrameworkImage.FLASHLIGHT);
      this.artifactTypeNames = artifactTypeNames;
   }

   public ArtifactTypesSearchItem(ArtifactTypesSearchItem artifactTypesSearchItem) {
      super(artifactTypesSearchItem, FrameworkImage.FLASHLIGHT);
      this.artifactTypeNames = artifactTypesSearchItem.artifactTypeNames;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (artifactTypeNames == null || artifactTypeNames.size() == 0) throw new OseeArgumentException(
            "Inavlid search \"" + getName() + "\"");
      return ArtifactQuery.getArtifactListFromTypes(artifactTypeNames, AtsUtil.getAtsBranch(), false);
   }

   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypesSearchItem(this);
   }

}
