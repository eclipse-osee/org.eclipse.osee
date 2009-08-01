/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Jeff C. Phillips
 */
public class CompareInput extends CompareEditorInput {
   private static final String CONFIRM_SAVE_PROPERTY = "org.eclipse.compare.internal.CONFIRM_SAVE_PROPERTY";
   private CompareItem leftCompareItem;
   private CompareItem rightCompareItem;
   private CompareItem parentCompareItem;
   private Object differences;

   public CompareInput(CompareConfiguration compareConfiguration, CompareItem leftCompareItem, CompareItem rightCompareItem, CompareItem parentCompareItem) {
      super(compareConfiguration);

      this.leftCompareItem = leftCompareItem;
      this.rightCompareItem = rightCompareItem;
      this.parentCompareItem = parentCompareItem;

      getCompareConfiguration().setProperty(CONFIRM_SAVE_PROPERTY, Boolean.TRUE);
      getCompareConfiguration().setProperty(CompareConfiguration.USE_OUTLINE_VIEW, Boolean.TRUE);
   }

   protected Object prepareInput(IProgressMonitor pm) {
      initTitle();

      Differencer differencer = new Differencer();
      differences =
            differencer.findDifferences(parentCompareItem != null, pm, null, parentCompareItem, leftCompareItem,
                  rightCompareItem);
      return differences;
   }

   private void initTitle() {
      CompareConfiguration configuration = getCompareConfiguration();
      String nameLeft = leftCompareItem.getName();
      String nameRight = rightCompareItem.getName();

      configuration.setLeftLabel(nameLeft);
      configuration.setLeftImage(leftCompareItem.getImage());

      configuration.setRightLabel(nameRight);
      configuration.setRightImage(rightCompareItem.getImage());
      setTitle("Compare (" + nameLeft + " - " + nameRight + ")");
   }

   @Override
   public void saveChanges(IProgressMonitor monitor) throws CoreException {
      super.saveChanges(monitor);
      leftCompareItem.persistContent();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CompareInput) {
         return super.equals(obj);
      }
      return false;
   }
}