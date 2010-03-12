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
package org.eclipse.osee.ote.core.framework.prompt;

public class PassFailPromptResult {
   private final boolean pass;
   private final String text;

   /**
    * @param pass
    * @param text
    */
   public PassFailPromptResult(boolean pass, String text) {
      this.pass = pass;
      this.text = text;
   }

   /**
    * @return the pass
    */
   public boolean isPass() {
      return pass;
   }

   /**
    * @return the text
    */
   public String getText() {
      return text;
   }

}