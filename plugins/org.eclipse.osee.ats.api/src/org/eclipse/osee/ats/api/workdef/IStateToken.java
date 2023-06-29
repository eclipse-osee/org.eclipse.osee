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

import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Donald G. Dunne
 */
public interface IStateToken extends Named {

   public String getDescription();

   public StateType getStateType();

   public boolean isCompleted();

   public boolean isCancelled();

   public boolean isWorking();

   public boolean isCompletedOrCancelled();

   default public boolean isState(IStateToken state) {
      return state.getName().equals(getName());
   }

   default public boolean isNotState(IStateToken state) {
      return !isState(state);
   }
}