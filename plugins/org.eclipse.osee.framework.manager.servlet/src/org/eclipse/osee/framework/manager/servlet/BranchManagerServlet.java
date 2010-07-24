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
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.branch.management.IOseeBranchService;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.function.ChangeBranchArchiveStateFunction;
import org.eclipse.osee.framework.manager.servlet.function.ChangeBranchStateFunction;
import org.eclipse.osee.framework.manager.servlet.function.ChangeBranchTypeFunction;
import org.eclipse.osee.framework.manager.servlet.function.ChangeReportFunction;
import org.eclipse.osee.framework.manager.servlet.function.CreateBranchFunction;
import org.eclipse.osee.framework.manager.servlet.function.CreateCommitFunction;
import org.eclipse.osee.framework.manager.servlet.function.PurgeBranchFunction;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Andrew M Finkbeiner
 */
public class BranchManagerServlet extends SecureOseeHttpServlet {

	private static final long serialVersionUID = 226986283540461526L;

	private final IOseeBranchService branchService;
	private final IDataTranslationService translationService;

	public BranchManagerServlet(ISessionManager sessionManager, IOseeBranchService branchService, IDataTranslationService translationService) {
		super(sessionManager);
		this.branchService = branchService;
		this.translationService = translationService;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String rawFunction = req.getParameter("function");
			Function function = Function.fromString(rawFunction);
			IOperation op = null;
			switch (function) {
				case BRANCH_COMMIT:
					op = new CreateCommitFunction(req, resp, branchService, translationService);
					break;
				case CREATE_BRANCH:
					op = new CreateBranchFunction(req, resp, branchService, translationService);
					break;
				case CHANGE_REPORT:
					op = new ChangeReportFunction(req, resp, branchService, translationService);
					break;
				case PURGE_BRANCH:
					op = new PurgeBranchFunction(req, resp, branchService, translationService);
					break;
				case UPDATE_BRANCH_TYPE:
					op = new ChangeBranchTypeFunction(req, resp, branchService, translationService);
					break;
				case UPDATE_BRANCH_STATE:
					op = new ChangeBranchStateFunction(req, resp, branchService, translationService);
					break;
				case UPDATE_ARCHIVE_STATE:
					op = new ChangeBranchArchiveStateFunction(req, resp, branchService, translationService);
					break;
				default:
					throw new UnsupportedOperationException();
			}
			Operations.executeWorkAndCheckStatus(op, new LogProgressMonitor(), -1.0);
		} catch (Exception ex) {
			OseeLog.log(Activator.class, Level.SEVERE,
						String.format("Branch servlet request error: [%s]", req.toString()), ex);
			resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
			resp.setContentType("text/plain");
			resp.getWriter().write(Lib.exceptionToString(ex));
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}
}
