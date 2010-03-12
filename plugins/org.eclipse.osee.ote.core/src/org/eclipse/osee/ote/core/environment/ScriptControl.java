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
   
   public ScriptControl(){
   }
   
   public boolean isLocked(){
      return false;
   }
   
   public boolean isExecutionUnitPaused() {
      return isOfpPaused;
   }
   
   public boolean isScriptPaused() {
      return isScriptPaused;
   }

   public boolean isScriptReady() {
      return isScriptReady;
   }
   
   
   public void lock(){
   }

   public void setExecutionUnitPause(boolean pause) {
      isOfpPaused = pause;
   }

   public void setScriptPause(boolean pause) {
      isScriptPaused = pause;
   }

   public void setScriptReady(boolean ready){
      isScriptReady = ready;
   }

   public boolean shouldStep(){
      return (isScriptPaused() && !isExecutionUnitPaused());   
   }

   public void unlock(){
   }
   
   public boolean hasLock(){
      return false;
   }
   
   public boolean isHeldByCurrentThread() {
      return false;
   }
}
