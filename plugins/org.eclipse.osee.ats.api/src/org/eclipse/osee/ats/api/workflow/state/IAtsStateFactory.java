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
package org.eclipse.osee.ats.api.workflow.state;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G Dunne
 */
public interface IAtsStateFactory {

   IAtsStateManager getStateManager(IAtsWorkItem workItem) ;

   void writeToStore(IAtsUser atsUser, IAtsWorkItem workItem, IAtsChangeSet changes) ;

   void load(IAtsWorkItem workItem, IAtsStateManager stateMgr);

   void clearStateManager(Id id);

   void setStateMgr(IAtsWorkItem workItem, IAtsStateManager stateMgr);

}
