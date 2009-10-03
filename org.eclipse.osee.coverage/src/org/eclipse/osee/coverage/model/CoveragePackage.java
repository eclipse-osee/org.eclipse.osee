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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.ICoverageEditorProvider;
import org.eclipse.osee.coverage.editor.ICoverageTabProvider;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * Effort of coverage that includes multiple imports, reports, exports and metrics
 * 
 * @author Donald G. Dunne
 */
public class CoveragePackage implements ICoverageEditorProvider, ICoverageTabProvider {

   private final String guid = GUID.create();
   private String name;
   private final Date creationDate;
   private final List<CoverageImport> coverageImports = new ArrayList<CoverageImport>();
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final List<TestUnit> testUnits = new ArrayList<TestUnit>();
   private final XResultData logResultData = new XResultData();

   public CoveragePackage() {
      this("Coverage Package", new Date());
   }

   public CoveragePackage(String name, Date runDate) {
      super();
      this.creationDate = runDate;
      this.name = name;
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

   public void addCoverageImport(CoverageImport CoverageImport) {
      coverageImports.add(CoverageImport);
   }

   public List<CoverageImport> getCoverageImports() {
      return coverageImports;
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
      return creationDate;
   }

   public void setCoverageUnits(List<CoverageUnit> coverageUnits) {
      this.coverageUnits = coverageUnits;
   }

   public String getName() {
      return name + " - " + XDate.getDateStr(creationDate, XDate.MMDDYYHHMM) + " - " + getCoverageItems().size() + " Coverage Items";
   }

   public XResultData getLog() {
      return logResultData;
   }

   @Override
   public Collection<? extends ICoverageEditorItem> getCoverageEditorItems() {
      return getCoverageUnits();
   }

   @Override
   public OseeImage getTitleImage() {
      return CoverageImage.COVERAGE_PACKAGE;
   }

   @Override
   public void getOverviewHtmlHeader(XResultData xResultData) {
      xResultData.log(AHTML.bold("Coverage Package " + getName()) + AHTML.newline());
      xResultData.log(AHTML.getLabelValueStr("Creation Date", XDate.getDateStr(getRunDate(), XDate.MMDDYYHHMM)));

      xResultData.log(AHTML.getLabelValueStr("Number of Coverage Imports", String.valueOf(coverageImports.size())));
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean isImportAllowed() {
      return true;
   }

   @Override
   public boolean isAssignable() {
      return true;
   }

}
