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
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Map;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.internal.BuildTypeDataProvider;
import org.eclipse.osee.framework.core.server.internal.BuildTypeIdentifier;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public final class SessionManagerTrackingHandler extends AbstractTrackingHandler {

	private static final Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {IOseeDatabaseService.class,
				IApplicationServerManager.class, IAuthenticationManager.class};

	private ServiceRegistration registration;

	private ISessionDataStoreSync dataStoreSync;
	private ISessionManager sessionManager;

	@Override
	public Class<?>[] getDependencies() {
		return SERVICE_DEPENDENCIES;
	}

	@Override
	public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
		IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
		IApplicationServerManager serverManager = getService(IApplicationServerManager.class, services);
		IAuthenticationManager authenticationManager = getService(IAuthenticationManager.class, services);

		String serverId = serverManager.getId();
		BuildTypeIdentifier identifier = new BuildTypeIdentifier(new BuildTypeDataProvider());
		SessionFactory sessionFactory = new SessionFactory(identifier);

		ISessionQuery sessionQuery = new DatabaseSessionQuery(serverId, databaseService);
		IOseeDataAccessor<Session> accessor =
					new DatabaseSessionAccessor(serverId, sessionFactory, sessionQuery, databaseService);
		SessionCache sessionCache = new SessionCache(accessor);

		sessionManager =
					new SessionManagerImpl(serverId, sessionFactory, sessionQuery, sessionCache, authenticationManager);

		registration = context.registerService(ISessionManager.class.getName(), sessionManager, null);

		dataStoreSync = new SessionDataStoreSync(sessionCache);
		dataStoreSync.start();
	}

	@Override
	public void onDeActivate() {
		if (registration != null) {
			if (dataStoreSync != null) {
				dataStoreSync.stop();
			}
			registration.unregister();
		}
	}

}