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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.IOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public abstract class DatabaseHealthOperation extends AbstractOperation {

   private boolean isFixOperationEnabled;
   private boolean isShowDetailsEnabled;
   private int itemToFixCount;

   private Appendable appendableBuffer;
   private final Appendable detailedReport;

   public DatabaseHealthOperation(String operationName) {
      super(operationName, SkynetGuiPlugin.PLUGIN_ID);
      this.isFixOperationEnabled = false;
      this.isShowDetailsEnabled = false;
      this.appendableBuffer = new StringBuilder();
      this.detailedReport = new StringBuilder();
      this.itemToFixCount = 0;
   }

   @Override
   public String getName() {
      return isFixOperationEnabled() ? getFixTaskName() : getVerifyTaskName();
   }

   public String getVerifyTaskName() {
      return String.format("Check for %s", super.getName());
   }

   public String getFixTaskName() {
      return String.format("Fix %s", super.getName());
   }

   public void setFixOperationEnabled(boolean isFixOperationEnabled) {
      this.isFixOperationEnabled = isFixOperationEnabled;
   }

   public boolean isFixOperationEnabled() {
      return isFixOperationEnabled;
   }

   public void setShowDetailsEnabled(boolean isShowDetailsEnabled) {
      this.isShowDetailsEnabled = isShowDetailsEnabled;
   }

   public boolean isShowDetailsEnabled() {
      return isShowDetailsEnabled;
   }

   public void setSummary(Appendable appendableBuffer) {
      this.appendableBuffer = appendableBuffer;
   }

   public Appendable getSummary() {
      return appendableBuffer;
   }

   public void appendToDetails(String value) throws IOException {
      getDetailedReport().append(value);
   }

   public Appendable getDetailedReport() {
      return detailedReport;
   }

   protected void setItemsToFix(int value) {
      this.itemToFixCount = value;
   }

   public boolean hadItemsToFix() {
      return getItemsToFixCount() > 0;
   }

   public int getItemsToFixCount() {
      return itemToFixCount;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected final void doWork(IProgressMonitor monitor) throws Exception {
      setItemsToFix(0);
      //      monitor.beginTask(isFixOperationEnabled() ? getFixTaskName() : getVerifyTaskName(), getTotalWorkUnits());
      //      try {
      doHealthCheck(monitor);
      //      } finally {
      //         monitor.done();
      //      }
   }

   protected abstract void doHealthCheck(IProgressMonitor monitor) throws Exception;
}
