/*
 * Created on Feb 4, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.io.File;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class XFileSelectionDialog extends XText {

   private String defaultFileSelection;

   public XFileSelectionDialog() {
      super();
   }

   /**
    * @param displayLabel
    */
   public XFileSelectionDialog(String displayLabel) {
      super(displayLabel);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int, boolean)
    */
   @Override
   public void createWidgets(final Composite parent, int horizontalSpan, boolean fillText) {
      super.createWidgets(parent, horizontalSpan, fillText);

      Button fileDialog = new Button(parent, SWT.NONE);
      fileDialog.setImage(SkynetGuiPlugin.getInstance().getImage("file.gif"));
      fileDialog.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.*"});
            String defaultDir = getDefaultFileSelection();
            File dir = new File(defaultDir != null ? defaultDir : "");
            if (dir.isFile() || dir.isDirectory())
               dialog.setFilterPath(defaultDir);
            else
               dialog.setFilterPath("c:\\");

            String result = dialog.open();
            if (result != null && !result.equals("")) {
               setText(dialog.getFilterPath() + File.separatorChar + dialog.getFileName());
            }
         }
      });
   }

   /**
    * @return the defaultFileSelection
    */
   public String getDefaultFileSelection() {
      return defaultFileSelection;
   }

   /**
    * @param defaultFileSelection the defaultFileSelection to set
    */
   public void setDefaultFileSelection(String defaultFileSelection) {
      this.defaultFileSelection = defaultFileSelection;
   }

}
