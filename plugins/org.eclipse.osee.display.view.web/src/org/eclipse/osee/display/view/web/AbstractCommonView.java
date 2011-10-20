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
import org.eclipse.osee.display.api.search.SearchPresenter;
import org.eclipse.osee.display.view.web.components.OseeSearchHeaderComponent;
import org.eclipse.osee.vaadin.widgets.Navigator;
import com.vaadin.Application;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public abstract class AbstractCommonView extends VerticalLayout implements Navigator.View {

   protected OseeSearchHeaderComponent searchHeader;
   protected SearchPresenter searchPresenter;
   private boolean isLayoutComplete = false;

   @Override
   public void attach() {
      if (!isLayoutComplete) {
         initComponents();
         if (searchHeader != null) {
            setSizeFull();

            addComponent(searchHeader);
            setComponentAlignment(searchHeader, Alignment.TOP_LEFT);

            createLayout();

            callInit("");
         }
         isLayoutComplete = true;
      }
   }

   @Override
   public void navigateTo(String requestedDataId) {
      String url = "";
      if (searchPresenter != null) {
         try {
            OseeUiApplication<SearchHeaderComponent, ViewSearchParameters> app =
               (OseeUiApplication<SearchHeaderComponent, ViewSearchParameters>) getApplication();
            url = app.getRequestedDataId();
         } catch (Exception e) {
            System.out.println("AbstractCommonView.navigateTo - CRITICAL ERROR: casting threw an exception.");
         }
         callInit(url);
      }
   }

   protected abstract void callInit(String url);

   protected abstract void createLayout();

   protected abstract void initComponents();

   @Override
   public void init(Navigator navigator, Application application) {
      //Do nothing.
   }

   @Override
   public String getWarningForNavigatingFrom() {
      return null;
   }

}
