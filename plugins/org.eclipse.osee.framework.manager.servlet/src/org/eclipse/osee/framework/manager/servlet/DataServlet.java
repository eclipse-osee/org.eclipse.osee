/*
 * Created on Jul 9, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.ArtifactUtil;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

public class DataServlet extends OseeHttpServlet {

	private static final long serialVersionUID = -1399699606153734250L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String urlRequest = request.getRequestURI();
		try {
			handleUriRequest(urlRequest, response);
		} catch (OseeCoreException ex) {
			handleError(response, HttpURLConnection.HTTP_INTERNAL_ERROR, "", ex);
		}
	}

	private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
		response.setContentType("text/plain");
		OseeLog.log(Activator.class, Level.SEVERE, message, ex);
		response.sendError(status, Lib.exceptionToString(ex));
	}

	@Override
	protected void checkAccessControl(HttpServletRequest request) {
		// Open to all
	}

	public static void handleUriRequest(String urlRequest, HttpServletResponse response) throws OseeCoreException {
		UrlParser parser = new UrlParser();
		parser.parse(urlRequest);
		String branchGuid = parser.getAttribute("branch");
		String artifactGuid = parser.getAttribute("artifact");
		String uri = ArtifactUtil.getUriByGuids(branchGuid, artifactGuid);
		ArtifactFileServlet.handleArtifactUri(urlRequest, uri, response);
	}

	private static final class UrlParser {
		private final List<String> contexts;

		public UrlParser() {
			this.contexts = new ArrayList<String>();
		}

		public void parse(String urlPath) {
			contexts.clear();
			if (Strings.isValid(urlPath)) {
				String[] items = urlPath.split("/");
				for (String item : items) {
					contexts.add(item);
				}
			}
		}

		public String getAttribute(String key) throws OseeCoreException {
			Conditions.checkNotNull(key, "attribute");
			int contextCount = contexts.size();
			for (int index = 0; index < contextCount; index++) {
				String context = contexts.get(index);
				if (context.equals(key)) {
					if (index + 1 < contextCount) {
						return contexts.get(index + 1);
					}
				}
			}
			throw new OseeNotFoundException(String.format("Unable to find [%s]", key));
		}
	}
}
