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
