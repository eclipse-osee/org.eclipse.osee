/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
