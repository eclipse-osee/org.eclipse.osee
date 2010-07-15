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

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;

/**
 * @author Roberto E. Escobar
 */
public final class SessionCache extends AbstractOseeCache<Session> {

	public SessionCache(IOseeDataAccessor<Session> dataAccessor) {
		super(OseeCacheEnum.SESSION_CACHE, dataAccessor, true);
	}

	@Override
	public synchronized Collection<Session> getRawValues() throws OseeCoreException {
		return super.getRawValues();
	}
}
