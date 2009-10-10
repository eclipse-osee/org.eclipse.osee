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
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.ICoverageEditorProvider;
import org.eclipse.osee.coverage.editor.ICoverageTabProvider;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.GeneralData;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.swt.graphics.Image;

/**
 * Single import of coverage information that includes
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport implements ICoverageEditorProvider, ICoverageEditorItem, ICoverageTabProvider {

   private String guid = GUID.create();
   private Date runDate;
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final List<TestUnit> testUnits = new ArrayList<TestUnit>();
   private final XResultData logResultData = new XResultData();
   private String location = "";
   private String blamName = "";
   private String name;
   private Artifact artifact;

   public CoverageImport(String name) {
      this(name, new Date());
   }

   public CoverageImport(String name, Date runDate) {
      super();
      this.name = name;
      this.runDate = runDate;
   }

   public void addTestUnit(TestUnit testUnit) {
      testUnits.add(testUnit);
   }

   public List<TestUnit> getTestUnits() {
      return testUnits;
   }

   public void addCoverageUnit(CoverageUnit coverageUnit) {
      coverageUnit.setParentCoverageEditorItem(this);
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
      List<CoverageItem> items = new ArrayList<CoverageItem>();
      for (CoverageItem coverageItem : getCoverageItems()) {
         if (coverageItem.getCoverageMethod() != CoverageMethodEnum.Not_Covered) {
            items.add(coverageItem);
         }
      }
      return items;
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
      return name + " - " + XDate.getDateStr(runDate, XDate.MMDDYYHHMM) + " - " + getCoverageItems().size() + " Coverage Items";
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
      return CoverageImage.COVERAGE_IMPORT;
   }

   @Override
   public void getOverviewHtmlHeader(XResultData xResultData) {
      xResultData.log(AHTML.bold("Coverage Import for " + XDate.getDateStr(getRunDate(), XDate.HHMMSSSS)) + AHTML.newline());
      xResultData.log(AHTML.getLabelValueStr("Location", location));
      if (Strings.isValid(getBlamName())) {
         xResultData.log(AHTML.getLabelValueStr("Blam Name", getBlamName()));
      }
      xResultData.log(AHTML.getLabelValueStr("Run Date", XDate.getDateStr(getRunDate(), XDate.MMDDYYHHMM)));
   }

   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   @Override
   public boolean isImportAllowed() {
      return false;
   }

   @Override
   public boolean isAssignable() {
      return false;
   }

   public String getBlamName() {
      return blamName;
   }

   public void setBlamName(String blamName) {
      this.blamName = blamName;
   }

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null && create) {
         artifact =
               ArtifactTypeManager.addArtifact(GeneralData.ARTIFACT_TYPE, BranchManager.getCommonBranch(), guid, null);
      }
      return artifact;
   }

   public void load() throws OseeCoreException {
      coverageUnits.clear();
      getArtifact(false);
      if (artifact != null) {
         setName(artifact.getName());
         KeyValueArtifact keyValueArtifact =
               new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         if (Strings.isValid(keyValueArtifact.getValue("location"))) {
            setLocation(keyValueArtifact.getValue("location"));
         }
         if (Strings.isValid(keyValueArtifact.getValue("blamName"))) {
            setBlamName(keyValueArtifact.getValue("blamName"));
         }
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.getArtifactTypeName().equals(GeneralData.ARTIFACT_TYPE)) {
               coverageUnits.add(new CoverageUnit(childArt));
            }
         }
      }
   }

   public void save(SkynetTransaction transaction) throws OseeCoreException {
      List<String> items = new ArrayList<String>();
      getArtifact(true);
      artifact.setName(getName());
      artifact.setAttributeValues(GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME, items);
      KeyValueArtifact keyValueArtifact =
            new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
      keyValueArtifact.setValue("location", location);
      keyValueArtifact.setValue("blamName", blamName);
      keyValueArtifact.save();
      for (CoverageUnit coverageUnit : coverageUnits) {
         coverageUnit.save(transaction);
      }
      artifact.persist(transaction);
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setRunDate(Date runDate) {
      this.runDate = runDate;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public Object[] getChildren() {
      return null;
   }

   @Override
   public Image getCoverageEditorImage(XViewerColumn xCol) {
      return null;
   }

   @Override
   public String getCoverageEditorValue(XViewerColumn xCol) {
      return null;
   }

   @Override
   public OseeImage getOseeImage() {
      return null;
   }

   @Override
   public ICoverageEditorItem getParent() {
      return null;
   }

   @Override
   public boolean isCompleted() {
      return false;
   }

   @Override
   public Result isEditable() {
      return Result.FalseResult;
   }

   @Override
   public boolean isCovered() {
      for (CoverageUnit coverageUnit : coverageUnits) {
         if (!coverageUnit.isCovered()) return false;
      }
      return true;
   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   @Override
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public String getNotes() {
      return null;
   }

}
