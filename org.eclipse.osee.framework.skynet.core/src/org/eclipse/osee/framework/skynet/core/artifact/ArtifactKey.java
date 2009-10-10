package org.eclipse.osee.framework.skynet.core.artifact;

public final class ArtifactKey {
      
	private int artId;
      private int branchId;

      public ArtifactKey(Artifact artifact) {
         this.artId = artifact.getArtId();
         this.branchId = artifact.getBranch().getBranchId();
      }

      public ArtifactKey(int artId, int branchId) {
         this.artId = artId;
         this.branchId = branchId;
      }

      public ArtifactKey getKey(Artifact artifact) {
         this.artId = artifact.getArtId();
         this.branchId = artifact.getBranch().getBranchId();
         return this;
      }

      public ArtifactKey getKey(int artId, int branchId) {
         this.artId = artId;
         this.branchId = branchId;
         return this;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + artId;
         result = prime * result + branchId;
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         }
         if (obj == null) {
            return false;
         }
         if (getClass() != obj.getClass()) {
            return false;
         }
         ArtifactKey other = (ArtifactKey) obj;
         if (artId != other.artId) {
            return false;
         }
         if (branchId != other.branchId) {
            return false;
         }
         return true;
      }
   }