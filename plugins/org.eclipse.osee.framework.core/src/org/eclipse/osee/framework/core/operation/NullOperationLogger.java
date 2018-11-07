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
package org.eclipse.osee.framework.core.operation;

/**
 * @author Roberto E. Escobar
 */
public class NullOperationLogger extends OperationLogger {
   private final static OperationLogger singleton = new NullOperationLogger();

   private NullOperationLogger() {
      // singleton so prevent external construction
   }

   @Override
   public void log(String... row) {
      // no implementation since this is a null logger
   }

   public static final OperationLogger getSingleton() {
      return singleton;
   }
}
