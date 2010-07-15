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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.ArtifactUtil;
import org.eclipse.osee.framework.manager.servlet.data.DefaultOseeArtifact;
import org.eclipse.osee.framework.manager.servlet.data.HttpArtifactFileInfo;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFileServlet extends UnsecuredOseeHttpServlet {

	private static final long serialVersionUID = -6334080268467740905L;

	private final IResourceLocatorManager locatorManager;
	private final IResourceManager resourceManager;

	public ArtifactFileServlet(IResourceLocatorManager locatorManager, IResourceManager resourceManager) {
		super();
		this.locatorManager = locatorManager;
		this.resourceManager = resourceManager;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			HttpArtifactFileInfo artifactFileInfo = null;

			String servletPath = request.getServletPath();
			//         System.out.println("servletPath: " + servletPath);
			if (!Strings.isValid(servletPath) || "/".equals(servletPath) || "/index".equals(servletPath)) {
				//            Enumeration<?> enumeration = request.getHeaderNames();
				//            while (enumeration.hasMoreElements()) {
				//               String headerField = (String) enumeration.nextElement();
				//               String value = request.getHeader(headerField);
				//               System.out.println(String.format("%s: %s", headerField, value));
				//            }

				Pair<String, String> defaultArtifact = DefaultOseeArtifact.get();
				if (defaultArtifact != null) {
					artifactFileInfo =
								new HttpArtifactFileInfo(defaultArtifact.getFirst(), null, defaultArtifact.getSecond());
				}
			} else {
				artifactFileInfo = new HttpArtifactFileInfo(request);
			}

			String uri = null;
			if (artifactFileInfo != null) {
				if (artifactFileInfo.isBranchNameValid()) {
					uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getBranchName());
				} else {
					uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getId());
				}
			}
			handleArtifactUri(locatorManager, resourceManager, request.getQueryString(), uri, response);
		} catch (NumberFormatException ex) {
			handleError(response, HttpServletResponse.SC_BAD_REQUEST,
						String.format("Invalid Branch Id: [%s]", request.getQueryString()), ex);
		} catch (Exception ex) {
			handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
						String.format("Unable to acquire resource: [%s]", request.getQueryString()), ex);
		} finally {
			response.flushBuffer();
		}
	}

	public static void handleArtifactUri(IResourceLocatorManager locatorManager, IResourceManager resourceManager, String request, String uri, HttpServletResponse response) throws OseeCoreException {
		boolean wasProcessed = false;
		if (Strings.isValid(uri)) {
			IResourceLocator locator = locatorManager.getResourceLocator(uri);
			Options options = new Options();
			options.put(StandardOptions.DecompressOnAquire.name(), true);
			IResource resource = resourceManager.acquire(locator, options);

			if (resource != null) {
				wasProcessed = true;

				InputStream inputStream = null;
				try {
					inputStream = resource.getContent();

					response.setStatus(HttpServletResponse.SC_OK);
					response.setContentLength(inputStream.available());
					response.setCharacterEncoding("ISO-8859-1");
					String mimeType = HttpURLConnection.guessContentTypeFromStream(inputStream);
					if (mimeType == null) {
						mimeType = HttpURLConnection.guessContentTypeFromName(resource.getLocation().toString());
						if (mimeType == null) {
							mimeType = "application/*";
						}
					}
					response.setContentType(mimeType);
					if (!mimeType.equals("text/html")) {
						response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());
					}
					Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());
					response.flushBuffer();
				} catch (IOException ex) {
					OseeExceptions.wrapAndThrow(ex);
				} finally {
					Lib.close(inputStream);
				}
			}
		}
		if (!wasProcessed) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.setContentType("text/plain");
			try {
				response.getWriter().write(String.format("Unable to find resource: [%s]", request));
			} catch (IOException ex) {
				OseeExceptions.wrapAndThrow(ex);
			}
		}
	}

	private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
		response.setStatus(status);
		response.setContentType("text/plain");
		OseeLog.log(Activator.class, Level.SEVERE, message, ex);
		response.getWriter().write(Lib.exceptionToString(ex));
	}
}
