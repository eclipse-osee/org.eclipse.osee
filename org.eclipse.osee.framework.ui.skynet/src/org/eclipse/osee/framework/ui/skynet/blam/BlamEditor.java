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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Ryan D. Brooks
 */
public class BlamEditor extends FormEditor implements IDirtiableEditor {
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

   @Override
   public BlamEditorInput getEditorInput() {
      return (BlamEditorInput) super.getEditorInput();
   }

   @Override
   public void showBusy(boolean busy) {
      super.showBusy(busy);
      if (overviewPage != null) {
         overviewPage.showBusy(busy);
      }
   }

   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());
      try {
         overviewPage = new BlamOverviewPage(this);
         addPage(overviewPage);
      } catch (PartInitException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

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
      getEditorInput().getBlamOperation().execute(getPartName(), overviewPage.getOutput(), getBlamVariableMap(),
            new BlamEditorExecutionAdapter());
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

   public static void edit(AbstractBlam blamOperation) throws OseeCoreException {
      BlamEditor.edit(new BlamEditorInput(blamOperation));
   }

   private final class BlamEditorExecutionAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         super.scheduled(event);
         getActionBarContributor().getExecuteBlamAction().setEnabled(false);
         showBusy(true);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
         overviewPage.setOuputText(String.format("Starting BLAM at [%s]\n", Lib.getElapseString(startTime)));
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         overviewPage.appendOutput(String.format("BLAM completed in [%s]\n", Lib.getElapseString(startTime)));
         showBusy(false);
         getActionBarContributor().getExecuteBlamAction().setEnabled(true);
      }
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   @Override
   public void doSaveAs() {
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void onDirtied() {
   }
}