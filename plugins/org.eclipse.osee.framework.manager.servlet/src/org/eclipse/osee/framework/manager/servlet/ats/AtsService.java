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
package org.eclipse.osee.framework.manager.servlet.ats;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResource;
import org.w3c.dom.Node;

/**
 * @author Roberto E. Escobar
 */
public class AtsService {

	private static enum DataTypeEnum {
		PROGRAM,
		BUILD,
		WORKFLOW;

		public String asFileName() {
			return "ats." + this.name().toLowerCase() + ".data.xml";
		}
	}

	public static interface IResourceProvider {
		IResource getResource(String typeName) throws OseeCoreException;
	}

	private final IResourceProvider resourceProvider;
	private final AtsXmlSearch xmlSearch;
	private final AtsXmlMessages messages;

	public AtsService(IResourceProvider resourceProvider, AtsXmlSearch xmlSearch, AtsXmlMessages messages) {
		super();
		this.xmlSearch = xmlSearch;
		this.messages = messages;
		this.resourceProvider = resourceProvider;
	}

	public void performOperation(IResource resource, HttpServletResponse response) {
		try {
			Collection<OperationData> requests = OperationData.fromResource(resource);
			for (OperationData data : requests) {
				switch (data.getOperationType()) {
					case GET_BUILDS_BY_PROGRAM_ID:
						getBuilds(response, data.getProgramId());
						break;
					case GET_CHANGE_REPORTS_BY_IDS:
						getChangeReport(response, data.getItemIds());
						break;
					case GET_PROGRAMS:
						getPrograms(response);
						break;
					case GET_WORKFLOWS_BY_IDS:
						getWorkflowsById(response, data.getItemIds());
						break;
					case GET_WORKFLOWS_BY_PROGRAM_AND_BUILD_ID:
						getWorkflowsByProgramAndBuild(response, data.getProgramId(), data.getBuildId());
						break;
					default:
						throw new UnsupportedOperationException();
				}
			}
		} catch (Exception ex) {
			messages.sendError(response, ex);
		}
	}

	private IResource getResource(DataTypeEnum fileType) throws OseeCoreException {
		return getResource(fileType.asFileName());
	}

	private IResource getResource(String resource) throws OseeCoreException {
		String urlPath = String.format("%s://%s", AtsResourceLocatorProvider.PROTOCOL, resource);
		return resourceProvider.getResource(urlPath);
	}

	public void getPrograms(HttpServletResponse response) throws OseeCoreException {
		IResource resource = getResource(DataTypeEnum.PROGRAM);
		Collection<Node> nodes = xmlSearch.findPrograms(resource);
		messages.sendPrograms(response, nodes);
	}

	public void getBuilds(HttpServletResponse response, String programId) throws OseeCoreException {
		IResource resource = getResource(DataTypeEnum.BUILD);
		Collection<Node> nodes = xmlSearch.findBuildsByProgramId(resource, programId);
		messages.sendBuilds(response, nodes);
	}

	public void getWorkflowsById(HttpServletResponse response, String idSearch) throws OseeCoreException {
		IResource resource = getResource(DataTypeEnum.WORKFLOW);
		Collection<Node> nodes = xmlSearch.findWorkflowsById(resource, idSearch);
		messages.sendWorkflows(response, nodes);
	}

	public void getWorkflowsByProgramAndBuild(HttpServletResponse response, String programId, String buildId) throws OseeCoreException {
		IResource resource = getResource(DataTypeEnum.WORKFLOW);
		Collection<Node> nodes = xmlSearch.findWorkflowsByProgramAndBuild(resource, programId, buildId);
		messages.sendWorkflows(response, nodes);
	}

	public void getChangeReport(HttpServletResponse response, String idSearch) throws OseeCoreException {
		IResource resource = getResource(DataTypeEnum.WORKFLOW);
		Collection<Node> nodes = xmlSearch.findWorkflowsById(resource, idSearch);
		messages.sendChangeReports(response, nodes);
	}

	public void sendClient(HttpServletRequest request, HttpServletResponse response) {
		String urlPath = request.getParameter("url");
		boolean wasErrorSent = false;
		IResource resource = null;
		try {
			if (Strings.isValid(urlPath)) {
				resource = resourceProvider.getResource(urlPath);
			} else {
				String servletPath = request.getServletPath();
				urlPath = request.getRequestURI().replace(servletPath, "");
				resource = getResource(urlPath);
			}
		} catch (OseeCoreException ex) {
			messages.sendError(response, ex);
			wasErrorSent = true;
		}

		if (resource == null && !wasErrorSent) {
			messages.sendError(response, new Exception(String.format("Resource not found - [%s]", urlPath)));
		} else {
			messages.sendResource(response, resource.getName(), resource);
		}
	}
}
