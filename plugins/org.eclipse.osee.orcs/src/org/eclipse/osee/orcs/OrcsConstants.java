/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsConstants {

   private OrcsConstants() {
      // Constants class
   }

   private static final String PREFIX = "org/eclipse/osee/orcs/event/";

   public static final String REGISTRATION_EVENT = PREFIX + "OSEE_ORCS_SERVICE_REGISTRATION";

   public static final String BRANCH_CHANGE_EVENT = PREFIX + "branch/CHANGE";
   public static final String BRANCH_MOVE_EVENT = PREFIX + "branch/MOVE";
}
