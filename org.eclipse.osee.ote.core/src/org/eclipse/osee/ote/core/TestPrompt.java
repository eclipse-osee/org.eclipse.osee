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
package org.eclipse.osee.ote.core;

import java.io.Serializable;
import org.eclipse.osee.ote.core.enums.PromptResponseType;


/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class TestPrompt implements Serializable {

   /**
	 * 
	 */
   private static final long serialVersionUID = 5960067878239875110L;
   private String prompt;
   private boolean waitForResponse;
   private boolean ofpStep;
   private PromptResponseType type;

   public TestPrompt(String prompt) {
      this(prompt, PromptResponseType.NONE);
   }
   
   /**
    *  
    */
   public TestPrompt(String prompt, PromptResponseType type) {
      super();
      this.prompt = prompt;
      this.waitForResponse = (type == PromptResponseType.SCRIPT_PAUSE || type == PromptResponseType.PASS_FAIL || type == PromptResponseType.SCRIPT_STEP|| type == PromptResponseType.USER_INPUT) ? true : false;
      this.ofpStep = (type == PromptResponseType.SCRIPT_STEP) ? true : false;
      this.type = type;
   }
   
   public PromptResponseType getType() {
      return type;
   }
   
   public boolean isWaiting() {
      return this.waitForResponse;
   }

   public String toString() {
      return this.prompt;
   }

   /**
    * @return Returns the ofpStep.
    */
   public boolean isOfpStep() {
      return ofpStep;
   }
}