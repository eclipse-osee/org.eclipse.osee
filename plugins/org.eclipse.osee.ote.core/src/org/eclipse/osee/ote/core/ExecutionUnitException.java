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
package org.eclipse.osee.ote.core;

import java.util.logging.Level;

public class ExecutionUnitException extends TestException{


   private static final long serialVersionUID = -9119275292591321042L;

   public ExecutionUnitException(String message, Level level, Throwable cause) {
      super(message, level, cause);
   }

   public ExecutionUnitException(String message, Level level) {
      super(message, level);
   }

}
