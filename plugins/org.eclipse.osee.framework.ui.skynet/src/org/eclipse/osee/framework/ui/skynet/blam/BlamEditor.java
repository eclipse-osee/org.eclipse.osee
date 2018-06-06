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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
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
      OseeStatusContributionItemFactory.addTo(this, true);
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getImage());
      try {
         overviewPage = new BlamOverviewPage(this);
         addPage(overviewPage);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private VariableMap getBlamVariableMap() {
      return overviewPage.getInput();
   }

   public void executeBlam() {
      OperationLogger reporter = overviewPage.getReporter();
      getEditorInput().getBlamOperation().execute(reporter, getBlamVariableMap(), new BlamEditorExecutionAdapter());
   }

   public static void edit(final BlamEditorInput blamEditorInput) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(blamEditorInput, EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static void edit(AbstractBlam blamOperation) {
      BlamEditor.edit(new BlamEditorInput(blamOperation));
   }

   private final class BlamEditorExecutionAdapter extends JobChangeAdapter {
      private long startTime = 0;

      @Override
      public void scheduled(IJobChangeEvent event) {
         getActionBarContributor().getExecuteBlamAction().setEnabled(false);
         showBusy(true);
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         startTime = System.currentTimeMillis();
         Date date = new Date();
         overviewPage.setOuputText(String.format("Starting BLAM at [%s]\n", date.toString()));
      }

      @Override
      public void done(IJobChangeEvent event) {
         overviewPage.appendOutput(String.format("BLAM completed in [%s]\n", Lib.getElapseString(startTime)));
         showBusy(false);
         getActionBarContributor().getExecuteBlamAction().setEnabled(true);
         overviewPage.refreshTextSize();
      }
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @Override
   public boolean isSaveAsAllowed() {
      // do nothing
      return false;
   }

   @Override
   public void onDirtied() {
      // do nothing
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
   }

   public String getButtonText() {
      return getEditorInput().getBlamOperation().getRunText();
   }

   public static Collection<BlamEditor> getEditors() {
      final List<BlamEditor> editors = new ArrayList<>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((BlamEditor) editor.getEditor(false));
            }
         }
      });
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor(editor.getEditor(false), false);
            }
         }
      });
   }
}