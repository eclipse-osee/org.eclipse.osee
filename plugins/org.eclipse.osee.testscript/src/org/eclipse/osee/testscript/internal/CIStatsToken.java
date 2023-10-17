/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.testscript.internal;

/**
 * @author Ryan T. Baldwin
 */
public class CIStatsToken {

   private String name;
   private int scriptsPass;
   private int scriptsFail;
   private int scriptsAbort;
   private int scriptsDispo;
   private int testPointsPass;
   private int testPointsFail;
   private int scriptsRan;
   private int scriptsNotRan;

   public CIStatsToken(String name, int scriptsPass, int scriptsFail, int scriptsAbort, int scriptsDispo, int testPointsPass, int testPointsFail, int scriptsRan, int scriptsNotRan) {
      this.name = name;
      this.scriptsPass = scriptsPass;
      this.scriptsFail = scriptsFail;
      this.scriptsAbort = scriptsAbort;
      this.scriptsDispo = scriptsDispo;
      this.testPointsPass = testPointsPass;
      this.testPointsFail = testPointsFail;
      this.scriptsRan = scriptsRan;
      this.scriptsNotRan = scriptsNotRan;
   }

   public CIStatsToken(String name) {
      this.name = name;
      this.scriptsPass = 0;
      this.scriptsFail = 0;
      this.scriptsAbort = 0;
      this.scriptsDispo = 0;
      this.testPointsPass = 0;
      this.testPointsFail = 0;
      this.scriptsRan = 0;
      this.scriptsNotRan = 0;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getScriptsPass() {
      return scriptsPass;
   }

   public void setScriptsPass(int scriptsPass) {
      this.scriptsPass = scriptsPass;
   }

   public void addScriptsPass(int scriptsPass) {
      this.scriptsPass += scriptsPass;
   }

   public int getScriptsFail() {
      return scriptsFail;
   }

   public void setScriptsFail(int scriptsFail) {
      this.scriptsFail = scriptsFail;
   }

   public void addScriptsFail(int scriptsFail) {
      this.scriptsFail += scriptsFail;
   }

   public int getScriptsAbort() {
      return scriptsAbort;
   }

   public void setScriptsAbort(int scriptsAbort) {
      this.scriptsAbort = scriptsAbort;
   }

   public void addScriptsAbort(int scriptsAbort) {
      this.scriptsAbort += scriptsAbort;
   }

   public int getScriptsDispo() {
      return scriptsDispo;
   }

   public void setScriptsDispo(int scriptsDispo) {
      this.scriptsDispo = scriptsDispo;
   }

   public void addScriptsDispo(int scriptsDispo) {
      this.scriptsDispo += scriptsDispo;
   }

   public int getTestPointsPass() {
      return testPointsPass;
   }

   public void setTestPointsPass(int testPointsPass) {
      this.testPointsPass = testPointsPass;
   }

   public void addTestPointsPass(int testPointsPass) {
      this.testPointsPass += testPointsPass;
   }

   public int getTestPointsFail() {
      return testPointsFail;
   }

   public void setTestPointsFail(int testPointsFail) {
      this.testPointsFail = testPointsFail;
   }

   public void addTestPointsFail(int testPointsFail) {
      this.testPointsFail += testPointsFail;
   }

   public int getScriptsRan() {
      return scriptsRan;
   }

   public void setScriptsRan(int scriptsRan) {
      this.scriptsRan = scriptsRan;
   }

   public void addScriptsRan(int scriptsRan) {
      this.scriptsRan += scriptsRan;
   }

   public int getScriptsNotRan() {
      return scriptsNotRan;
   }

   public void setScriptsNotRan(int scriptsNotRan) {
      this.scriptsNotRan = scriptsNotRan;
   }

   public void addScriptsNotRan(int scriptsNotRan) {
      this.scriptsNotRan += scriptsNotRan;
   }

}
