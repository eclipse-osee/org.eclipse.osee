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

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface WorkStateProvider {

   public String getCurrentStateName();

   public void setCurrentStateName(String currentStateName) throws OseeCoreException;

   public List<IAtsUser> getAssigneesForState(String fromStateName) throws OseeCoreException;

   public List<IAtsUser> getAssignees() throws OseeCoreException;

   public void setAssignees(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   public void addAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   public void addAssignee(IAtsUser assignee) throws OseeCoreException;

   public void addState(String stateName, List<? extends IAtsUser> assignees) throws OseeCoreException;

   public void addState(String stateName, List<? extends IAtsUser> assignees, double hoursSpent, int percentComplete) throws OseeCoreException;

   public void setAssignees(List<? extends IAtsUser> assignees) throws OseeCoreException;

   /**
    * Create state if does not already exist
    */
   public abstract void createState(String stateName) throws OseeCoreException;

   public abstract void setPercentComplete(String stateName, int percentComplete) throws OseeCoreException;

   public abstract void setHoursSpent(String stateName, double hoursSpent) throws OseeCoreException;

   public abstract double getHoursSpent(String stateName) throws OseeCoreException;

   public abstract int getPercentComplete(String stateName) throws OseeCoreException;

   public abstract List<String> getVisitedStateNames() throws OseeCoreException;

   public abstract void removeAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   public abstract void addAssignees(String stateName, Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   public abstract List<IAtsUser> getAssignees(String stateName) throws OseeCoreException;

   public abstract boolean isUnAssignedSolely() throws OseeCoreException;

   public abstract boolean isUnAssigned() throws OseeCoreException;

   public abstract String getAssigneesStr() throws OseeCoreException;

   public abstract String getAssigneesStr(String stateName, int length) throws OseeCoreException;

   public abstract String getAssigneesStr(String stateName) throws OseeCoreException;

   public abstract void addAssignees(Collection<? extends IAtsUser> assignees) throws OseeCoreException;

   public abstract void setAssignee(IAtsUser assignee) throws OseeCoreException;

   public abstract boolean isStateVisited(String stateName) throws OseeCoreException;

   public abstract void clearAssignees() throws OseeCoreException;

   public abstract void removeAssignee(IAtsUser assignee) throws OseeCoreException;

   public abstract void setAssignee(String stateName, IAtsUser assignee) throws OseeCoreException;

   public void addState(WorkState workState) throws OseeCoreException;

   /**
    * Return true if state exists and all values are same
    */
   public boolean isSame(WorkState workState) throws OseeCoreException;
}
