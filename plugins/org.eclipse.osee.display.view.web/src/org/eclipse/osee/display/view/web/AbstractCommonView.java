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

import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.components.ComponentUtility;
import org.eclipse.osee.display.view.web.components.OseeSearchHeaderComponent;
import org.eclipse.osee.vaadin.widgets.HasViewTitle;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public abstract class AbstractCommonView extends VerticalLayout implements Navigator.View, HasViewTitle {

   protected OseeSearchHeaderComponent searchHeader;
   private SearchPresenter<?, ?> searchPresenter;

   private boolean isLayoutComplete = false;
   private String initialUrl;

   @Override
   public void init(Navigator navigator, Application application) {
      searchPresenter = ComponentUtility.getPresenter(this);
   }

   @Override
   public final void attach() {
      if (!isLayoutComplete) {
         initComponents();
         if (searchHeader != null) {
            setSizeFull();

            addComponent(searchHeader);
            setComponentAlignment(searchHeader, Alignment.TOP_LEFT);

            createLayout();

            callInit(initialUrl);
         }
         isLayoutComplete = true;
      }
   }

   @Override
   public final void navigateTo(String requestedDataId) {
      ComponentUtility.setUrl(this, requestedDataId);
      searchPresenter = ComponentUtility.getPresenter(this);
      initialUrl = requestedDataId;
      if (searchPresenter != null) {
         callInit(initialUrl);
      }
   }

   protected abstract void callInit(String url);

   protected abstract void createLayout();

   protected abstract void initComponents();

   protected OseeSearchHeaderComponent getSearchHeader() {
      return searchHeader;
   }

   protected void setSearchHeader(OseeSearchHeaderComponent searchHeader) {
      this.searchHeader = searchHeader;
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

}
