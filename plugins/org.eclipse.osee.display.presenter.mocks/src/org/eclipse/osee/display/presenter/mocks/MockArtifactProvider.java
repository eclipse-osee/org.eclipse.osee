package org.eclipse.osee.display.presenter.mocks;

import java.util.List;
import org.eclipse.osee.display.presenter.ArtifactProvider;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.orcs.data.ReadableArtifact;

public class MockArtifactProvider implements ArtifactProvider {

   private ReadableArtifact artifact;
   private List<ReadableArtifact> artList;

   public void setArtifact(ReadableArtifact artifact) {
      this.artifact = artifact;
   }

   public void setArtifactList(List<ReadableArtifact> artList) {
      this.artList = artList;
   }

   @Override
   public ReadableArtifact getArtifactByArtifactToken(IOseeBranch branch, IArtifactToken token) {
      return artifact;
   }

   @Override
   public ReadableArtifact getArtifactByGuid(IOseeBranch branch, String guid) {
      return artifact;
   }

   @Override
   public List<ReadableArtifact> getSearchResults(IOseeBranch branch, boolean nameOnly, String searchPhrase) {
      return artList;
   }

}