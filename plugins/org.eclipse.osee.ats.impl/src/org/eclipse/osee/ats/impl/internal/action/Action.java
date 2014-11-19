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
package org.eclipse.osee.ats.impl.internal.action;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class Action extends AtsObject implements IAtsAction {

   private final IAtsServer atsServer;

   public Action(IAtsServer atsServer, ArtifactReadable artRead) {
      super(artRead.getGuid(), artRead.getName(), artRead.getLocalId());
      this.atsServer = atsServer;
      setStoreObject(artRead);
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeamWorkflows() throws OseeCoreException {
      return atsServer.getWorkItemService().getTeams(this);
   }

   @Override
   public String getAtsId() {
      try {
         return ((ArtifactReadable) getStoreObject()).getSoleAttributeAsString(AtsAttributeTypes.AtsId, getGuid());
      } catch (OseeCoreException ex) {
         return null;
      }
   }

   @Override
   public void setAtsId(String atsId) throws OseeCoreException {
      throw new OseeStateException("Not implemented");
   }

}
