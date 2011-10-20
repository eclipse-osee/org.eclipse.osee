/*******************************************************************************
 * Copyright (c) October 21, 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.components.SearchHeaderComponent;
import org.eclipse.osee.display.api.data.ViewSearchParameters;
import org.eclipse.osee.display.api.search.SearchNavigator;
import org.eclipse.osee.display.api.search.SearchPresenter;

/**
 * @author Shawn F. Cook
 */
public abstract class MockSearchPresenter<T extends SearchHeaderComponent, K extends ViewSearchParameters> implements SearchPresenter<T, K> {

   @Override
   public void selectSearch(String url, ViewSearchParameters params, SearchNavigator atsNavigator) {
      System.out.println("");
   }
}
