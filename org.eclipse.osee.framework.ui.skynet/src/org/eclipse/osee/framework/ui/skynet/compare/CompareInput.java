/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.compare;

import java.lang.reflect.Field;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author b1565043
 */
public class CompareInput extends CompareEditorInput {
   private static final String CONFIRM_SAVE_PROPERTY = "org.eclipse.compare.internal.CONFIRM_SAVE_PROPERTY";
   private Object differences;
   private CompareItem left;
   private CompareItem right;

   /*
    * 
    * 
    * 
    *             
            CompareConfiguration compareConfiguration =  new CompareConfiguration();
            compareConfiguration.setLeftEditable(true);
            compareConfiguration.setRightEditable(false);
                 CompareUI.openCompareEditor(new CompareInput(compareConfiguration));
                 
                 
            
    */
   public CompareInput(CompareConfiguration compareConfiguration) {
      super(compareConfiguration);

      getCompareConfiguration().setProperty(CONFIRM_SAVE_PROPERTY, Boolean.FALSE);
   }

   protected Object prepareInput(IProgressMonitor pm) {
      initTitle();

      CompareItem ancestor = new CompareItem("Common", "contents", System.currentTimeMillis());
      left = new CompareItem("Left", "new contents 23\nhi\nbye", System.currentTimeMillis());
      right = new CompareItem("Right", "old contents 21\nhi\ntry", System.currentTimeMillis());
      Differencer differencer = new Differencer();
      differences = differencer.findDifferences(true, pm, null, ancestor, left, right);
      //      MergeNode mergeNode = new MergeNode(null, Differencer.CONFLICTING, 
      //            ancestor, left, right);
      return differences;
   }

   private void initTitle() {
      CompareConfiguration cc = getCompareConfiguration();
      String nameLeft = "Left Object";
      String nameRight = "Right Object";
      if (nameLeft.equals(nameRight)) {
         nameLeft = "Left object";
         nameRight = "Right Object";
      }

      cc.setLeftLabel(nameLeft);
//      cc.setLeftImage(Artifact.getOverrideImage());

      cc.setRightLabel(nameRight);
//      cc.setRightImage(Artifact.getOverrideImage());

//      cc.setLeftEditable(true);
//      cc.setRightEditable(true);
      setTitle("Compare (" + nameLeft + " - " + nameRight + ")");
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.CompareEditorInput#isEditionSelectionDialog()
    */
   @Override
   public boolean isEditionSelectionDialog() {
      return super.isEditionSelectionDialog();
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.CompareEditorInput#isSaveNeeded()
    */
   @Override
   public boolean isSaveNeeded() {
      return super.isSaveNeeded();
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.CompareEditorInput#saveChanges(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void saveChanges(IProgressMonitor monitor) throws CoreException {
      super.saveChanges(monitor);
      if (differences instanceof DiffNode) {
         try {
            //              boolean result = commit(monitor, (DiffNode) differences);
            // let the UI re-compare here on changed inputs
            if (true) {
            }
         } finally {
            setDirty(false);
         }
      }
   }



   public CompareViewerSwitchingPane getInputPane() {
      try {
         Field field = CompareEditorInput.class.getDeclaredField("fContentInputPane");
         field.setAccessible(true);
         Object object = field.get(this);
         if (object instanceof CompareViewerSwitchingPane) {
            return (CompareViewerSwitchingPane) object;
         }
      } catch (Throwable e) {
         // ignore
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.CompareEditorInput#isDirty()
    */
   @Override
   public boolean isDirty() {
      return super.isDirty();
   }

   /* (non-Javadoc)
    * @see org.eclipse.compare.CompareEditorInput#save(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void save(IProgressMonitor pm) {
      super.save(pm);
   }
}