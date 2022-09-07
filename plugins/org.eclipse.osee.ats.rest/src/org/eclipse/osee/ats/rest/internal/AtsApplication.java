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

package org.eclipse.osee.ats.rest.internal;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.ats.rest.AtsApiServer;
import org.eclipse.osee.ats.rest.internal.agile.AgileEndpointImpl;
import org.eclipse.osee.ats.rest.internal.agile.operations.SprintDataUiOperation;
import org.eclipse.osee.ats.rest.internal.agile.operations.SprintSummaryOperation;
import org.eclipse.osee.ats.rest.internal.config.ActionableItemResource;
import org.eclipse.osee.ats.rest.internal.config.AtsConfigEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.ConvertAtsConfigGuidAttributes;
import org.eclipse.osee.ats.rest.internal.config.ConvertCreateUpdateAtsConfig;
import org.eclipse.osee.ats.rest.internal.config.ConvertResource;
import org.eclipse.osee.ats.rest.internal.config.CountryEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.CountryResource;
import org.eclipse.osee.ats.rest.internal.config.InsertionActivityEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.InsertionActivityResource;
import org.eclipse.osee.ats.rest.internal.config.InsertionEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.InsertionResource;
import org.eclipse.osee.ats.rest.internal.config.ProgramEndpointImpl;
import org.eclipse.osee.ats.rest.internal.config.ProgramResource;
import org.eclipse.osee.ats.rest.internal.config.ReportResource;
import org.eclipse.osee.ats.rest.internal.config.TeamResource;
import org.eclipse.osee.ats.rest.internal.config.UserResource;
import org.eclipse.osee.ats.rest.internal.config.VersionResource;
import org.eclipse.osee.ats.rest.internal.notify.AtsNotifyEndpointImpl;
import org.eclipse.osee.ats.rest.internal.util.AtsProductLineEndpointImpl;
import org.eclipse.osee.ats.rest.internal.util.health.AtsHealthEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsActionEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsActionUiEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsAttributeEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTaskEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsTeamWfEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.AtsWorkPackageEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.StateResource;
import org.eclipse.osee.ats.rest.internal.workitem.operations.ConvertWorkDefinitionToAttributes;
import org.eclipse.osee.ats.rest.internal.workitem.sync.jira.JiraEndpointImpl;
import org.eclipse.osee.ats.rest.internal.workitem.workdef.AtsWorkDefEndpointImpl;
import org.eclipse.osee.ats.rest.internal.world.AtsWorldEndpointImpl;
import org.eclipse.osee.ats.rest.metrics.MetricsEndpointImpl;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.jdk.core.type.IResourceRegistry;
import org.eclipse.osee.framework.jdk.core.type.ResourceRegistry;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.template.engine.OseeTemplateTokens;

/**
 * @author John Misinco
 */
@ApplicationPath("ats")
public class AtsApplication extends Application {

   private final Set<Object> singletons = new HashSet<>();

   private Log logger;
   private static OrcsApi orcsApi;
   private AtsApiServer atsApiServer;
   private JdbcService jdbcService;

   private ExecutorAdmin executorAdmin;

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void setOrcsApi(OrcsApi orcsApi) {
      AtsApplication.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setAtsApiServer(AtsApiServer atsApiServer) {
      this.atsApiServer = atsApiServer;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start() {
      IResourceRegistry registry = new ResourceRegistry();
      OseeTemplateTokens.register(registry);

      // Register conversions
      ConvertCreateUpdateAtsConfig atsConfgConversion = new ConvertCreateUpdateAtsConfig(orcsApi);
      atsApiServer.addAtsDatabaseConversion(atsConfgConversion);
      atsApiServer.addAtsDatabaseConversion(new ConvertAtsConfigGuidAttributes());
      atsApiServer.addAtsDatabaseConversion(new ConvertWorkDefinitionToAttributes());

      // Register agile html report operations
      atsApiServer.getAgileSprintHtmlReportOperations().add(new SprintSummaryOperation(atsApiServer, registry));
      atsApiServer.getAgileSprintHtmlReportOperations().add(new SprintDataUiOperation(atsApiServer, registry));

      // Resources
      singletons.add(new VersionResource(atsApiServer, orcsApi));
      singletons.add(new TeamResource(atsApiServer, orcsApi));
      singletons.add(new ActionableItemResource(atsApiServer, orcsApi));

      singletons.add(new CountryResource(atsApiServer, orcsApi));
      singletons.add(new ProgramResource(atsApiServer, orcsApi));
      singletons.add(new InsertionResource(atsApiServer, orcsApi));
      singletons.add(new InsertionActivityResource(atsApiServer, orcsApi));

      singletons.add(new StateResource(atsApiServer));
      singletons.add(new ConvertResource(atsApiServer));
      singletons.add(new UserResource(atsApiServer.getUserService()));

      // Endpoints
      // NOTE: @Consumes(MediaType.APPLICATION_JSON) doesn't work with GET, must be PUT
      singletons.add(new AtsActionEndpointImpl(atsApiServer, orcsApi));
      singletons.add(new AtsWorldEndpointImpl(atsApiServer));
      singletons.add(new AtsHealthEndpointImpl(atsApiServer, jdbcService));
      singletons.add(new AtsWorkDefEndpointImpl(atsApiServer, orcsApi));
      singletons.add(new AgileEndpointImpl(atsApiServer, registry, jdbcService, orcsApi));
      singletons.add(new CountryEndpointImpl(atsApiServer));
      singletons.add(new ProgramEndpointImpl(atsApiServer));
      singletons.add(new InsertionEndpointImpl(atsApiServer));
      singletons.add(new InsertionActivityEndpointImpl(atsApiServer));
      singletons.add(new AtsTaskEndpointImpl(atsApiServer));
      singletons.add(new AtsConfigEndpointImpl(atsApiServer, orcsApi, executorAdmin));
      singletons.add(new AtsProductLineEndpointImpl(atsApiServer, orcsApi));
      singletons.add(new AtsNotifyEndpointImpl(atsApiServer));
      singletons.add(new AtsWorkPackageEndpointImpl(atsApiServer));
      singletons.add(new AtsTeamWfEndpointImpl(atsApiServer));
      singletons.add(new AtsAttributeEndpointImpl(atsApiServer, orcsApi));
      singletons.add(new JiraEndpointImpl(atsApiServer));
      singletons.add(new MetricsEndpointImpl(atsApiServer, orcsApi));

      // UIs
      singletons.add(new AtsActionUiEndpointImpl(atsApiServer, logger));
      singletons.add(new ReportResource(orcsApi, atsApiServer));

      Thread loadConfig = new Thread("Load ATS Config") {

         @Override
         public void run() {
            atsApiServer.getConfigService().getConfigurations();
         }

      };
      loadConfig.start();

      logger.warn("ATS Application Started - %s", System.getProperty("OseeApplicationServer"));
   }

   public void stop() {
      singletons.clear();
   }

   @Override
   public Set<Object> getSingletons() {
      return singletons;
   }

   public static OrcsApi getOrcsApi() {
      return orcsApi;
   }

}
