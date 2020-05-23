/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
