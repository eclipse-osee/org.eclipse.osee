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
package org.eclipse.osee.ats.core.mock;

import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.api.workflow.state.WorkStateFactory;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;

/**
 * @author Donald G. Dunne
 */
public class MockWorkStateFactory implements WorkStateFactory {

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees) {
      return new WorkStateImpl(name, assignees);
   }

   @Override
   public WorkState createStateData(String name) {
      return new WorkStateImpl(name);
   }

   @Override
   public WorkState createStateData(String name, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) {
      return new WorkStateImpl(name, assignees, hoursSpent, percentComplete);
   }

   @Override
   public String getId() {
      return "Mock ID";
   }

}
