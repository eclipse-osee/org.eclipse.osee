/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.define.jobs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.artifacts.TestRunOperator;
import org.eclipse.osee.ote.ui.define.OteUiDefinePlugin;
import org.eclipse.osee.ote.ui.define.dialogs.CommitDialog;
import org.eclipse.osee.ote.ui.define.dialogs.OverrideInvalidScriptRevisions;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
class CommitJobDialog extends UIJob {
   private static final Image CHILD_BRANCH_IMAGE = OteUiDefinePlugin.getInstance().getImage("childBranch.gif");

   private static String JOB_NAME = "Commit Test Run";
   private String message;
   private Object[] items;

   private Artifact[] allItems;
   private Artifact[] preSelected;
   private Artifact[] unselectable;
   private boolean isOverrideAllowed;

   public CommitJobDialog(Artifact[] allitems, Artifact[] preSelected, boolean isOverrideAllowed) {
      this(allitems, preSelected, null, isOverrideAllowed);
   }

   public CommitJobDialog(Artifact[] allitems, Artifact[] preSelected, Artifact[] unselectable, boolean isOverrideAllowed) {
      super(JOB_NAME);
      this.allItems = allitems;
      this.preSelected = preSelected;
      this.unselectable = unselectable;
      setUser(false);
      setPriority(Job.LONG);
      if (unselectable == null) {
         unselectable = new Artifact[0];
      }
      this.isOverrideAllowed = isOverrideAllowed;
      this.message = null;
      this.items = null;
   }

   public String getMessage() {
      return message;
   }

   public Object[] getSelected() {
      return items;
   }

   public IStatus runInUIThread(IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      monitor.setTaskName(getName());
      Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
      CommitDialog dialog = new CommitDialog(shell, CommitColumnEnum.toStringArray(), new TestRunTableLabelProvider());
      dialog.setBlockOnOpen(true);
      dialog.setInput(allItems);
      dialog.setUnSelectable(unselectable);
      dialog.setSelected(preSelected);
      if (isOverrideAllowed != false) {
         dialog.setOverrideHandler(new OverrideInvalidScriptRevisions());
      }
      int result = dialog.open();
      if (result == Window.OK) {
         message = dialog.getComments();
         items = dialog.getSelectedResources();
         toReturn = Status.OK_STATUS;
      }
      return toReturn;
   }

   private enum CommitColumnEnum {

      Branch, Name, Id, Outfile;

      public static String[] toStringArray() {

         CommitColumnEnum[] cols = CommitColumnEnum.values();
         String[] toReturn = new String[cols.length];
         for (int index = 0; index < cols.length; index++) {
            toReturn[index] = cols[index].name();
         }
         return toReturn;
      }
   }

   private final class TestRunTableLabelProvider extends BaseLabelProvider implements ITableLabelProvider {
      private HashSet<Object> unselectableItems = new HashSet<Object>();
      private final int DUMMY_COLUMNS = 1;

      public TestRunTableLabelProvider() {
         if (unselectable != null) {
            this.unselectableItems.addAll(Arrays.asList(unselectable));
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
       */
      public Image getColumnImage(Object element, int columnIndex) {
         Image toReturn = null;
         if (columnIndex >= DUMMY_COLUMNS) {
            CommitColumnEnum column = CommitColumnEnum.values()[columnIndex - DUMMY_COLUMNS];
            switch (column) {
               case Branch:
                  toReturn = CHILD_BRANCH_IMAGE;
                  break;
               default:
                  break;
            }
         }
         return toReturn;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
       */
      public String getColumnText(Object element, int columnIndex) {
         String toReturn = "";
         if (columnIndex >= DUMMY_COLUMNS) {
            if (element instanceof Artifact) {
               Artifact artifact = (Artifact) element;
               if (artifact != null && artifact.isDeleted() != true) {
                  CommitColumnEnum column = CommitColumnEnum.values()[columnIndex - DUMMY_COLUMNS];
                  switch (column) {
                     case Branch:
                        toReturn = artifact.getBranch().getBranchName();
                        break;
                     case Id:
                        try {
                           toReturn = new TestRunOperator(artifact).getChecksum();
                        } catch (Exception ex) {
                           OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, "Error getting Checksum", ex);
                        }
                        break;
                     case Name:
                        toReturn = artifact.getDescriptiveName();
                        break;
                     case Outfile:
                        try {
                           toReturn = new TestRunOperator(artifact).getOutfileAttribute().getDisplayableString();
                        } catch (Exception ex) {
                           OseeLog.log(OteUiDefinePlugin.class, Level.SEVERE, "Error getting Outfile", ex);
                        }
                        break;
                     default:
                        break;
                  }
               }
            }
         }
         return toReturn;
      }
   }
}