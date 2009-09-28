/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.coverage.editor.xcover.ICoverageEditorProvider;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * Single import of coverage information that includes
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport implements ICoverageEditorProvider {

   private final String guid = GUID.create();
   private final Date runDate;
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final List<TestUnit> testUnits = new ArrayList<TestUnit>();
   private final XResultData logResultData = new XResultData();

   public CoverageImport() {
      this(new Date());
   }

   public CoverageImport(Date runDate) {
      super();
      this.runDate = runDate;
   }

   public void addTestUnit(TestUnit testUnit) {
      testUnits.add(testUnit);
   }

   public List<TestUnit> getTestUnits() {
      return testUnits;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnits.add(coverageUnit);
   }

   public List<CoverageUnit> getCoverageUnits() {
      return coverageUnits;
   }

   public List<CoverageItem> getCoverageItems() {
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageUnit coverageUnit : coverageUnits) {
         items.addAll(coverageUnit.getCoverageItems(true));
      }
      return items;
   }

   public int getPercentCoverage() {
      if (getCoverageItems().size() == 0 || getCoverageItemsCovered().size() == 0) return 0;
      Double percent = new Double(getCoverageItemsCovered().size());
      percent = percent / getCoverageItems().size();
      percent = percent * 100;
      return percent.intValue();
   }

   public List<CoverageItem> getCoverageItemsCovered() {
      return getCoverageItemsCovered(CoverageMethodEnum.None, CoverageMethodEnum.Unknown);
   }

   public List<CoverageItem> getCoverageItemsCovered(CoverageMethodEnum... coverageMethodEnum) {
      List<CoverageMethodEnum> coverageMethods = Collections.getAggregate(coverageMethodEnum);
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems()) {
         if (coverageMethods.contains(coverageItem.getCoverageMethod())) {
            items.add(coverageItem);
         }
      }
      return items;
   }

   public String getGuid() {
      return guid;
   }

   public Date getRunDate() {
      return runDate;
   }

   public void setCoverageUnits(List<CoverageUnit> coverageUnits) {
      this.coverageUnits = coverageUnits;
   }

   public String getName() {
      return "Coverage Import - " + XDate.getDateStr(runDate, XDate.MMDDYYHHMM);
   }

   public XResultData getLog() {
      return logResultData;
   }

   @Override
   public Object getInput() {
      return getCoverageUnits();
   }

   @Override
   public OseeImage getTitleImage() {
      return CoverageImage.COVERAGE;
   }
}
