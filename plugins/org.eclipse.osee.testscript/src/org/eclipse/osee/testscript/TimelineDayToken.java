/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimelineDayToken {

   private final Date executionDate;
   private int scriptsPass = 0;
   private int scriptsFail = 0;
   private int pointsPass = 0;
   private int pointsFail = 0;
   private int abort = 0;
   private final Map<String, TimelineScriptToken> scripts = new HashMap<>();

   public TimelineDayToken() {
      this.executionDate = new Date();
   }

   public TimelineDayToken(Date executionDate) {
      this.executionDate = executionDate;
   }

   public Date getExecutionDate() {
      return executionDate;
   }

   public int getScriptsPass() {
      return scriptsPass;
   }

   public void setScriptsPass(int scriptsPass) {
      this.scriptsPass = scriptsPass;
   }

   public int getScriptsFail() {
      return scriptsFail;
   }

   public void setScriptsFail(int scriptsFail) {
      this.scriptsFail = scriptsFail;
   }

   public int getPointsPass() {
      return pointsPass;
   }

   public void setPointsPass(int pointsPass) {
      this.pointsPass = pointsPass;
   }

   public int getPointsFail() {
      return pointsFail;
   }

   public void setPointsFail(int pointsFail) {
      this.pointsFail = pointsFail;
   }

   public Integer getAbort() {
      return abort;
   }

   public void setAbort(Integer abort) {
      this.abort = abort;
   }

   public Map<String, TimelineScriptToken> getScripts() {
      return scripts;
   }

   @Override
   public String toString() {
      return executionDate.toString() + " // " + "pass=" + getScriptsPass() + " fail=" + getScriptsFail() + " abort=" + getAbort() + " scripts=" + getScripts().size();
   }

}
