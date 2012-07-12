package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectLabelProvider extends LabelProvider { //StyledCellLabelProvider {

   public AtsObjectLabelProvider() {
      super();
   }

   /*
    * @see ILabelProvider#getImage(Object)
    */
   @Override
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) element);
      } else if (element instanceof IAtsActionableItem) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.ActionableItem);
      } else if (element instanceof IAtsTeamDefinition) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
      } else if (element instanceof IAtsVersion) {
         return ArtifactImageManager.getImage(AtsArtifactTypes.Version);
      } else if (element instanceof Match && ((Match) element).getElement() instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) ((Match) element).getElement());
      }
      return ImageManager.getImage(ImageManager.MISSING);
   }

   /*
    * @see ILabelProvider#getText(Object)
    */
   @Override
   public String getText(Object element) {
      if (element instanceof Match) {
         element = ((Match) element).getElement();
      }
      if (element instanceof IAtsObject) {
         IAtsObject artifact = (IAtsObject) element;

         List<String> extraInfo = new ArrayList<String>();
         String name = artifact.getName();
         extraInfo.add(name != null ? name : "");
         return Collections.toString(" ", extraInfo);
      } else {
         return element.toString();
      }
   }
}
