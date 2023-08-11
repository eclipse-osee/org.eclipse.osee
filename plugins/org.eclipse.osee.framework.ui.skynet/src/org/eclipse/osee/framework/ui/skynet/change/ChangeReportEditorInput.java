/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.change;

import java.util.logging.Level;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

public class ChangeReportEditorInput implements IEditorInput, IPersistableElement, Adaptable {

   private final ChangeUiData changeData;
   private BranchId branch;
   private boolean transactionTabActive = false;
   private boolean notLoaded = false;
   private Integer numTransactions;

   public ChangeReportEditorInput(ChangeUiData changeData) {
      this(changeData, null);
   }

   public ChangeReportEditorInput(ChangeUiData changeData, BranchId branch) {
      this.changeData = changeData;
      this.branch = branch;
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

   public String getTitle() {
      return changeData.getCompareType().getHandler().getName(changeData.getTxDelta());
   }

   @Override
   public String getName() {
      return String.format("Change Report: %s", getTitle());
   }

   public String getBranchTransactionName() {
      return String.format("Branch Transactions: %s", getTitle());
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
         OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   @Override
   public String getToolTipText() {
      return getTitle();
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

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public boolean isTransactionTabActive() {
      return transactionTabActive;
   }

   public void setTransactionTabActive(boolean transactionTabActive) {
      this.transactionTabActive = transactionTabActive;
   }

   public boolean isNotLoaded() {
      return notLoaded;
   }

   public void setNotLoaded(boolean notLoaded) {
      this.notLoaded = notLoaded;
   }

   public Image getBranchTransactionImage() {
      return ImageManager.getImage(FrameworkImage.DB_ICON_BLUE);
   }

   public void setNumTransactions(Integer numTransactions) {
      this.numTransactions = numTransactions;
   }

   public Integer getNumTransactions() {
      return numTransactions;
   }

}
