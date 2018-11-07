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

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class OseeInvalidInheritanceException extends OseeCoreException {

   public OseeInvalidInheritanceException(String message, Throwable cause) {
      super(message, cause);
   }

   public OseeInvalidInheritanceException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public OseeInvalidInheritanceException(Throwable cause) {
      super(cause);
   }

   private static final long serialVersionUID = -4553986819597790648L;

   public OseeInvalidInheritanceException(String message, Object... args) {
      super(message, args);
   }
}
