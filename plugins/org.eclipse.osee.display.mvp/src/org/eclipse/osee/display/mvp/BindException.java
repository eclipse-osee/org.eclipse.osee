/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.mvp;

import org.eclipse.osee.display.mvp.internal.BaseException;

/**
 * @author Roberto E. Escobar
 */
public class BindException extends BaseException {

   private static final long serialVersionUID = -7428217337191035166L;

   public BindException(String message, Object... args) {
      super(message, args);
   }

   public BindException(Throwable cause, String message, Object... args) {
      super(cause, message, args);
   }

   public BindException(Throwable cause) {
      super(cause);
   }

}
