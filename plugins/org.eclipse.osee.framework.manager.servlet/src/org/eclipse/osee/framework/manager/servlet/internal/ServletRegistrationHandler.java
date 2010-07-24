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
package org.eclipse.osee.framework.manager.servlet.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.branch.management.IBranchExchange;
import org.eclipse.osee.framework.branch.management.IOseeBranchService;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.message.IOseeModelingService;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.manager.servlet.ArtifactFileServlet;
import org.eclipse.osee.framework.manager.servlet.AtsServlet;
import org.eclipse.osee.framework.manager.servlet.BranchExchangeServlet;
import org.eclipse.osee.framework.manager.servlet.BranchManagerServlet;
import org.eclipse.osee.framework.manager.servlet.ClientInstallInfoServlet;
import org.eclipse.osee.framework.manager.servlet.ConfigurationServlet;
import org.eclipse.osee.framework.manager.servlet.DataServlet;
import org.eclipse.osee.framework.manager.servlet.OseeCacheServlet;
import org.eclipse.osee.framework.manager.servlet.OseeModelServlet;
import org.eclipse.osee.framework.manager.servlet.ResourceManagerServlet;
import org.eclipse.osee.framework.manager.servlet.SearchEngineServlet;
import org.eclipse.osee.framework.manager.servlet.SearchEngineTaggerServlet;
import org.eclipse.osee.framework.manager.servlet.ServerLookupServlet;
import org.eclipse.osee.framework.manager.servlet.SessionClientLoopbackServlet;
import org.eclipse.osee.framework.manager.servlet.SessionManagementServlet;
import org.eclipse.osee.framework.manager.servlet.SystemManagerServlet;
import org.eclipse.osee.framework.manager.servlet.UnsubscribeServlet;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;

/**
 * @author Roberto E. Escobar
 */
public class ServletRegistrationHandler extends AbstractTrackingHandler {

	private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {
				//
				ISessionManager.class, //
				IApplicationServerLookup.class, // 
				IApplicationServerManager.class, // 
				IAuthenticationManager.class,// 
				IDataTranslationService.class, // 
				IOseeModelingService.class, // 
				IOseeCachingService.class,// 
				IOseeDatabaseService.class, // 
				IOseeBranchService.class, // 
				IBranchExchange.class, // 
				ISearchEngine.class,// 
				ISearchEngineTagger.class, // 
				IOseeModelFactoryService.class, // 
				IResourceLocatorManager.class,// 
				IResourceManager.class, // 
				HttpService.class, // 
	};

	private final Set<String> contexts = new HashSet<String>();
	private HttpService httpService;
	private IApplicationServerManager appServerManager;

	@Override
	public Class<?>[] getDependencies() {
		return SERVICE_DEPENDENCIES;
	}

	@Override
	public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
		ISessionManager sessionManager = getService(ISessionManager.class, services);
		IApplicationServerLookup serverLookup = getService(IApplicationServerLookup.class, services);
		appServerManager = getService(IApplicationServerManager.class, services);
		IDataTranslationService translationService = getService(IDataTranslationService.class, services);
		IOseeModelingService modeling = getService(IOseeModelingService.class, services);
		IOseeCachingService caching = getService(IOseeCachingService.class, services);
		IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
		IOseeBranchService branchService = getService(IOseeBranchService.class, services);
		IBranchExchange branchExchangeService = getService(IBranchExchange.class, services);
		ISearchEngine search = getService(ISearchEngine.class, services);
		ISearchEngineTagger tagger = getService(ISearchEngineTagger.class, services);
		IAuthenticationManager authenticationManager = getService(IAuthenticationManager.class, services);
		IOseeModelFactoryService factoryService = getService(IOseeModelFactoryService.class, services);
		IResourceLocatorManager locatorManager = getService(IResourceLocatorManager.class, services);
		IResourceManager resourceManager = getService(IResourceManager.class, services);

		httpService = getService(HttpService.class, services);
		appServerManager = getService(IApplicationServerManager.class, services);

		register(new SystemManagerServlet(sessionManager), OseeServerContext.MANAGER_CONTEXT);
		register(new ResourceManagerServlet(sessionManager, locatorManager, resourceManager),
					OseeServerContext.RESOURCE_CONTEXT);
		register(new ArtifactFileServlet(locatorManager, resourceManager), OseeServerContext.PROCESS_CONTEXT);
		register(new ArtifactFileServlet(locatorManager, resourceManager), OseeServerContext.ARTIFACT_CONTEXT);
		register(new ArtifactFileServlet(locatorManager, resourceManager), "index");
		register(new BranchExchangeServlet(sessionManager, branchExchangeService, locatorManager, resourceManager),
					OseeServerContext.BRANCH_EXCHANGE_CONTEXT);
		register(new BranchManagerServlet(sessionManager, branchService, translationService),
					OseeServerContext.BRANCH_CONTEXT);
		register(new SearchEngineServlet(sessionManager, search, caching), OseeServerContext.SEARCH_CONTEXT);
		register(new SearchEngineTaggerServlet(sessionManager, tagger), OseeServerContext.SEARCH_TAGGING_CONTEXT);
		register(new ServerLookupServlet(serverLookup, appServerManager), OseeServerContext.LOOKUP_CONTEXT);
		register(new SessionManagementServlet(sessionManager, authenticationManager), OseeServerContext.SESSION_CONTEXT);
		register(new SessionClientLoopbackServlet(sessionManager), OseeServerContext.CLIENT_LOOPBACK_CONTEXT);
		register(new ClientInstallInfoServlet(), "osee/install/info");
		register(new OseeCacheServlet(sessionManager, translationService, caching, factoryService),
					OseeServerContext.CACHE_CONTEXT);
		register(new OseeModelServlet(sessionManager, translationService, modeling), OseeServerContext.OSEE_MODEL_CONTEXT);
		register(new UnsubscribeServlet(context, databaseService, caching), "osee/unsubscribe");

		register(new AtsServlet(locatorManager, resourceManager), "osee/ats");
		register(new ConfigurationServlet(appServerManager, translationService, databaseService, branchService),
					OseeServerContext.OSEE_CONFIGURE_CONTEXT);
		register(new DataServlet(locatorManager, resourceManager), "osee/data");
	}

	private void register(OseeHttpServlet servlet, String... contexts) {
		this.contexts.addAll(Arrays.asList(contexts));
		ServletUtil.register(httpService, appServerManager, servlet, contexts);
	}

	@Override
	public void onDeActivate() {
		if (httpService != null && appServerManager != null) {
			ServletUtil.unregister(httpService, appServerManager, contexts);
		}
		contexts.clear();
	}

}
