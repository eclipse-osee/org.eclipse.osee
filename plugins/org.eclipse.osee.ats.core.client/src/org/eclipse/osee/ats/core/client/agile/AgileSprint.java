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
package org.eclipse.osee.ats.core.client.agile;

import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.ats.core.client.internal.workflow.WorkItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AgileSprint extends WorkItem implements IAgileSprint {

   public AgileSprint(IAtsClient atsClient, Artifact artifact) {
      super(atsClient, artifact);
   }

   @Override
   public boolean isActive() {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Active, true);
   }

   @Override
   public long getTeamUuid() {
      return artifact.getArtId();
   }

}
