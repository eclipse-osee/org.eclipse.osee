/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.agile;

import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.internal.workitem.WorkItem;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class AgileBacklog extends WorkItem implements IAgileBacklog {

   public AgileBacklog(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public boolean isActive() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Active, true);
   }

   @Override
   public long getTeamUuid() {
      return artifact.getLocalId();
   }

}
