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
public class InvalidLocatorException extends Exception {

   private static final long serialVersionUID = -1291325728313575694L;

   public InvalidLocatorException() {
      super();
   }

   public InvalidLocatorException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidLocatorException(String message) {
      super(message);
   }

   public InvalidLocatorException(Throwable cause) {
      super(cause);
   }

}
