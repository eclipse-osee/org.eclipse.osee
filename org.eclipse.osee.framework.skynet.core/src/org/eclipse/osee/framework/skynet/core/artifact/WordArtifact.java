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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.SQLException;

/**
 * @author Ryan D. Brooks
 */
public class WordArtifact extends Artifact {
   public static final String ARTIFACT_NAME = "Word Artifact";
   private boolean isWholeWordArtifact;

   /**
    * @return the isWholeWordArtifact
    */
   public boolean isWholeWordArtifact() {
      return isWholeWordArtifact;
   }

   /**
    * @param isWholeWordArtifact the isWholeWordArtifact to set
    */
   public void setWholeWordArtifact(boolean isWholeWordArtifact) {
      this.isWholeWordArtifact = isWholeWordArtifact;
   }

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    * @throws SQLException
    */
   public WordArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

}
