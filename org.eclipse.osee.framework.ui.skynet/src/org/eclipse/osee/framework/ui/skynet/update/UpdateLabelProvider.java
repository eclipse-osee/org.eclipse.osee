/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class UpdateLabelProvider extends LabelProvider implements IStyledLabelProvider {
   @Override
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getImage();
      }
      return SkynetGuiPlugin.getInstance().getImage("laser_16_16.gif");
   }

   @Override
   public StyledString getStyledText(Object element) {
      StyledString styledString = new StyledString();

      if (element instanceof TransferObjects) {
         TransferObjects transferObjects = (TransferObjects)element;
         styledString.append(transferObjects.getArtifact().getDescriptiveName());
         styledString.append(" - "+ transferObjects.getStatus().getMessage(), StyledString.COUNTER_STYLER);
      }
      return styledString;
   }

}
