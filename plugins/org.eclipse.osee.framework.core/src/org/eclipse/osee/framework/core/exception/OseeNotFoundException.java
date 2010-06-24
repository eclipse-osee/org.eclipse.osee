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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Ryan D. Brooks
 */
public class OseeNotFoundException extends OseeCoreException {

   private static final long serialVersionUID = 3496442169445903235L;

   public OseeNotFoundException(String message) {
      super(new Status(IStatus.ERROR, "OSEE", message));
   }

   public OseeNotFoundException(String message, Throwable cause) {
      super(new Status(IStatus.ERROR, "OSEE", message, cause));
   }

   public OseeNotFoundException(Throwable cause) {
      super(new Status(IStatus.ERROR, "OSEE", cause.getMessage(), cause));
   }

   public OseeNotFoundException(IStatus status) {
      super(status);
   }
}