package org.eclipse.osee.ats.workflow;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.swt.graphics.Image;

public class ChangeTypeLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object arg0) {
      return ChangeTypeToSwtImage.getImage((ChangeType) arg0);
   }

   @Override
   public String getText(Object arg0) {
      return ((ChangeType) arg0).name();
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
