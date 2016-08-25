/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.dsl.integration.mocks;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;

/**
 * @author Jeff C. Phillips
 */
public class MockArtifact extends NamedIdentity<String> implements IBasicArtifact<Object> {

   private final Branch branch;
   private final ArtifactType artifactType;
   private final int artId;

   public MockArtifact(String guid, String name, IOseeBranch branch, IArtifactType artifactType, int artId) {
      super(guid, name);
      this.branch =
         new Branch(branch.getUuid(), branch.getName(), BranchType.WORKING, BranchState.MODIFIED, false, false);
      this.artifactType = new ArtifactType(artifactType.getGuid(), artifactType.getName(), false);
      this.artId = artId;
   }

   @Override
   public int getArtId() {
      return artId;
   }

   @Override
   public Branch getBranch() {
      return branch;
   }

   @Override
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   @Override
   public Object getFullArtifact() {
      return null;
   }

   @Override
   public Long getId() {
      return Long.valueOf(artId);
   }

}
