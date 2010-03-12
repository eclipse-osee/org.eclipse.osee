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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class EmptyResourceException extends OseeCoreException {

   private static final long serialVersionUID = 262834138993880676L;

   public EmptyResourceException(String message, Throwable cause) {
      super(message, cause);
   }

   public EmptyResourceException(String message) {
      super(message);
   }

   public EmptyResourceException(Throwable cause) {
      super(cause);
   }
}
