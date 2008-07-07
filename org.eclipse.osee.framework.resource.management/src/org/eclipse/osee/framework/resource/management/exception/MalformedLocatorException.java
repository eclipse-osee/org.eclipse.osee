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
package org.eclipse.osee.framework.resource.management.exception;

/**
 * @author Roberto E. Escobar
 */
public class MalformedLocatorException extends Exception {

   private static final long serialVersionUID = -7595802736847790150L;

   public MalformedLocatorException() {
      super();
   }

   /**
    * @param message
    * @param cause
    */
   public MalformedLocatorException(String message, Throwable cause) {
      super(message, cause);
   }

   /**
    * @param message
    */
   public MalformedLocatorException(String message) {
      super(message);
   }

   /**
    * @param cause
    */
   public MalformedLocatorException(Throwable cause) {
      super(cause);
   }

}
