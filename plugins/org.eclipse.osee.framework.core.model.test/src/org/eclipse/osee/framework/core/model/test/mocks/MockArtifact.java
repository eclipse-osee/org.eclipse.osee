/*
 * Created on Jul 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.test.mocks;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Jeff C. Phillips
 */
public class MockArtifact extends NamedIdentity implements IBasicArtifact<Object> {

   private final Branch branch;
   private final ArtifactType artifactType;
   private final int artId;

   public MockArtifact(String guid, String name, IOseeBranch branch, IArtifactType artifactType, int artId) {
      super(guid, name);
      this.branch = new Branch(branch.getGuid(), branch.getName(), BranchType.WORKING, BranchState.MODIFIED, false);
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
   public Object getFullArtifact() throws OseeCoreException {
      return null;
   }

}
