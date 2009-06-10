/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.conflict.AnyeditCompareInput.ExclusiveJobRule;
import org.eclipse.ui.progress.UIJob;

/**
 * @author b1565043
 */
public class CompareInput extends CompareEditorInput {
   private static final String CONFIRM_SAVE_PROPERTY = "org.eclipse.compare.internal.CONFIRM_SAVE_PROPERTY";
   private Object differences;
   private boolean createNoDiffNode;
   private CompareItem left;
   private CompareItem right;

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
      cc.setLeftImage(Artifact.getOverrideImage());

      cc.setRightLabel(nameRight);
      cc.setRightImage(Artifact.getOverrideImage());

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
               reuseEditor();
            }
         } finally {
            setDirty(false);
         }
      }
   }

   void reuseEditor() {
      UIJob job = new UIJob("AnyEdit: re-comparing editor selection") {
         public IStatus runInUIThread(IProgressMonitor monitor) {
            if (monitor.isCanceled()) {// || left.isDisposed() || right.isDisposed()){
               return Status.CANCEL_STATUS;
            }

            // This causes too much flicker:
            //              AnyeditCompareInput input = new AnyeditCompareInput(left.recreate(), right
            //                      .recreate());
            //              if(monitor.isCanceled()){
            //                  input.internalDispose();
            //                  return Status.CANCEL_STATUS;
            //              }
            //              CompareUI.reuseCompareEditor(input, (IReusableEditor) getWorkbenchPart());

            CompareInput input = CompareInput.this;
            // allow "no diff" result to keep the editor open
            createNoDiffNode = true;
            try {
               CompareItem old_left = left;
               //                  left = old_left.recreate();
               //                  old_left.dispose();
               CompareItem old_right = right;
               //                  right = old_right.recreate();
               //                  old_right.dispose();

               // calls prepareInput(monitor);
               input.run(monitor);
               if (differences != null) {
                  CompareViewerSwitchingPane pane = getInputPane();
                  if (pane != null) {
                     Viewer viewer = pane.getViewer();
                     if (viewer instanceof TextMergeViewer) {
                        viewer.setInput(differences);
                     }
                  }
               }
            } catch (InterruptedException e) {
               // ignore, we are interrupted
               return Status.CANCEL_STATUS;
            } catch (InvocationTargetException e) {
               return Status.CANCEL_STATUS;
            } finally {
               createNoDiffNode = false;
            }
            return Status.OK_STATUS;
         }

         public boolean belongsTo(Object family) {
            return CompareInput.this == family;
         }

      };
      job.setPriority(Job.SHORT);
      job.setUser(true);
      job.setRule(new ExclusiveJobRule(this));
      Job[] jobs = Job.getJobManager().find(this);
      if (jobs.length > 0) {
         for (int i = 0; i < jobs.length; i++) {
            jobs[i].cancel();
         }
      }
      jobs = Job.getJobManager().find(this);
      if (jobs.length > 0) {
         job.schedule(1000);
      } else {
         job.schedule(500);
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