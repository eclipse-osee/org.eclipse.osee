/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.core.config;

import java.util.Comparator;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;

/**
 * @author Donald G. Dunne
 */
public class GoalSorter implements Comparator<IAtsGoal> {

   public GoalSorter() {
      super();
   }

   @Override
   public int compare(IAtsGoal o1, IAtsGoal o2) {
      return o1.getName().compareTo(o2.getName());
   }
}