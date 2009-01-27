/*
 * Created on Jan 23, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.html;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class ResultsEditorHtmlTab implements IResultsEditorHtmlTab {

   private final String tabName;
   private XResultsComposite xResultsComposite;
   private static String HELP_CONTEXT_ID = "xResultView";
   private ResultsEditor resultsEditor;
   private final XResultPage xResultPage;
   private Label infoLabel;

   public ResultsEditorHtmlTab(XResultPage xResultPage) {
      this.xResultPage = xResultPage;
      tabName = "Results";
   }

   public ResultsEditorHtmlTab(String title, String tabName, String html) {
      this.tabName = tabName;
      xResultPage = new XResultPage(title, html);
      org.eclipse.core.runtime.Assert.isNotNull(tabName);
      org.eclipse.core.runtime.Assert.isNotNull(html);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorHtmlTab#getReportHtml()
    */
   @Override
   public String getReportHtml() throws OseeCoreException {
      return xResultPage.getManipulatedHtml();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab#getTabName()
    */
   @Override
   public String getTabName() {
      return tabName;
   }

   public Composite createTab(Composite parent, ResultsEditor resultsEditor) throws OseeCoreException {
      this.resultsEditor = resultsEditor;

      Composite comp = ALayout.createCommonPageComposite(parent);
      ToolBar toolBar = resultsEditor.createToolBar(comp);
      createToolbar(toolBar);

      infoLabel = new Label(comp, SWT.NONE);
      StringBuffer sb = new StringBuffer();
      sb.append(String.format("Errors: %s    Warnings: %s", xResultPage.getNumErrors(), xResultPage.getNumWarnings()));
      infoLabel.setText(sb.toString());
      infoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      GridData gd = new GridData(GridData.FILL_BOTH);
      xResultsComposite = new XResultsComposite(comp, SWT.BORDER);
      xResultsComposite.setLayoutData(gd);
      xResultsComposite.setHtmlText(xResultPage.getManipulatedHtml());

      SkynetGuiPlugin.getInstance().setHelp(xResultsComposite, HELP_CONTEXT_ID);
      SkynetGuiPlugin.getInstance().setHelp(xResultsComposite.getBrowser(), HELP_CONTEXT_ID);
      return comp;
   }

   private void createToolbar(ToolBar toolBar) {
      ToolItem item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("print.gif"));
      item.setToolTipText("Print this tab");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultsComposite.getBrowser().setUrl("javascript:print()");
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("email.gif"));
      item.setToolTipText("Email");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            Set<Manipulations> manipulations = new HashSet<Manipulations>();
            manipulations.add(Manipulations.ALL);
            manipulations.add(Manipulations.ERROR_WARNING_HEADER);
            Dialogs.emailDialog(resultsEditor.getTitle(), xResultPage.getManipulatedHtml(manipulations));
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("export.gif"));
      item.setToolTipText("Export Table");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultPage.handleExport();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("save.gif"));
      item.setToolTipText("Save Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            xResultPage.saveToFile();
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("load.gif"));
      item.setToolTipText("Import Saved Results Report");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            try {
               final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.OPEN);
               dialog.setFilterExtensions(new String[] {"*.html"});
               String filename = dialog.open();
               if (filename == null || filename.equals("")) return;
               String html = AFile.readFile(filename);
               if (html == null) throw new IllegalStateException("Can't load file");
               if (html.equals("")) throw new IllegalStateException("Empty file");
               resultsEditor.addResultsTab(new ResultsEditorHtmlTab(new XResultPage(filename, html,
                     Manipulations.RAW_HTML)));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

}
