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
package org.eclipse.osee.framework.skynet.core.test.mocks;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationTypeSide;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Roberto E. Escobar
 */
public class MockIArtifact extends NamedIdentity implements IArtifact {

   private boolean wasGetFullArtifactCalled;
   private final int uniqueId;
   private final ArtifactType artifactType;
   private final Branch branch;

   // MockObject do not change to use tokens
   public MockIArtifact(int uniqueId, String name, String guid, Branch branch, ArtifactType artifactType) {
      super(guid, name);
      this.uniqueId = uniqueId;
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
   public Artifact getFullArtifact() {
      wasGetFullArtifactCalled = true;
      return null;
   }

   @Override
   public String toString() {
      return String.format("[%s]", getGuid());
   }

   @Override
   public List<? extends IArtifact> getRelatedArtifacts(RelationTypeSide relationTypeSide) {
      return Collections.emptyList();
   }

}
