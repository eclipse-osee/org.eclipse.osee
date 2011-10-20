/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.view.web;

import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import com.vaadin.Application;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeUiApplication<T extends SearchHeaderComponent, K extends ViewSearchParameters> extends Application {

   protected final SearchNavigator navigator = createNavigator();
   protected SearchPresenter<T, K> searchPresenter;
   protected String requestedDataId = "";

   public OseeUiApplication(SearchPresenter<T, K> searchPresenter) {
      this.searchPresenter = searchPresenter;
   }

   public String getRequestedDataId() {
      return requestedDataId;
   }

   public void setRequestedDataId(String requestedDataId) {
      this.requestedDataId = requestedDataId;
   }

   @Override
   public void init() {
      setTheme("osee");
   }

   @Override
   public String getVersion() {
      Bundle bundle = FrameworkUtil.getBundle(this.getClass());
      return bundle.getVersion().toString();
   }

   protected SearchNavigator createNavigator() {
      return new OseeNavigator();
   }

   public SearchNavigator getNavigator() {
      return navigator;
   }

   public SearchPresenter<T, K> getSearchPresenter() {
      return searchPresenter;
   }

}
