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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Robert A. Fisher
 */
public class ArtifactTypeSearch implements ISearchPrimitive {
   private final List<ArtifactTypeToken> artifactTypes;

   public ArtifactTypeSearch(List<ArtifactTypeToken> artifactTypes) {
      super();
      this.artifactTypes = artifactTypes;
   }

   @Override
   public String toString() {
      return "Artifact type: " + artifactTypes.toString();
   }

   @Override
   public String getStorageString() {
      StringBuilder storageString = new StringBuilder();

      for (ArtifactTypeToken a : artifactTypes) {
         storageString.append(a.getIdString());
         storageString.append(",");
      }
      storageString.deleteCharAt(storageString.length() - 1);
      return storageString.toString();
   }

   public static ArtifactTypeSearch getPrimitive(String storageString) {
      ArrayList<ArtifactTypeToken> artifactTypes = new ArrayList<>();

      for (String artifactTypeId : storageString.split(",")) {
         artifactTypes.add(TokenFactory.createArtifactType(Long.parseLong(artifactTypeId), "SearchArtType"));
      }

      return new ArtifactTypeSearch(artifactTypes);
   }

   @Override
   public void addToQuery(QueryBuilderArtifact builder) {
      builder.andIsOfType(artifactTypes);
   }

}
