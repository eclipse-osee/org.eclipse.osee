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
package org.eclipse.osee.ats.presenter.internal;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.api.search.AtsArtifactProvider;
import org.eclipse.osee.ats.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.orcs.OseeApi;

/**
 * @author John Misinco
 */
public class AtsPresenterFactoryImpl<T extends AtsSearchHeaderComponentInterface> implements AtsPresenterFactory<T> {

   private OseeApi oseeApi;

   public void setOseeApi(OseeApi oseeApi) {
      this.oseeApi = oseeApi;
   }

   @Override
   public AtsSearchPresenter<T> createInstance() {
      AtsArtifactProvider provider =
         AtsArtifactProviderFactory.createAtsArtifactProvider(oseeApi.getQueryFactory(null));
      AtsSearchPresenterImpl<T> instance = new AtsSearchPresenterImpl<T>(provider);
      return instance;
   }

}
