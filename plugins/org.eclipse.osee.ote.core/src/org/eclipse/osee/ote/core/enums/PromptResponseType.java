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
package org.eclipse.osee.ote.core.enums;

/**
 * @author Andrew M. Finkbeiner
 */
public enum PromptResponseType {

   NONE,
   /**
    * Wait for the a response from the user confirming that they have started the debug ofp.
    */
   OFP_DEBUG_RESPONSE,
   /**
    * Pause script execution until a response is recieved from a client.
    */
   SCRIPT_PAUSE, PASS_FAIL, SCRIPT_STEP, USER_INPUT;

}
