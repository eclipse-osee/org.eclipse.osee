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
package org.eclipse.osee.ote.ui.define.dialogs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.define.jobs.RemoteResourceRequestJob;
import org.eclipse.osee.ote.ui.define.OteDefineImage;
import org.eclipse.osee.ote.ui.define.internal.Activator;
import org.eclipse.osee.ote.ui.define.panels.ListSelectionPanel;
import org.eclipse.osee.ote.ui.define.panels.PreviewPanel;
import org.eclipse.osee.ote.ui.define.panels.PreviewPanel.PanelEnum;
import org.eclipse.osee.ote.ui.define.reports.ExtensionDefinedReports;
import org.eclipse.osee.ote.ui.define.reports.HttpReportRequest;
import org.eclipse.osee.ote.ui.define.reports.ITestRunReport;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFactory;
import org.eclipse.osee.ote.ui.define.reports.output.OutputFormat;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("deprecation")
public class ReportsDialog extends TitleAreaDialog {
   private static final Image MESSAGE_IMAGE = ImageManager.getImage(OteDefineImage.COMMIT_WIZ);
   private static final Image TITLE_BAR_IMAGE = ImageManager.getImage(OteDefineImage.COMMIT);

   private static final String MESSAGE_TITLE = "Select a report";
   private static final String TITLE_BAR_TEXT = "OSEE Test Run Reports";
   private static final String MESSAGE = "The preview window displays an example of the selected report.";
   private static final String REPORT_SELECTION_TITLE = "Select a Report";
   private static final String PREVIEW_TITLE = "Preview";
   private static final String FORMAT_TITLE = "Select a Format";
   private static final String REPORT_DESCRIPTION = "Report Description";
   private static final String PREVIEW_DESCRIPTION = "Enter Preview Size";

   private static final String REPORT_STORE_ID = ReportsDialog.class.getName() + ".reportId";
   private static final String FORMAT_STORE_ID = ReportsDialog.class.getName() + ".formatId";
   private static final String PREVIEW_SIZE_STORE_ID = ReportsDialog.class.getName() + ".previewSize";

   private static final int DEFAULT_PREVIEW_SIZE = 5;

   private PreviewPanel previewPanel;
   private org.eclipse.osee.ote.ui.define.panels.ListSelectionPanel listSelectionPanel;
   private SelectionChangedListener selectionChangedListener;
   private Text descriptionArea;
   private Text previewSizeArea;
   private String selectedReportId;
   private OutputFormat selectedReportFormat;
   private final List<IFile> filesToDelete;
   private Map<OutputFormat, Button> formatButtons;

   public ReportsDialog(Shell parent) {
      super(parent);
      this.filesToDelete = new ArrayList<>();
      setShellStyle(SWT.SHELL_TRIM);
   }

   @Override
   protected Control createButtonBar(Composite parent) {
      Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
      separator.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
      return super.createButtonBar(parent);
   }

   /*
    * @see Dialog#createDialogArea(Composite)
    */
   @Override
   protected Control createDialogArea(Composite parent) {
      Composite content = (Composite) super.createDialogArea(parent);

      Composite composite = new Composite(content, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      SashForm sash = new SashForm(composite, SWT.HORIZONTAL);
      sash.setLayout(new GridLayout());
      sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sash.setFont(parent.getFont());

      createReportSelectionArea(sash);

      Composite panel = new Composite(sash, SWT.NONE);
      GridLayout gL1 = new GridLayout();
      gL1.marginHeight = 0;
      gL1.marginWidth = 0;
      panel.setLayout(gL1);
      panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createFormatAndDescriptionArea(panel);
      createPreviewArea(panel);
      sash.setWeights(new int[] {1, 2});

      setTitle(MESSAGE_TITLE);
      setTitleImage(MESSAGE_IMAGE);
      setMessage(MESSAGE);
      getShell().setText(TITLE_BAR_TEXT);
      getShell().setImage(TITLE_BAR_IMAGE);
      attachListeners();
      restoreDialog();
      return sash;
   }

   private void restoreDialog() {
      IDialogSettings settings = Activator.getInstance().getDialogSettings();
      if (settings != null) {
         String reportSelected = settings.get(REPORT_STORE_ID);
         String format = settings.get(FORMAT_STORE_ID);

         int value = 0;
         try {
            value = settings.getInt(PREVIEW_SIZE_STORE_ID);
         } catch (Exception ex) {
            value = DEFAULT_PREVIEW_SIZE;
         }
         clamp(previewSizeArea, value);

         OutputFormat outputFormat = OutputFormat.HTML;
         int index = 0;
         if (Strings.isValid(reportSelected) && Strings.isValid(format)) {
            Pair<String, String> pair = ExtensionDefinedReports.getInstance().getIdAndName(reportSelected);
            index = listSelectionPanel.indexOf(pair);
            outputFormat = OutputFormat.fromString(format);
         }
         if (index > -1) {
            listSelectionPanel.setSelection(index);
         }
         setReportFormat(outputFormat);
         for (OutputFormat key : formatButtons.keySet()) {
            Button button = formatButtons.get(key);
            button.setSelection(key.equals(outputFormat));
         }
      }
   }

   private void saveDialog() {
      IDialogSettings settings = Activator.getInstance().getDialogSettings();
      if (settings != null) {
         String reportSelected = getReportSelected();
         String format = getReportFormat();
         int previewSize = getPreviewSize();
         settings.put(REPORT_STORE_ID, reportSelected);
         settings.put(FORMAT_STORE_ID, format);
         settings.put(PREVIEW_SIZE_STORE_ID, previewSize);
      }
   }

   @Override
   protected void okPressed() {
      saveDialog();
      cleanUp(filesToDelete);
      super.okPressed();
   }

   private void createReportSelectionArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());
      composite.setText(REPORT_SELECTION_TITLE);

