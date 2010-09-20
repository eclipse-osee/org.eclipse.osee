package org.eclipse.osee.coverage.util;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.coverage.model.WorkProductAction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.cm.IOseeCmService.ImageType;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

public class WorkProductActionLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      if (arg0 instanceof WorkProductAction) {
         return ImageManager.getImage(SkynetGuiPlugin.getInstance().getOseeCmService().getImage(ImageType.Pcr));
      } else {
         return ImageManager.getImage(SkynetGuiPlugin.getInstance().getOseeCmService().getImage(ImageType.Task));
      }
   }

   @Override
   public String getText(Object arg0) {
      if (arg0 instanceof WorkProductAction) {
         return String.format("%s - %d Tasks", arg0.toString(), ((WorkProductAction) arg0).getTasks().size());
      }
      return arg0.toString();
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