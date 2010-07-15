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

import java.net.URI;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.manager.servlet.ats.AtsService;
import org.eclipse.osee.framework.manager.servlet.ats.AtsXmlMessages;
import org.eclipse.osee.framework.manager.servlet.ats.AtsXmlSearch;
import org.eclipse.osee.framework.manager.servlet.ats.XmlMessage;
import org.eclipse.osee.framework.manager.servlet.data.ServletResourceBridge;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class AtsServlet extends UnsecuredOseeHttpServlet {

	private static final long serialVersionUID = -9064467328387640427L;

	private final AtsService atsService;

	private final IResourceLocatorManager locatorManager;
	private final IResourceManager resourceManager;

	public AtsServlet(IResourceLocatorManager locatorManager, IResourceManager resourceManager) {
		super();
		this.locatorManager = locatorManager;
		this.resourceManager = resourceManager;
		AtsService.IResourceProvider provider = new ResourceProvider();
		AtsXmlSearch xmlSearch = new AtsXmlSearch();
		AtsXmlMessages messages = new AtsXmlMessages(new XmlMessage());
		this.atsService = new AtsService(provider, xmlSearch, messages, locatorManager, resourceManager);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		atsService.sendClient(request, response);
	}

	@Override
	protected void doPost(final HttpServletRequest request, HttpServletResponse response) {
		IResourceLocator locator = new IResourceLocator() {

			@Override
			public String getRawPath() {
				return request.getRequestURL().toString();
			}

			@Override
			public String getProtocol() {
				return request.getProtocol();
			}

			@Override
			public URI getLocation() {
				try {
					return new URL(request.getRequestURL().toString()).toURI();
				} catch (Exception ex) {
					return null;
				}
			}
		};
		atsService.performOperation(new ServletResourceBridge(request, locator), response);
	}

	private final class ResourceProvider implements AtsService.IResourceProvider {
		@Override
		public IResource getResource(String path) throws OseeCoreException {
			IResourceLocator locator = locatorManager.getResourceLocator(path);
			return resourceManager.acquire(locator, new Options());
		}
	}

}
