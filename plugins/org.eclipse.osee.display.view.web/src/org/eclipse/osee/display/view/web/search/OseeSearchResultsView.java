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
package org.eclipse.osee.display.view.web.search;

import org.eclipse.osee.display.view.web.AbstractCommonView;
import org.eclipse.osee.display.view.web.components.OseeLeftMarginContainer;
import org.eclipse.osee.display.view.web.components.OseeSearchResultsListComponent;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public abstract class OseeSearchResultsView extends AbstractCommonView {

   protected OseeSearchResultsListComponent searchResultsListComponent = new OseeSearchResultsListComponent();

   protected void createLayout() {
      OseeLeftMarginContainer leftMargContainer = new OseeLeftMarginContainer();
      leftMargContainer.setSizeFull();
      searchResultsListComponent.setSizeFull();

      leftMargContainer.addComponent(searchResultsListComponent);
      addComponent(leftMargContainer);

      leftMargContainer.setExpandRatio(searchResultsListComponent, 1.0f);
      setExpandRatio(leftMargContainer, 1.0f);
   }
}
