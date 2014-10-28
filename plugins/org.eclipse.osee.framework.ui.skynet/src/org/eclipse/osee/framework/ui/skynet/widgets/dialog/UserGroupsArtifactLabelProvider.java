package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.graphics.Image;

public class UserGroupsArtifactLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      return ArtifactImageManager.getImage((Artifact) arg0);
   }

   @Override
   public String getText(Object arg0) {
      return ((Artifact) arg0).getName() + " - (" + ((Artifact) arg0).getArtifactTypeName() + ")";
   }

   @Override
   public void addListener(ILabelProviderListener arg0) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object arg0, String arg1) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener arg0) {
      // do nothing
   }

}
