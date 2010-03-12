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
package org.eclipse.osee.ote.message;

import java.util.logging.Level;

import org.eclipse.osee.ote.core.TestException;


public class MessageSystemException extends TestException{

   /**
    * 
    */
   private static final long serialVersionUID = -8476610648021756216L;

   public MessageSystemException(String message, Level level) {
      this(message, level, null);
   }
   
   public MessageSystemException(String message, Level level, Throwable cause) {
      super(message, level, cause);
   }
}
