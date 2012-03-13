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

   private static final String ORCS_EVENT_PREFIX = "org/eclipse/osee/orcs/event/";

   public static final String ORCS_REGISTRATION_EVENT = ORCS_EVENT_PREFIX + "OSEE_ORCS_SERVICE_REGISTRATION";
   public static final String ORCS_DEREGISTRATION_EVENT = ORCS_EVENT_PREFIX + "OSEE_ORCS_SERVICE_DEREGISTRATION";

   public static final String ORCS_BRANCH_MODIFIED_EVENT = ORCS_EVENT_PREFIX + "branch/MODIFIED";
   public static final String ORCS_BRANCH_MOVE_EVENT = ORCS_EVENT_PREFIX + "branch/MOVE";

   public static final String ORCS_BRANCH_EVENT_DATA = ORCS_EVENT_PREFIX + "branch/data";
}
