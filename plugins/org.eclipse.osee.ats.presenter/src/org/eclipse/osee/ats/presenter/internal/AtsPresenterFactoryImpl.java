/*
 * Created on Oct 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.presenter.internal;

import org.eclipse.osee.ats.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.display.presenter.ArtifactProvider;
import org.eclipse.osee.display.presenter.ArtifactProviderImpl;
import org.eclipse.osee.orcs.OseeApi;

public class AtsPresenterFactoryImpl implements AtsPresenterFactory {

   private OseeApi oseeApi;

   public void setOseeApi(OseeApi oseeApi) {
      this.oseeApi = oseeApi;
   }

   @Override
   public AtsSearchPresenter createInstance() {
      ArtifactProvider provider = new ArtifactProviderImpl(oseeApi.getQueryFactory(null));
      AtsSearchPresenterImpl instance = new AtsSearchPresenterImpl(provider);
      return instance;
   }

}
