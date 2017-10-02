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
package org.eclipse.osee.ats.core.workflow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.team.ITeamWorkflowProvider;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class TeamWorkflowProviders implements ITeamWorkflowProvidersLazy {

   private Log logger;
   private static final List<ITeamWorkflowProvider> teamWorkflowProviders =
      new CopyOnWriteArrayList<ITeamWorkflowProvider>();

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void addTeamWorkflowProvider(ITeamWorkflowProvider teamWorkflowProvider) {
      teamWorkflowProviders.add(teamWorkflowProvider);
   }

   public void removeTeamWorkflowProvider(ITeamWorkflowProvider teamWorkflowProvider) {
      teamWorkflowProviders.remove(teamWorkflowProvider);
   }

   public void start() {
      logger.info("AtsTeamWorkflowProviders started");
   }

   public void stop() {
      logger = null;
      teamWorkflowProviders.clear();
   }

   public static List<ITeamWorkflowProvider> getTeamWorkflowProviders() {
      return teamWorkflowProviders;
   }

   @Override
   public List<ITeamWorkflowProvider> getProviders() {
      return teamWorkflowProviders;
   }

}
