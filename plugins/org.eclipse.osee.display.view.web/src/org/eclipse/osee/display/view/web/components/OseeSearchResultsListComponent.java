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
import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import org.eclipse.osee.display.view.web.CssConstants;
import org.eclipse.osee.display.view.web.components.OseePagingComponent.PageSelectedEvent;
import org.eclipse.osee.display.view.web.components.OseePagingComponent.PageSelectedListener;
import com.vaadin.Application;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
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
   private SearchHeaderComponent searchHeaderComponent;
   private final CheckBox showVerboseCheckBox = new CheckBox("Show Detailed Results", false);
   private final ComboBox manyResultsComboBox = new ComboBox();
   private final int INIT_MANY_RES_PER_PAGE = 15;

   public OseeSearchResultsListComponent() {
      this(null);
   }

   public OseeSearchResultsListComponent(SearchHeaderComponent searchHeaderComponent) {
      this.setSearchHeaderComponent(searchHeaderComponent);
      this.setSizeFull();

      manySearchResultsHorizLayout.setSizeUndefined();

      Label spacer = new Label();
      spacer.setHeight(5, UNITS_PIXELS);

      mainLayout.setMargin(false, false, false, true);
      Panel mainLayoutPanel = new Panel();
      mainLayoutPanel.setScrollable(true);
      mainLayoutPanel.getContent().setSizeUndefined();
      mainLayoutPanel.setContent(mainLayout);
      mainLayoutPanel.setSizeFull();

      bottomSpacer.setSizeFull();
      mainLayout.addComponent(bottomSpacer);
      mainLayout.setExpandRatio(bottomSpacer, 1.0f);

      pagingComponent.addListener(this);

      this.addComponent(manySearchResultsHorizLayout);
      this.addComponent(spacer);
      this.addComponent(mainLayoutPanel);
      this.setExpandRatio(mainLayoutPanel, 1.0f);
      this.addComponent(pagingComponent);

      showVerboseCheckBox.setImmediate(true);
      final boolean showVerbose = showVerboseCheckBox.toString().equalsIgnoreCase("true");
      showVerboseCheckBox.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (OseeSearchResultsListComponent.this.searchHeaderComponent != null) {
               OseeSearchResultsListComponent.this.searchHeaderComponent.setShowVerboseSearchResults(showVerbose);
            }
            for (OseeSearchResultComponent resultComp : resultList) {
               resultComp.setShowVerboseSearchResults(showVerbose);
            }
            updateSearchResultsLayout();
         }
      });
      if (searchHeaderComponent != null) {
         searchHeaderComponent.setShowVerboseSearchResults(showVerbose);
      }

      manyResultsComboBox.setImmediate(true);
      manyResultsComboBox.setTextInputAllowed(false);
      manyResultsComboBox.setNullSelectionAllowed(false);
      manyResultsComboBox.addItem("5");
      manyResultsComboBox.addItem("15");
      manyResultsComboBox.addItem("50");
      manyResultsComboBox.addItem("100");
      manyResultsComboBox.addItem("All");
      manyResultsComboBox.setValue((new Integer(INIT_MANY_RES_PER_PAGE)).toString());
      pagingComponent.setManyItemsPerPage(INIT_MANY_RES_PER_PAGE);
      manyResultsComboBox.setWidth(50, UNITS_PIXELS);
      manyResultsComboBox.addListener(new Property.ValueChangeListener() {
         @Override
         public void valueChange(ValueChangeEvent event) {
            if (pagingComponent != null) {
               String manyItemsPerPage_str = (String) manyResultsComboBox.getValue();
               if (manyItemsPerPage_str.equalsIgnoreCase("All")) {
                  pagingComponent.setAllItemsPerPage();
               } else {
                  int manyItemsPerPage = Integer.parseInt(manyItemsPerPage_str);
                  pagingComponent.setManyItemsPerPage(manyItemsPerPage);
               }
               updateSearchResultsLayout();
            }
         }
      });
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

      Label manySearchResults = new Label(String.format("[%d] ", manySearchResultComponents));
      Label spacer = new Label();
      spacer.setWidth(5, UNITS_PIXELS);
      Label manySearchResults_suffix = new Label("Results Found");
      manySearchResults.setSizeUndefined();
      manySearchResults_suffix.setSizeUndefined();
      manySearchResults.setStyleName(CssConstants.OSEE_SEARCHRESULT_MATCH_MANY);

      manySearchResultsHorizLayout.removeAllComponents();
      manySearchResultsHorizLayout.addComponent(manySearchResults);
      manySearchResultsHorizLayout.addComponent(spacer);
      manySearchResultsHorizLayout.addComponent(manySearchResults_suffix);

      if (manySearchResultComponents > 0 && searchHeaderComponent != null) {
         searchHeaderComponent.setShowVerboseSearchResults(showVerboseCheckBox.toString().equalsIgnoreCase("true"));
         Label spacer1 = new Label();
         spacer1.setWidth(30, UNITS_PIXELS);
         manySearchResultsHorizLayout.addComponent(spacer1);
         manySearchResultsHorizLayout.addComponent(showVerboseCheckBox);
      }

      Label spacer2 = new Label();
      spacer2.setWidth(30, UNITS_PIXELS);
      Label spacer3 = new Label();
      spacer3.setWidth(5, UNITS_PIXELS);
      Label manyResultsLabel = new Label("Results Per Page");
      manySearchResultsHorizLayout.addComponent(spacer2);
      manySearchResultsHorizLayout.addComponent(manyResultsComboBox);
      manySearchResultsHorizLayout.setComponentAlignment(manyResultsComboBox, Alignment.TOP_CENTER);
      manySearchResultsHorizLayout.addComponent(spacer3);
      manySearchResultsHorizLayout.addComponent(manyResultsLabel);

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

   public SearchHeaderComponent getSearchHeaderComponent() {
      return searchHeaderComponent;
   }

   public void setSearchHeaderComponent(SearchHeaderComponent searchHeaderComponent) {
      this.searchHeaderComponent = searchHeaderComponent;
   }
}
