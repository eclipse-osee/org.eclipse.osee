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

package org.eclipse.osee.display.view.web.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.components.OseePagingComponent.PageSelectedEvent;
import org.eclipse.osee.display.view.web.components.OseePagingComponent.PageSelectedListener;
import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchResultsListComponent extends VerticalLayout implements SearchResultsListComponent, PageSelectedListener {

   private VerticalLayout mainLayout = new VerticalLayout();
   private VerticalLayout bottomSpacer = new VerticalLayout();
   private HorizontalLayout manySearchResultsHorizLayout = new HorizontalLayout();
   private OseePagingComponent pagingComponent = new OseePagingComponent();
   private List<OseeSearchResultComponent> resultList = new ArrayList<OseeSearchResultComponent>();

   public OseeSearchResultsListComponent() {
      this.setSizeFull();

      this.addComponent(manySearchResultsHorizLayout);
      manySearchResultsHorizLayout.setSizeUndefined();

      mainLayout.setMargin(false, false, false, true);
      Panel mainLayoutPanel = new Panel();
      mainLayoutPanel.setScrollable(true);
      mainLayoutPanel.getContent().setSizeUndefined();
      mainLayoutPanel.setContent(mainLayout);
      this.addComponent(mainLayoutPanel);
      mainLayoutPanel.setSizeFull();
      this.setExpandRatio(mainLayoutPanel, 1.0f);

      bottomSpacer.setSizeFull();
      mainLayout.addComponent(bottomSpacer);
      mainLayout.setExpandRatio(bottomSpacer, 1.0f);

      this.addComponent(pagingComponent);
      pagingComponent.addListener(this);
   }

   @Override
   public void clearAll() {
      resultList.clear();
      pagingComponent.gotoFirstPage();
      updateManySearchResultsLabel();
      updateSearchResultsLayout();
   }

   private void updateManySearchResultsLabel() {
      int manySearchResultComponents = resultList.size();
      manySearchResultsHorizLayout.removeAllComponents();

      Label manySearchResults = new Label(String.format("[%d] ", manySearchResultComponents));
      Label manySearchResults_suffix = new Label("search result(s) found.");
      manySearchResultsHorizLayout.addComponent(manySearchResults);
      manySearchResultsHorizLayout.addComponent(manySearchResults_suffix);
      manySearchResults.setSizeUndefined();
      manySearchResults_suffix.setSizeUndefined();
      manySearchResults.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH_MANY);

      pagingComponent.setManyItemsTotal(manySearchResultComponents);

   }

   private Collection<Integer> prevResultListIndices = new ArrayList<Integer>();

   private void updateSearchResultsLayout() {
      //if the list of currently visible items has not changed, then don't bother updating the layout
      pagingComponent.setManyItemsTotal(resultList.size());
      Collection<Integer> resultListIndices = pagingComponent.getCurrentVisibleItemIndices();
      if (!resultListIndices.equals(prevResultListIndices)) {
         //First, get a list of all the search results components currently in the layout
         Collection<Component> removeTheseComponents = new ArrayList<Component>();
         for (Iterator<Component> iter = mainLayout.getComponentIterator(); iter.hasNext();) {
            Component component = iter.next();
            if (component.getClass() == OseeSearchResultComponent.class) {
               removeTheseComponents.add(component);
            }
         }

         //Second, remove the search result components
         for (Component component : removeTheseComponents) {
            mainLayout.removeComponent(component);
         }

         //Next, add the result components to the layout that are on the current 'page'
         int spacerIndex = mainLayout.getComponentIndex(bottomSpacer);
         for (Integer i : resultListIndices) {
            try {
               OseeSearchResultComponent searchResultComp = resultList.get(i);
               mainLayout.addComponent(searchResultComp, 0);
            } catch (IndexOutOfBoundsException e) {
               System.out.println("OseeSearchResultsListComponent.updateSearchResultsLayout - CRITICAL ERROR: IndexOutOfBoundsException e");
            }
         }

         //Update prevResultListIndices
         prevResultListIndices.clear();
         prevResultListIndices.addAll(resultListIndices);
      }
   }

   @Override
   public SearchResultComponent createSearchResult() {
      OseeSearchResultComponent searchResultComp = new OseeSearchResultComponent();
      resultList.add(searchResultComp);
      //      int spacerIndex = mainLayout.getComponentIndex(bottomSpacer);
      //      mainLayout.addComponent(searchResultComp, spacerIndex);
      updateManySearchResultsLabel();
      updateSearchResultsLayout();

      return searchResultComp;
   }

   @Override
   public void setErrorMessage(String message) {
      Application app = this.getApplication();
      if (app != null) {
         Window mainWindow = app.getMainWindow();
         if (mainWindow != null) {
            mainWindow.showNotification(message, Notification.TYPE_ERROR_MESSAGE);
         } else {
            System.out.println("OseeSearchResultsListComponent.setErrorMessage - ERROR: Application.getMainWindow() returns null value.");
         }
      } else {
         System.out.println("OseeSearchResultsListComponent.setErrorMessage - ERROR: getApplication() returns null value.");
      }
   }

   @Override
   public void pageSelected(PageSelectedEvent source) {
      updateSearchResultsLayout();
   }
}
