/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface INewActionListener {

   /**
    * Called after Action and team workflows are created and before persist of Action
    */
   public void actionCreated(IAtsAction action) throws OseeCoreException;

   /**
    * Called after team workflow and initialized and before persist of Action
    */
   public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) throws OseeCoreException;

   /**
    * @return workflow id to use instead of default configured id
    */
   public String getOverrideWorkDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException;
}
