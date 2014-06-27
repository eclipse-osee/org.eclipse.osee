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

import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

public class NewActionAdapter implements INewActionListener {

   @Override
   public void actionCreated(IAtsAction action) throws OseeCoreException {
      // for override
   }

   @Override
   public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) throws OseeCoreException {
      // for override
   }

   @Override
   public String getOverrideWorkDefinitionId(IAtsTeamWorkflow teamWf) throws OseeCoreException {
      // for override
      return null;
   }

}
