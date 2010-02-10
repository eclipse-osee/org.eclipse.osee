/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.ICoverageImportRecordProvider;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class SaveImportRecordAction extends Action {
   private final ISaveable saveable;
   private final ICoverageImportRecordProvider coverageImportRecordProvider;

   public SaveImportRecordAction(ICoverageImportRecordProvider coverageImportRecordProvider, ISaveable saveable) {
      super("Save Coverage Import Record");
      this.coverageImportRecordProvider = coverageImportRecordProvider;
      this.saveable = saveable;
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.REPORT);
   }

   @Override
   public void run() {
      Result result = saveable.isEditable();
      if (result.isFalse()) {
         result.popup();
         return;
      }
      if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Save Coverage Import Record",
            "Overwrite coverage import record with current import information?")) {
         try {
            SkynetTransaction transaction =
                  new SkynetTransaction(CoverageUtil.getBranch(), "Coverage - Save Import Record");
            saveable.saveImportRecord(transaction, coverageImportRecordProvider);
            transaction.execute();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            return;
         }
      }
      try {
         saveable.save();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return;
      }
   }
}
