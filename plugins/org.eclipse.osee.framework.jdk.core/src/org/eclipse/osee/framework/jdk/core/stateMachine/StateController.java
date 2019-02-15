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
 * Runs each state of the stateMachine defined. Each state will calculate and know which state to go to next so this is
 * really a dumb controller, calling the run method of whatever state the current state returned from being run.
 */
public class StateController {
   private IState nextState;
   private boolean isRunning;

   public StateController(IState initialState) {
      isRunning = true;
      nextState = initialState;
   }

   /**
    * Calls the run method of the next state.
    */
   public void runNextState() {
      if (isRunning) {
         nextState = nextState.run();
      }
      if (nextState == null) {
         isRunning = false;
      }
   }

   public boolean isRunning() {
      return isRunning;
   }

   /**
    * causes machine to terminate on next invocation of the controller's run method.
    */
   public void turnMachineOff() {
      isRunning = false;
   }

}
