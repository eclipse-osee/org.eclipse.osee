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

package org.eclipse.osee.framework.ui.plugin.util;

public class Result {

   public static Result TrueResult = new Result(true);
   public static Result FalseResult = new Result(false);
   private boolean isTrue = false;
   private String text = "";

   public Result(boolean valid, String text) {
      this.isTrue = valid;
      this.text = text;
   }

   public Result(String text) {
      this.isTrue = false;
      this.text = text;
   }

   public Result(boolean isTrue) {
      this.isTrue = isTrue;
   }

   public Result() {
      this.isTrue = true;
   }

   public boolean isTrue() {
      return isTrue;
   }

   public boolean isFalse() {
      return !isTrue;
   }

   public String getText() {
      return text;
   }

   public void popup() {
      popup(false);
   }

   public void popup(final boolean success) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            AWorkbench.popup((success ? "Success" : "ERROR"),
                  text != null && !text.equals("") ? text : "Error Encountered.  See Error Log View");
         };
      });
   }

   public void setText(String text) {
      this.text = text;
   }

}
