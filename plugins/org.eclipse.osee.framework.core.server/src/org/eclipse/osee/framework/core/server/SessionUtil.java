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
package org.eclipse.osee.framework.core.server;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.Strings;

public final class SessionUtil {

	private SessionUtil() {

	}

	public static boolean isAlive(ISession session) throws OseeCoreException {
		boolean wasAlive = false;
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			URL url =
						new URL(String.format("http://%s:%s/osee/request?cmd=pingId", session.getClientAddress(),
									session.getClientPort()));
			AcquireResult result = HttpProcessor.acquire(url, outputStream);
			if (result.wasSuccessful()) {
				String sessionId = outputStream.toString(result.getEncoding());
				if (Strings.isValid(sessionId)) {
					wasAlive = sessionId.contains(session.getGuid());
				}
			}
		} catch (Exception ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
		return wasAlive;
	}
}
