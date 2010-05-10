/*
 * Created on Apr 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.change;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
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
      return ImageManager.getImage(changeData.getCompareType().getHandler().getActionImage());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(changeData.getCompareType().getHandler().getActionImage());
   }

   @Override
   public String getName() {
      return String.format("Change Report: " + changeData.getCompareType().getHandler().getName(changeData.getTxDelta()));
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
   public boolean equals(Object object) {
      boolean result = false;
      if (object instanceof ChangeReportEditorInput) {
         ChangeReportEditorInput other = (ChangeReportEditorInput) object;
         result = this.getChangeData().equals(other.getChangeData());
      }
      return result;
   }

   @Override
   public int hashCode() {
      return changeData.hashCode();
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
