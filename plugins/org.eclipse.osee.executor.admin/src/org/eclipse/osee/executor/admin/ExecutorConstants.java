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
package org.eclipse.osee.executor.admin;

/**
 * @author Roberto E. Escobar
 */
public final class ExecutorConstants {

   private ExecutorConstants() {
      // Constants Class
   }

   private static final String PREFIX = "org/eclipse/osee/executor/admin/";

   public static final String EXECUTOR_ADMIN_REGISTRATION_EVENT = PREFIX + "EXECUTOR_ADMIN_REGISTRATION";

   public static final String EXECUTOR_ADMIN_DEREGISTRATION_EVENT = PREFIX + "EXECUTOR_ADMIN_DEREGISTRATION";

}
