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
package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.Date;
import org.eclipse.osee.framework.skynet.core.revision.ChangeReportInput;

/**
 * @author Robert A. Fisher
 */
public class SnapshotDescription {
   private final ChangeReportInput oldInput;
   private final ChangeReportInput newInput;
   private final Date changeTime;

   /**
    * @param oldInput
    * @param newInput
    * @param changeTime
    */
   public SnapshotDescription(ChangeReportInput oldInput, ChangeReportInput newInput, Date changeTime) {
      this.oldInput = oldInput;
      this.newInput = newInput;
      this.changeTime = changeTime;
   }

   @Override
   public String toString() {
      return "Snapshot" + (isOutOfDate() ? "(out of date)" : "") + " from " + changeTime + " on transactions " + oldInput.getBaseTransaction().getTransactionNumber() + " to " + oldInput.getToTransaction().getTransactionNumber();
   }

   /**
    * @return Returns the changeTime.
    */
   public Date getChangeTime() {
      return changeTime;
   }

   /**
    * @return Returns the newInput.
    */
   public ChangeReportInput getNewInput() {
      return newInput;
   }

   /**
    * @return Returns the oldInput.
    */
   public ChangeReportInput getOldInput() {
      return oldInput;
   }

   public boolean isOutOfDate() {
      return oldInput.getChecksum() != newInput.getChecksum();
   }
}