      listSelectionPanel = new ListSelectionPanel(composite, SWT.NONE, 300, 300, new ListLabelProvider());
      listSelectionPanel.setSorter(new ViewerSorter() {

         @Override
         @SuppressWarnings("unchecked")
         public int compare(Viewer viewer, Object o1, Object o2) {
            Pair<String, String> pair1 = (Pair<String, String>) o1;
            Pair<String, String> pair2 = (Pair<String, String>) o2;
            return getComparator().compare(pair1.getSecond(), pair2.getSecond());
         }

      });
      Pair<String, String>[] reportNames = ExtensionDefinedReports.getInstance().getIdsAndNames();
      listSelectionPanel.setInput(reportNames);
      listSelectionPanel.setSelection(0);
   }

   private void createFormatAndDescriptionArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout(2, false);
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      composite.setLayout(gL);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createFormatArea(composite);

      Composite panel = new Composite(composite, SWT.NONE);
      GridLayout gL1 = new GridLayout();
      gL1.marginHeight = 0;
      gL1.marginWidth = 0;
      panel.setLayout(gL1);
      panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createDescriptionArea(panel);
      createPreviewSizeArea(panel);
   }

   private void createPreviewSizeArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setFont(parent.getFont());
      group.setText(PREVIEW_DESCRIPTION);

      previewSizeArea = new Text(group, SWT.SINGLE | SWT.BORDER);
      previewSizeArea.setTextLimit(2);
      GridData gd = new GridData(SWT.RIGHT, SWT.FILL, false, false);
      gd.heightHint = convertHeightInCharsToPixels(1);
      gd.widthHint = convertWidthInCharsToPixels(4);
      previewSizeArea.setLayoutData(gd);
      new Widgets.IntegerTextEntryHandler(previewSizeArea, false, 2) {
         @Override
         public void applyValue(long value) {
            clamp(previewSizeArea, value);
         }
      };

      Label label = new Label(group, SWT.NONE);
      label.setText("[ 0-10 ]");
      label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
   }

   private void clamp(Text text, long value) {
      if (value < 0) {
         previewSizeArea.setText(Integer.toString(0));
      }
      if (value > 10) {
         previewSizeArea.setText(Integer.toString(10));
      }
   }

   private void createDescriptionArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setFont(parent.getFont());
      group.setText(REPORT_DESCRIPTION);

      descriptionArea = new Text(group, SWT.WRAP | SWT.MULTI);
      GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
      gd.heightHint = 100;
      gd.widthHint = 100;
      descriptionArea.setLayoutData(gd);
      descriptionArea.setEditable(false);
   }

   private void createFormatArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
      group.setFont(parent.getFont());
      group.setText(FORMAT_TITLE);

      this.formatButtons = new HashMap<>();
      for (OutputFormat format : OutputFormat.values()) {
         Button button = new Button(group, SWT.RADIO);
         button.setText(format.name());
         button.setData(format);
         if (format.equals(OutputFormat.HTML)) {
            button.setSelection(true);
         }
         button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Button button = (Button) e.getSource();
               if (button.getSelection() != false) {
                  setReportFormat((OutputFormat) button.getData());
               }
            }
         });
         formatButtons.put(format, button);
      }
   }

   private void createPreviewArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      group.setFont(parent.getFont());
      group.setText(PREVIEW_TITLE);
      previewPanel = new PreviewPanel(group, SWT.NONE);
   }

   private void attachListeners() {
      listSelectionPanel.addDoubleClickListener(new IDoubleClickListener() {
         @Override
         @SuppressWarnings("unchecked")
         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            if (selection != null) {
               Pair<String, String> item = (Pair<String, String>) selection.getFirstElement();
               if (item != null) {
                  selectedReportId = item.getFirst();
               }
            }
            okPressed();
         }
      });

      selectionChangedListener = new SelectionChangedListener();
      listSelectionPanel.addSelectionChangedListener(selectionChangedListener);
   }

   public String getReportSelected() {
      return selectedReportId;
   }

   public String getReportFormat() {
      return selectedReportFormat.name();
   }

   private void setReportFormat(OutputFormat reportFormat) {
      this.selectedReportFormat = reportFormat;
      selectionChangedListener.selectionChanged(null);
   }

   private PanelEnum asPanelEnum(OutputFormat format) {
      PanelEnum toReturn = PanelEnum.DEFAULT;
      switch (format) {
         case HTML:
         case PDF:
         case EXCEL:
         case RTF:
            toReturn = PanelEnum.BROWSER;
            break;
         default:
            toReturn = PanelEnum.DEFAULT;
            break;
      }
      return toReturn;
   }

   private void updatePanel(final PanelEnum panelId, final URI uri, final String description, final List<IFile> oldIFiles) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            previewPanel.updatePreview(panelId, uri);
            previewPanel.setDisplay(panelId);
            descriptionArea.setText(description);
         }
      });
      cleanUp(oldIFiles);
   }

   private int getPreviewSize() {
      int toReturn = DEFAULT_PREVIEW_SIZE;
      String value = previewSizeArea.getText();
      try {
         toReturn = Integer.parseInt(value);
      } catch (Exception ex) {
         toReturn = DEFAULT_PREVIEW_SIZE;
         updatePreviewSizeValue(toReturn);
      }
      return toReturn;
   }

   private void updatePreviewSizeValue(final int value) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            previewSizeArea.setText(Integer.toString(value));
         }
      });
   }

   private void generatePreview(final OutputFormat outputFormat, final String reportId, final ITestRunReport report) throws URISyntaxException {
      String urlRequest = HttpReportRequest.getUrl(reportId, outputFormat.name(), "local", getPreviewSize());
      String fileName = OutputFactory.getOutputFilename(outputFormat, reportId);
      switch (outputFormat) {
         case HTML:
            updatePanel(asPanelEnum(outputFormat), new URI(urlRequest), report.getDescription(), filesToDelete);
            break;
         default:
            remoteFileToLocal(outputFormat, urlRequest, fileName, report);
            break;
      }
   }

   private void remoteFileToLocal(final OutputFormat outputFormat, final String urlRequest, final String fileName, final ITestRunReport report) {
      RemoteResourceRequestJob requestJob = new RemoteResourceRequestJob(urlRequest, fileName);
      requestJob.addJobChangeListener(new PreviewUpdateJobChangeListener(report, outputFormat));
      requestJob.getDownloadedFile();
      requestJob.schedule();
   }

   private final class SelectionChangedListener implements ISelectionChangedListener {
      @Override
      @SuppressWarnings("unchecked")
      public void selectionChanged(SelectionChangedEvent event) {
         IStructuredSelection selection = listSelectionPanel.getSelection();
         if (selection != null) {
            Pair<String, String> item = (Pair<String, String>) selection.getFirstElement();
            if (item != null) {
               ITestRunReport report = ExtensionDefinedReports.getInstance().getReportGenerator(item.getFirst());
               if (report != null) {
                  selectedReportId = item.getFirst();
                  try {
                     generatePreview(selectedReportFormat, selectedReportId, report);
                  } catch (Exception ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
      }
   }

   private final class ListLabelProvider extends LabelProvider {

      @SuppressWarnings("unchecked")
      @Override
      public Image getImage(Object element) {
         Image toReturn = null;
         if (element instanceof Pair) {
            toReturn = ExtensionDefinedReports.getInstance().getImage(((Pair<String, String>) element).getFirst());
         }
         return toReturn;
      }

      @SuppressWarnings("unchecked")
      @Override
      public String getText(Object element) {
         if (element instanceof Pair) {
            return ((Pair<String, String>) element).getSecond();
         }
         return super.getText(element);
      }
   }

   private final class PreviewUpdateJobChangeListener extends JobChangeAdapter {
      private final ITestRunReport report;
      private final OutputFormat format;

      private PreviewUpdateJobChangeListener(ITestRunReport report, OutputFormat format) {
         this.report = report;
         this.format = format;
      }

      @Override
      public void done(IJobChangeEvent event) {
         IStatus status = event.getResult();
         if (status.equals(Status.OK_STATUS) || status.getCode() == IStatus.OK) {
            IFile iFile = ((RemoteResourceRequestJob) event.getJob()).getDownloadedFile();
            if (iFile != null) {
               updatePanel(asPanelEnum(format), iFile.getLocationURI(), report.getDescription(), filesToDelete);
               filesToDelete.add(iFile);
            }
         }
      }
   }

   private void cleanUp(final List<IFile> iFiles) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IFile iFile : iFiles) {
               if (iFile != null && iFile.exists()) {
                  try {
                     iFile.delete(true, new NullProgressMonitor());
                     filesToDelete.remove(iFile);
                  } catch (CoreException ex) {
                     // Do Nothing
                  }
               }
            }
         }
      });
   }
}
