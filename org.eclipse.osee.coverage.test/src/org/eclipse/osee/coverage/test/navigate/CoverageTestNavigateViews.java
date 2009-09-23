/*
 * Created on Feb 1, 2006
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.coverage.test.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.coverage.navigate.ICoverageNavigateItem;
import org.eclipse.osee.coverage.test.import1.CoverageTestImporter1;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestNavigateViews implements ICoverageNavigateItem {

   public CoverageTestNavigateViews() {
      super();
   }

   public List<XNavigateItem> getNavigateItems() throws OseeCoreException {

      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (AtsPlugin.areOSEEServicesAvailable().isFalse()) return items;

      items.add(new CoverageTestImporter1(null));

      return items;
   }

}
