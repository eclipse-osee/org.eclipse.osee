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
package org.eclipse.osee.framework.core.exception;

/**
 * @author Ryan D. Brooks
 */
public class OseeCoreException extends Exception {
   private static final long serialVersionUID = 1L;

   /**
    * @param message
    */
   public OseeCoreException(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public OseeCoreException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * @param cause
    */
   public OseeCoreException(Throwable cause) {
      super(cause);
   }
}