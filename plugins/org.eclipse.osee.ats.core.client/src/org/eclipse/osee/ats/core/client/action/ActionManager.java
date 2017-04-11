/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.action;

import java.util.Collection;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class ActionManager {

   public static Collection<TeamWorkFlowArtifact> getTeams(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         return ((ActionArtifact) object).getTeams();
      } else if (object instanceof ActionResult) {
         return Collections.castAll(AtsObjects.getArtifacts(((ActionResult) object).getTeamWfArts()));
      }
      return java.util.Collections.emptyList();
   }

   public static TeamWorkFlowArtifact getFirstTeam(Object object) throws OseeCoreException {
      if (object instanceof ActionArtifact) {
         return ((ActionArtifact) object).getFirstTeam();
      } else if (object instanceof ActionResult) {
         return (TeamWorkFlowArtifact) ((ActionResult) object).getFirstTeam();
      }
      return null;
   }

}
