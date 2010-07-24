/*******************************************************************************
 * Copyright(c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.function;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.IOseeBranchService;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.ChangeReportRequest;
import org.eclipse.osee.framework.core.message.ChangeReportResponse;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction extends AbstractOperation {

	private final HttpServletRequest req;
	private final HttpServletResponse resp;
	private final IOseeBranchService branchService;
	private final IDataTranslationService translationService;

	public ChangeReportFunction(HttpServletRequest req, HttpServletResponse resp, IOseeBranchService branchService, IDataTranslationService translationService) {
		super("Branch Change Report", Activator.PLUGIN_ID);
		this.req = req;
		this.resp = resp;
		this.branchService = branchService;
		this.translationService = translationService;
	}

	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		ChangeReportRequest request =
					translationService.convert(req.getInputStream(), CoreTranslatorId.CHANGE_REPORT_REQUEST);

		ChangeReportResponse response = new ChangeReportResponse();
		IOperation subOp = branchService.getChanges(monitor, request, response);
		doSubWork(subOp, monitor, 0.90);

		resp.setStatus(HttpServletResponse.SC_ACCEPTED);
		resp.setContentType("text/xml");
		resp.setCharacterEncoding("UTF-8");
		InputStream inputStream = translationService.convertToStream(response, CoreTranslatorId.CHANGE_REPORT_RESPONSE);
		Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
		monitor.worked(calculateWork(0.10));
	}
}