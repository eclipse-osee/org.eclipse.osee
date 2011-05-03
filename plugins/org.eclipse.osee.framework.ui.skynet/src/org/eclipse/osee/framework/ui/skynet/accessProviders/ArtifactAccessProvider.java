/*
 * Created on Sep 16, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.accessProviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.IAccessPolicyHandlerService;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactAccessProvider {

   public ArtifactAccessProvider() {
      super();
   }

   public List<Artifact> getArtifactsWithPermission(IAccessPolicyHandlerService accessService, PermissionEnum permission, List<Artifact> artifacts) throws OseeCoreException {
      ArrayList<Artifact> toReturn = new ArrayList<Artifact>(artifacts);
      Iterator<Artifact> artIterator = toReturn.iterator();

      // Remove Artifact that do not have write permission.
      while (artIterator.hasNext()) {
         Artifact cur = artIterator.next();
         if (!accessService.hasArtifactPermission(Collections.singleton(cur), permission, Level.WARNING).matched()) {
            artIterator.remove();
         }
      }
      return toReturn;
   }
}
