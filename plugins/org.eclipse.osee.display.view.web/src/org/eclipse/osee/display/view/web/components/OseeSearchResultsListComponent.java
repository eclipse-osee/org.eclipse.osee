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
import org.eclipse.osee.display.api.components.SearchResultComponent;
import org.eclipse.osee.display.api.components.SearchResultsListComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class OseeSearchResultsListComponent extends VerticalLayout implements SearchResultsListComponent {

   VerticalLayout bottomSpacer = new VerticalLayout();

   public OseeSearchResultsListComponent() {
      bottomSpacer.setSizeFull();
      addComponent(bottomSpacer);
      setExpandRatio(bottomSpacer, 1.0f);
   }

   @Override
   public void clearAll() {
      Collection<Component> removeTheseComponents = new ArrayList<Component>();
      for (Iterator<Component> iter = getComponentIterator(); iter.hasNext();) {
         Component component = iter.next();
         if (component.getClass() == OseeSearchResultComponent.class) {
            removeTheseComponents.add(component);
         }
      }

      //Remove the components
      for (Component component : removeTheseComponents) {
         removeComponent(component);
      }
   }

   @Override
   public SearchResultComponent createSearchResult() {
      OseeSearchResultComponent searchResultComp = new OseeSearchResultComponent();
      int spacerIndex = this.getComponentIndex(bottomSpacer);
      addComponent(searchResultComp, spacerIndex);
      return searchResultComp;
   }

   @Override
   public void setErrorMessage(String message) {
   }
}
