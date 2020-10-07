/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.web.rest.layer.application;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.icteam.common.clientserver.osee.ats.workdefs.ICTeamAtsWorkDefintionProvider;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.ComponentsResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.GeneralArtifactResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.ProjectsResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.ReleasesResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.TeamWorkflowResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.TeamsResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.UserDashboardResource;
import org.eclipse.osee.icteam.web.rest.layer.structure.resources.UsersResource;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Application class to register all the Rest resources
 *
 * @author Ajay Chandrahasan
 */
@ApplicationPath("getproject")
public class ProjectApplication extends Application {
   private final Set<Object> singletons = new HashSet<>();
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   private static AtsApi atsApi;

   public void setAtsServer(final IAtsServer atsServer) {
      atsApi = atsServer;
   }

   /**
    * To get atsServer value
    * 
    * @return atsServer value
    */
   public static AtsApi getAtsServer() {
      return atsApi;
   }

   public void start() {
      singletons.add(new TeamsResource(atsApi, orcsApi));
      singletons.add(new ComponentsResource(atsApi, orcsApi));
      singletons.add(new ReleasesResource(atsApi, orcsApi));
      singletons.add(new ProjectsResource(atsApi, orcsApi));
      singletons.add(new TeamWorkflowResource(atsApi, orcsApi));
      singletons.add(new UsersResource(atsApi, orcsApi));
      singletons.add(new GeneralArtifactResource(atsApi, orcsApi));
      singletons.add(new UserDashboardResource(atsApi, orcsApi));

      Thread loadConfig = new Thread("Load iCTeam ATS Config") {

         @Override
         public void run() {
            atsApi.getConfigService().getConfigurations();
         }

      };
      loadConfig.start();

      registerICTeamWorkDef();
   }

   /**
    * Registers ICTeam work definitions on launch of server
    */
   public void registerICTeamWorkDef() {
      ICTeamAtsWorkDefintionProvider workDefProvider = new ICTeamAtsWorkDefintionProvider();
      getAtsServer().getWorkDefinitionProviderService().addWorkDefinitionProvider(workDefProvider);
   }

   @Override
   public Set<Object> getSingletons() {

      return singletons;
   }

}
