/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rightsimport com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
he Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;

/**
 * @author Shawn F. Cook AppData contains thread-safe session-global data based
 *         on Vaadin demonstation:
 *         https://vaadin.com/book/-/page/advanced.global.html
 */
@SuppressWarnings("serial")
public class OseeAppData implements HttpServletRequestListener {

	protected final Application app; // For distinguishing between apps
	protected static ThreadLocal<OseeAppData> instance = new ThreadLocal<OseeAppData>();

	private final SearchNavigator navigator = createNavigator();
	private final SearchPresenter searchPresenter = createSearchPresenter();

	public OseeAppData(Application app) {
		this.app = app;

		// It's usable from now on in the current request
		instance.set(this);
	}

	protected SearchNavigator createNavigator() {
		return new OseeNavigator();
	}

	protected SearchPresenter createSearchPresenter() {
		return null;
	}

	public static SearchNavigator getNavigator() {
		return instance.get().navigator;
	}

	public static SearchPresenter getSearchPresenter() {
		return instance.get().searchPresenter;
	}

	// @return the current application instance
	public static OseeAppData getInstance() {
		return instance.get();
	}

	// Set the current application instance
	public static void setInstance(OseeAppData appdata) {
		instance.set(appdata);
	}

	@Override
	public void onRequestStart(HttpServletRequest request,
			HttpServletResponse response) {
		OseeAppData.setInstance(this);
	}

	@Override
	public void onRequestEnd(HttpServletRequest request,
			HttpServletResponse response) {
		instance.remove();

	}
}
