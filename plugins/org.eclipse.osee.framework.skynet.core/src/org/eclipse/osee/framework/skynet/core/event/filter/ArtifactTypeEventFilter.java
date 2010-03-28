/*
 * Created on Mar 23, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.core.data.IArtifactType;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilter implements IEventFilter {

   private final Collection<String> artTypeGuids;

   public ArtifactTypeEventFilter(String artTypeGuid) {
      this.artTypeGuids = Arrays.asList(artTypeGuid);
   }

   public ArtifactTypeEventFilter(Collection<String> artTypeGuids) {
      this.artTypeGuids = artTypeGuids;
   }

   public ArtifactTypeEventFilter(IArtifactType... artifactTypes) {
      this.artTypeGuids = new HashSet<String>();
      for (IArtifactType artifactType : artifactTypes) {
         this.artTypeGuids.add(artifactType.getGuid());
      }
   }

   public boolean isFiltered(String artTypeGuid) {
      return this.artTypeGuids.contains(artTypeGuid);
   }
}
