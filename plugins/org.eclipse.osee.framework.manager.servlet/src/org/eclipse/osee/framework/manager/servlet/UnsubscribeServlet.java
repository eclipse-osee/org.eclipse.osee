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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class UnsubscribeServlet extends OseeHttpServlet {

	private static final long serialVersionUID = -1515762009004235783L;

	public UnsubscribeServlet(Activator activator) {
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	private void handleError(HttpServletResponse response, int status,
			String message, Throwable ex) throws IOException {
		response.setStatus(status);
		response.setContentType("text/plain");
		OseeLog.log(Activator.class, Level.SEVERE, message, ex);
		response.getWriter().write(Lib.exceptionToString(ex));
	}

	@Override
	protected void checkAccessControl(HttpServletRequest request)
			throws OseeCoreException {
	}

}
