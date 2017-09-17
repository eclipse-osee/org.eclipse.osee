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
package org.eclipse.osee.ats.api.agile;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Donald G. Dunne
 */
public interface IAgileTeam extends IAtsConfigObject, IAgileObject {

   public List<Long> getAtsTeamUuids();

   public ArtifactId getBacklogId();

   /**
    * @return should be only in-work sprint, otherwise first in-work sprint
    */
   public ArtifactId getSprintId();

}
