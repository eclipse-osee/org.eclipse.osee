/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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