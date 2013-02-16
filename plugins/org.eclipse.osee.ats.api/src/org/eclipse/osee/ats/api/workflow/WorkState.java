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
package org.eclipse.osee.ats.api.workflow;

import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public interface WorkState {

   public abstract void setHoursSpent(double hoursSpent);

   public abstract void setPercentComplete(int percentComplete);

   public abstract String getName();

   public abstract List<IAtsUser> getAssignees();

   public abstract double getHoursSpent();

   public abstract int getPercentComplete();

   public abstract void addAssignee(IAtsUser steve);

   public abstract void setAssignees(List<? extends IAtsUser> users);

   public abstract void setName(String name);

   public abstract void removeAssignee(IAtsUser assignee);

}