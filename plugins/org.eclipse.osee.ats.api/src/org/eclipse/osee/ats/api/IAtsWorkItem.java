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
package org.eclipse.osee.ats.api;

import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workflow.HasAssignees;
import org.eclipse.osee.ats.api.workflow.HasStateProvider;
import org.eclipse.osee.ats.api.workflow.HasWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkItem extends IAtsObject, HasWorkData, HasAssignees, HasStateProvider {

   String getAtsId();

   void setAtsId(String atsId) throws OseeCoreException;

   IAtsTeamWorkflow getParentTeamWorkflow() throws OseeCoreException;

   IAtsStateManager getStateMgr();

   IAtsLog getLog();

   IAtsWorkDefinition getWorkDefinition() throws OseeCoreException;

   IAtsStateDefinition getStateDefinition() throws OseeCoreException;

   IAtsLogItem getStateStartedData(IStateToken state) throws OseeCoreException;

   boolean isTask();

   boolean isTeamWorkflow();

   String getTypeName();

}
