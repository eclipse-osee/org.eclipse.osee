/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.stateMachine;

/**
 * A single state in a contructed state machine. All implementers should calculate and control what the next state in
 * the machine should be.
 */
public interface IState {
   /**
    * Called by the state machine controller to start this particular state.
    *
    * @return The next state the controller should run or null if the machine should terminate
    */
   public IState run();
}
