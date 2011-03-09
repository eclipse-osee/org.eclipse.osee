package org.eclipse.osee.coverage.util.dialog;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.coverage.model.CoverageOption;
import org.eclipse.swt.graphics.Image;

public class CoverageMethodLabelProvider implements ILabelProvider {

   @Override
   public Image getImage(Object element) {
      return null;
   }

   @Override
   public String getText(Object element) {
      if (element instanceof CoverageOption) {
         CoverageOption option = (CoverageOption) element;
         return option.getName();
      }
      return "Unknown";
   }

   @Override
   public void addListener(ILabelProviderListener listener) {
      // do nothing
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   @Override
   public void removeListener(ILabelProviderListener listener) {
      // do nothing
   }

}
