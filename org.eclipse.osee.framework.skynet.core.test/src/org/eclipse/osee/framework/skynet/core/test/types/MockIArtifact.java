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
package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class MockIArtifact implements IArtifact {

   private boolean wasGetFullArtifactCalled;
   private final int uniqueId;
   private final ArtifactType artifactType;
   private final Branch branch;
   private final String guid;
   private final String name;

   public MockIArtifact(int uniqueId, String name, String guid, Branch branch, ArtifactType artifactType) {
      this.uniqueId = uniqueId;
      this.name = name;
      this.guid = guid;
      this.branch = branch;
      this.artifactType = artifactType;
      clear();
   }

   public void clear() {
      wasGetFullArtifactCalled = false;
   }

   public boolean wasGetFullArtifactCalled() {
      return wasGetFullArtifactCalled;
   }

   @Override
   public int getArtId() {
      return uniqueId;
   }

   @Override
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public Branch getBranch() {
      return branch;
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      wasGetFullArtifactCalled = true;
      return null;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return String.format("[%s]", getGuid());
   }

   @Override
   public List<? extends IArtifact> getRelatedArtifacts(RelationTypeSide relationTypeSide) throws OseeCoreException {
      return Collections.emptyList();
   }

}
