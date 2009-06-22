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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.ui.PartInitException;

/**
 * @author Ryan D. Brooks
 */
public class BlamEditor extends AbstractArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.blam.BlamEditor";

   private BlamEditorActionBarContributor actionBarContributor;
   private BlamOverviewPage overviewPage;

   public BlamEditor() {
      super();
   }

   public BlamEditorActionBarContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new BlamEditorActionBarContributor(this);
      }
      return actionBarContributor;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#getEditorInput()
    */
   @Override
   public BlamEditorInput getEditorInput() {
      return (BlamEditorInput) super.getEditorInput();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#showBusy(boolean)
    */
   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (overviewPage != null) {
         overviewPage.showBusy(busy);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());
      try {
         overviewPage = new BlamOverviewPage(this);
         addPage(overviewPage);
         addPage(new WorkflowDataPage(this, overviewPage));
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == IActionable.class) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               return "";
            }
         };
      }
      return super.getAdapter(adapter);
   }

   private VariableMap getBlamVariableMap() {
      return overviewPage.getInput();
   }

   public void executeBlam() {
      try {
         final List<BlamOperation> operations = new ArrayList<BlamOperation>();
         operations.addAll(getEditorInput().getArtifact().getOperations());
         IOperation blamOperation =
               new ExecuteBlamOperation(getPartName(), overviewPage.getOutput(), getBlamVariableMap(), operations);
         Operations.executeAsJob(blamOperation, true, Job.LONG, new BlamEditorExecutionAdapter());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void edit(BlamWorkflow blamWorkflow) {
      BlamEditor.edit(new BlamEditorInput(blamWorkflow));
   }

   public static void edit(final BlamEditorInput blamEditorInput) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(blamEditorInput, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static void edit(BlamOperation blamOperation) throws OseeCoreException {
      BlamEditor.edit(new BlamEditorInput(blamOperation));
   }

   private final class BlamEditorExecutionAdapter extends JobChangeAdapter {
      private long startTime = 0;

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
       */
      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         getActionBarContributor().getExecuteBlamAction().setEnabled(false);
         showBusy(true);
      }

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
       */
      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
         overviewPage.setOuputText(String.format("Starting BLAM at [%s]\n", Lib.getElapseString(startTime)));
      }

      /* (non-Javadoc)
       * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
       */
      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         overviewPage.appendOutput(String.format("BLAM completed in [%s]\n", Lib.getElapseString(startTime)));
         showBusy(false);
         getActionBarContributor().getExecuteBlamAction().setEnabled(true);
      }
   }
}