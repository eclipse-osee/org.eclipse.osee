/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class ChangeReportEditorInput implements IEditorInput, IPersistableElement {

   private final ChangeUiData changeData;

   public ChangeReportEditorInput(ChangeUiData changeData) {
      this.changeData = changeData;
   }

   @Override
   public boolean exists() {
      return true;
   }

   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.BRANCH_CHANGE);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.BRANCH_CHANGE);
   }

   @Override
   public String getName() {
      String branchName = "Unknown";
      String comment = "";
      if (changeData.isBranchValid()) {
         branchName = changeData.getBranch().getShortName();
      } else if (changeData.isTransactionValid()) {
         TransactionRecord transactionId = changeData.getTransaction();
         try {
            branchName = transactionId.getBranch().getShortName();
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
         }
         if (transactionId.getComment() != null) {
            comment = String.format(" - %s", transactionId.getComment());
         }
      }
      return String.format("Change Report: %s%s", branchName, comment);
   }

   @Override
   public IPersistableElement getPersistable() {
      try {
         if (EditorsPreferencePage.isCloseChangeReportEditorsOnShutdown()) {
            return null;
         } else {
            return this;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   @Override
   public String getToolTipText() {
      return getName();
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      return null;
   }

   public ChangeUiData getChangeData() {
      return changeData;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof ChangeReportEditorInput) {
         return changeData.equals(((ChangeReportEditorInput) obj).getChangeData());
      }
      return false;
   }

   @Override
   public String getFactoryId() {
      return ChangeReportEditorInputFactory.ID;
   }

   @Override
   public void saveState(IMemento memento) {
      ChangeReportEditorInputFactory.saveState(memento, this);
   }

}
