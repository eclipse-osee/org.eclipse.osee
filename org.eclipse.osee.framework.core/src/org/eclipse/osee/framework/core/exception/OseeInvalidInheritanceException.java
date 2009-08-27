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

public class OseeInvalidInheritanceException extends OseeCoreException {

   private static final long serialVersionUID = -4553986819597790648L;

   public OseeInvalidInheritanceException(String message) {
      super(message);
   }

   public OseeInvalidInheritanceException(String message, Throwable cause) {
      super(message, cause);
   }

   public OseeInvalidInheritanceException(Throwable cause) {
      super(cause);
   }
}
