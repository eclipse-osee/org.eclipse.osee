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
package org.eclipse.osee.ats.core.workflow;

import java.util.List;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.StateTypeAdapter;

/**
 * @author Donald G. Dunne
 */
public class OneStates extends StateTypeAdapter {

   public static OneStates Endorse = new OneStates("Endorse", StateType.Working, "This is OneStates Endorse");
   public static OneStates Cancelled = new OneStates("Cancelled", StateType.Cancelled);
   public static OneStates Completed = new OneStates("Completed", StateType.Completed);

   public OneStates(String pageName, StateType StateType) {
      super(OneStates.class, pageName, StateType);
   }

   public OneStates(String pageName, StateType StateType, String description) {
      super(OneStates.class, pageName, StateType);
      setDescription(description);
   }

   public static OneStates valueOf(String pageName) {
      return StateTypeAdapter.valueOfPage(OneStates.class, pageName);
   }

   public static List<OneStates> values() {
      return StateTypeAdapter.pages(OneStates.class);
   }

}
