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
package org.eclipse.osee.framework.ui.skynet.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.ImageCapture;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultsComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditor extends AbstractArtifactEditor implements IDirtiableEditor, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.results.ResultsEditor";
   private int chartPageIndex, reportsPageIndex;
   private Integer startPage = null;
   private XResultsComposite xResultComposite;
   private Composite chartComposite;
   private Canvas chartCanvas;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IResultsEditorProvider provider = getResultsEditorProvider();

         createChartTab();
         createReportTab();

         setPartName(provider.getName());
         setActivePage(startPage);

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private ToolBar createToolBar(Composite parent) {
      ToolBar toolBar = ALayout.createCommonToolBar(parent);
      ToolItem item;

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "ATS Results");

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("print.gif"));
      item.setToolTipText("Print this tab");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            if (getCurrentPage() == reportsPageIndex) {
               xResultComposite.getBrowser().setUrl("javascript:print()");
            } else if (getCurrentPage() == chartPageIndex) {
               ImageCapture iCapture = new ImageCapture(chartCanvas);
               iCapture.popupDialog();
            }
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      return toolBar;
   }

   public void setEditorTitle(final String str) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            setPartName(str);
            firePropertyChange(IWorkbenchPart.PROP_TITLE);
         }
      });
   }

   public IResultsEditorProvider getResultsEditorProvider() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof ResultsEditorInput)) {
         throw new IllegalArgumentException("Editor Input not WorldEditorInput");
      }
      ResultsEditorInput worldEditorInput = (ResultsEditorInput) editorInput;
      return worldEditorInput.getIWorldEditorProvider();
   }

   private void createChartTab() throws OseeCoreException {
      chartComposite = ALayout.createCommonPageComposite(getContainer());
      createToolBar(chartComposite);
      Chart chart = getResultsEditorProvider().getChart();

      GridData gd = new GridData(GridData.FILL_BOTH);
      if (chart == null) {
         Label label = new Label(chartComposite, SWT.BORDER);
         label.setText("\n   No Chart Provided");
      } else {
         chartCanvas = new Canvas(chartComposite, SWT.NONE);
         chartCanvas.setLayoutData(gd);
         chartCanvas.addPaintListener(new ChartViewerSWT(chart));
      }

      chartPageIndex = addPage(chartComposite);
      if (chart != null) {
         startPage = chartPageIndex;
      }
      setPageText(chartPageIndex, "Chart");
   }

   private void createReportTab() throws OseeCoreException, PartInitException {
      Composite comp = ALayout.createCommonPageComposite(getContainer());
      createToolBar(comp);

      GridData gd = new GridData(GridData.FILL_BOTH);
      xResultComposite = new XResultsComposite(comp, SWT.BORDER);
      xResultComposite.setLayoutData(gd);
      if (getResultsEditorProvider().getReportHtml() != null) {
         xResultComposite.setHtmlText(getResultsEditorProvider().getReportHtml());
      }

      reportsPageIndex = addPage(comp);
      if (startPage == null) {
         startPage = reportsPageIndex;
      }
      setPageText(reportsPageIndex, "Report");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   public void refreshTitle() {
      firePropertyChange(IWorkbenchPart.PROP_TITLE);
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.ats.IActionable#getActionDescription()
    */
   @Override
   public String getActionDescription() {
      return null;
   }

   public static void open(final IResultsEditorProvider provider) throws OseeCoreException {
      open(provider, false);
   }

   public static void open(final IResultsEditorProvider provider, boolean forcePend) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new ResultsEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      }, forcePend);
   }

   public void closeEditor() {
      final MultiPageEditorPart editor = this;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   public static Collection<ResultsEditor> getEditors() {
      final List<ResultsEditor> editors = new ArrayList<ResultsEditor>();
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((ResultsEditor) editor.getEditor(false));
            }
         }
      }, true);
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor((editor.getEditor(false)), false);
            }
         }
      });
   }

}
