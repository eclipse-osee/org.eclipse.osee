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
package org.eclipse.osee.ats.presenter.mock;

import org.eclipse.osee.ats.presenter.mock.internal.MockAtsWebSearchPresenter;
import org.eclipse.osee.ats.ui.api.data.AtsSearchParameters;
import org.eclipse.osee.ats.ui.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.ui.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.ui.api.view.AtsSearchHeaderComponent;
import org.eclipse.osee.orcs.ApplicationContext;

/**
 * @author John Misinco
 */
public class MockAtsPresenterFactory implements AtsPresenterFactory<AtsSearchHeaderComponent, AtsSearchParameters> {

   @Override
   public AtsSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters> createInstance(ApplicationContext context) {
      return new MockAtsWebSearchPresenter<AtsSearchHeaderComponent, AtsSearchParameters>();
   }

}
