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
import org.eclipse.osee.coverage.CoverageManager;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.editor.ICoverageEditorProvider;
import org.eclipse.osee.coverage.editor.ICoverageTabProvider;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
 * Effort of coverage that includes multiple imports, reports, exports and metrics
 * 
 * @author Donald G. Dunne
 */
public class CoveragePackage implements ISaveable, ICoverageEditorItem, ICoverageEditorProvider, ICoverageTabProvider {

   public static String ARTIFACT_NAME = "Coverage Package";
   private String guid = GUID.create();
   private String name;
   private boolean editable = true;
   private Date creationDate;
   private final List<CoverageImport> coverageImports = new ArrayList<CoverageImport>();
   private List<CoverageUnit> coverageUnits = new ArrayList<CoverageUnit>();
   private final List<TestUnit> testUnits = new ArrayList<TestUnit>();
   private final XResultData logResultData = new XResultData();
   private Artifact artifact;

   public CoveragePackage(String name) {
      this(name, new Date());
   }

   public CoveragePackage(String name, Date runDate) {
      super();
      this.creationDate = runDate;
      this.name = name;
      CoverageManager.cache(this);
   }

   public CoveragePackage(Artifact artifact) {
      super();
      this.artifact = artifact;
      try {
         load();
         CoverageManager.cache(this);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
      }
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
      return creationDate;
   }

   public void setCoverageUnits(List<CoverageUnit> coverageUnits) {
      this.coverageUnits = coverageUnits;
   }

   public String getName() {
      return name;
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
      return isEditable().isTrue();
   }

   @Override
   public boolean isAssignable() {
      return isEditable().isTrue();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      CoveragePackage other = (CoveragePackage) obj;
      if (guid == null) {
         if (other.guid != null) return false;
      } else if (!guid.equals(other.guid)) return false;
      return true;
   }

   public Artifact getArtifact(boolean create) throws OseeCoreException {
      if (artifact == null && create) {
         artifact = ArtifactTypeManager.addArtifact(ARTIFACT_NAME, BranchManager.getCommonBranch(), guid, null);
      }
      return artifact;
   }

   public void delete(SkynetTransaction transaction, boolean purge) throws OseeCoreException {
      if (getArtifact(false) != null) {
         if (purge)
            getArtifact(false).purgeFromBranch();
         else
            getArtifact(false).deleteAndPersist(transaction);
      }
      for (CoverageUnit coverageUnit : coverageUnits) {
         coverageUnit.delete(transaction, purge);
      }
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
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
   public String getAssignees() throws OseeCoreException {
      return "";
   }

   @Override
   public boolean isCompleted() {
      return false;
   }

   @Override
   public boolean isCovered() {
      for (CoverageUnit coverageUnit : coverageUnits) {
         if (!coverageUnit.isCovered()) return false;
      }
      return true;
   }

   @Override
   public Result isEditable() {
      if (!editable) return new Result("CoveragePackage locked for edits.");
      return Result.TrueResult;

   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   public String getLocation() {
      return "";
   }

   @Override
   public String getNamespace() {
      return "";
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public void save(SkynetTransaction transaction) throws OseeCoreException {
      getArtifact(true);
      System.out.println("save coveragePackage " + guid);

      artifact.setName(getName());
      KeyValueArtifact keyValueArtifact =
            new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
      keyValueArtifact.setValue("date", String.valueOf(creationDate.getTime()));
      keyValueArtifact.setValue("editable", String.valueOf(String.valueOf(editable)));
      keyValueArtifact.save();
      for (CoverageUnit coverageUnit : coverageUnits) {
         coverageUnit.save(transaction);
         artifact.addChild(artifact);
      }
      artifact.persist(transaction);
   }

   @Override
   public Result save() {
      try {
         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getCommonBranch());
         save(transaction);
         transaction.execute();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         return new Result("Save Failed: " + ex.getLocalizedMessage());
      }
      return Result.TrueResult;
   }

   public void load() throws OseeCoreException {
      coverageUnits.clear();
      getArtifact(false);
      if (artifact != null) {
         setName(artifact.getName());
         KeyValueArtifact keyValueArtifact =
               new KeyValueArtifact(artifact, GeneralData.GENERAL_STRING_ATTRIBUTE_TYPE_NAME);
         if (Strings.isValid(keyValueArtifact.getValue("date"))) {
            Date date = new Date();
            date.setTime(new Long(keyValueArtifact.getValue("date")).longValue());
            setCreationDate(date);
         }
         if (Strings.isValid(keyValueArtifact.getValue("editable"))) {
            setEditable(keyValueArtifact.getValue("editable").equals("true"));
         }
         for (Artifact childArt : artifact.getChildren()) {
            if (childArt.getArtifactTypeName().equals(CoverageUnit.ARTIFACT_NAME)) {
               addCoverageUnit(new CoverageUnit(childArt));
            }
         }
      }
   }

   @Override
   public String getNotes() {
      return null;
   }

}
