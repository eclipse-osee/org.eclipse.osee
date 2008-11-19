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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypesSearchItem extends WorldUISearchItem {

   private final Collection<String> artifactTypeNames;

   public ArtifactTypesSearchItem(String name, Collection<String> artifactTypeNames) {
      super(name);
      this.artifactTypeNames = artifactTypeNames;
   }

   public ArtifactTypesSearchItem(ArtifactTypesSearchItem artifactTypesSearchItem) {
      super(artifactTypesSearchItem);
      this.artifactTypeNames = artifactTypesSearchItem.artifactTypeNames;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      if (artifactTypeNames == null || artifactTypeNames.size() == 0) throw new OseeArgumentException(
            "Inavlid search \"" + getName() + "\"");
      return ArtifactQuery.getArtifactsFromTypes(artifactTypeNames, AtsPlugin.getAtsBranch());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#copy()
    */
   @Override
   public WorldUISearchItem copy() {
      return new ArtifactTypesSearchItem(this);
   }

}
