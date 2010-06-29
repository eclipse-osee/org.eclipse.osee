/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilter implements IEventFilter {

   private final Collection<String> artTypeGuids;

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(String artTypeGuid) {
      this.artTypeGuids = Arrays.asList(artTypeGuid);
   }

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(Collection<String> artTypeGuids) {
      this.artTypeGuids = artTypeGuids;
   }

   /**
    * Provide artifact types of events to be passed through. All others will be ignored.
    */
   public ArtifactTypeEventFilter(IArtifactType... artifactTypes) {
      this.artTypeGuids = new HashSet<String>();
      for (IArtifactType artifactType : artifactTypes) {
         this.artTypeGuids.add(artifactType.getGuid());
      }
   }

   @Override
   public boolean isMatch(IBasicGuidArtifact guidArt) {
      return this.artTypeGuids.contains(guidArt.getArtTypeGuid());
   }

   @Override
   public boolean isMatch(IBasicGuidRelation relArt) {
      return this.artTypeGuids.contains(relArt.getArtA().getArtTypeGuid()) ||
      //
      this.artTypeGuids.contains(relArt.getArtB().getArtTypeGuid());

   }

}
