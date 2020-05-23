/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
      new CopyOnWriteArrayList<>();

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
