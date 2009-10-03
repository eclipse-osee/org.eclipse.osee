/*
 * Created on Sep 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.TestUnit;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageTabProvider {

   public String getName();

   public Collection<? extends ICoverageEditorItem> getCoverageEditorItems();

   public OseeImage getTitleImage();

   public List<TestUnit> getTestUnits();

   public List<CoverageUnit> getCoverageUnits();

   public List<CoverageItem> getCoverageItems();

   public int getPercentCoverage();

   public List<CoverageItem> getCoverageItemsCovered();

   public List<CoverageItem> getCoverageItemsCovered(CoverageMethodEnum... coverageMethodEnum);

   public Date getRunDate();

   public XResultData getLog();

   public void getOverviewHtmlHeader(XResultData xResultData);

   public boolean isAssignable();

}
