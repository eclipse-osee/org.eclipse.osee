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
 * allows a state machine to wait for a certain number of cycles before continuing. Notice that if a state needs to wait
 * only one cycle, a wait state should not be used as it would really be two cycles ( one to run the wait state then one
 * to start the next state). Instead that state could simply return an instance of the next state to run as normal.
 */
public class WaitState implements IState {

   int iterationsToWait;
   IState nextRealStateToRun;

   /**
    * @param iterationsToWait The number of times this state will be run
    * @param nextRealStateToRun The next state to run after waiting
    */
   public WaitState(int iterationsToWait, IState nextRealStateToRun) {
      this.iterationsToWait = iterationsToWait;
      this.nextRealStateToRun = nextRealStateToRun;
   }

   @Override
   public IState run() {
      iterationsToWait--;
      if (iterationsToWait <= 0) {
         return nextRealStateToRun;
      } else {
         return this;
      }

   }

}
