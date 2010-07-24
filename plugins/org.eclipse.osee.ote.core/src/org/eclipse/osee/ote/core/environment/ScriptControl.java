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
package org.eclipse.osee.ote.core.environment;

import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;

public class ScriptControl implements IScriptControl {

   protected boolean isOfpPaused = false;
   protected boolean isScriptPaused = false;
   protected boolean isScriptReady = false;

   public ScriptControl() {
   }

   @Override
   public boolean isLocked() {
      return false;
   }

   @Override
   public boolean isExecutionUnitPaused() {
      return isOfpPaused;
   }

   @Override
   public boolean isScriptPaused() {
      return isScriptPaused;
   }

   @Override
   public boolean isScriptReady() {
      return isScriptReady;
   }

   @Override
   public void lock() {
   }

   @Override
   public void setExecutionUnitPause(boolean pause) {
      isOfpPaused = pause;
   }

   @Override
   public void setScriptPause(boolean pause) {
      isScriptPaused = pause;
   }

   @Override
   public void setScriptReady(boolean ready) {
      isScriptReady = ready;
   }

   @Override
   public boolean shouldStep() {
      return isScriptPaused() && !isExecutionUnitPaused();
   }

   @Override
   public void unlock() {
   }

   @Override
   public boolean hasLock() {
      return false;
   }

   @Override
   public boolean isHeldByCurrentThread() {
      return false;
   }
}
