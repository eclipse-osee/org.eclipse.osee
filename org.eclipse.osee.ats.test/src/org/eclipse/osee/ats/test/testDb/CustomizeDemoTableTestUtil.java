/*
 * Created on Jan 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.customize.dialog.XViewerCustomizeDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author B1375980
 */
public class CustomizeDemoTableTestUtil extends XViewerCustomizeDialog {

   /**
    * @param viewer
    */
   public CustomizeDemoTableTestUtil(XViewer viewer) {
      super(viewer);
   }

   /**
    * for testing purposes - simulate customization view
    */
   public Control createDialogArea(Composite parent) {
      OseeLog.log(AtsPlugin.class, Level.INFO, "===> Simulating CustomizationView " + "\"...");
      return super.createDialogArea(parent);
   }

   /**
    * for testing purposes - simulate the add all button click
    */
   public void handleAddAllItemButtonClick() {
      OseeLog.log(AtsPlugin.class, Level.INFO, "===> Simulating CustomizationView Add All Columns" + "\"...");
      super.handleAddAllItem();
      super.handleLoadConfigCust();
   }
}
