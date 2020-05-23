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

package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public enum StateType {
   Working,
   Completed,
   Cancelled;

   public boolean isCompletedState() {
      return this == Completed;
   }

   public boolean isCompleted() {
      return isCompletedState();
   }

   public boolean isCompletedOrCancelledState() {
      return isCompletedState() || isCancelledState();
   }

   public boolean isCompletedOrCancelled() {
      return isCompletedOrCancelledState();
   }

   public boolean isCancelledState() {
      return this == Cancelled;
   }

   public boolean isCancelled() {
      return isCancelledState();
   }

   public boolean isWorkingState() {
      return this == Working;
   }

   public boolean isInWork() {
      return isWorkingState();
   }
}
