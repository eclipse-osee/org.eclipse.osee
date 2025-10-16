/*******************************************************************************
 * Copyright (c) 2025 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.Arrays;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public enum HoldState {

   On_Hold,
   Not_On_Hold,
   Not_Set;

   public static List<HoldState> values = Arrays.asList(On_Hold, Not_On_Hold, Not_Set);

   public boolean isOnHold() {
      return this == On_Hold;
   }

   public boolean isNotOnHold() {
      return !isOnHold();
   }

   public boolean isNotSet() {
      return this == Not_Set;
   }
}
