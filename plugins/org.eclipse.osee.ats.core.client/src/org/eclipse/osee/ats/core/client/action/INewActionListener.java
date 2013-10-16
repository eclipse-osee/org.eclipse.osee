/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.action;

import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public interface INewActionListener {

   /**
    * Called after Action and team workflows are created and before persist of Action
    */
   public void actionCreated(Artifact actionArt) throws OseeCoreException;

   /**
    * Called after team workflow and initialized and before persist of Action
    */
   public void teamCreated(ActionArtifact actionArt, TeamWorkFlowArtifact teamArt, SkynetTransaction transaction) throws OseeCoreException;

   /**
    * @return workflow id to use instead of default configured id
    */
   public String getOverrideWorkDefinitionId(TeamWorkFlowArtifact teamArt) throws OseeCoreException;
}
