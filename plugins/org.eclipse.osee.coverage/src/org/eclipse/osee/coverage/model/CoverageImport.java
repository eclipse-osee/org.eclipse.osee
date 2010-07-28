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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * Single import of coverage information that includes
 * 
 * @author Donald G. Dunne
 */
public class CoverageImport extends CoveragePackageBase {

   private Date runDate;
   private String location = "";
   private String blamName = "";
   private final List<File> importRecordFiles = new ArrayList<File>();
   private String importDirectory = null;

   public CoverageImport(String name) {
      this(name, new Date());
   }

   public CoverageImport(String name, Date runDate) {
      super(name, CoverageOptionManagerDefault.instance());
      this.runDate = runDate;
   }

   public Date getRunDate() {
      return runDate;
   }

   @Override
   public String getName() {
      return super.getName() + " - " + XDate.getDateStr(runDate, XDate.MMDDYYHHMM) + " - " + getCoverageItems().size() + " Coverage Items";
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

   @Override
   public String getLocation() {
      return location;
   }

   public void setLocation(String location) {
      this.location = location;
   }

   public String getBlamName() {
      return blamName;
   }

   public void setBlamName(String blamName) {
      this.blamName = blamName;
   }

   @Override
   public void loadKeyValues(KeyValueArtifact keyValueArtifact) {
      if (Strings.isValid(keyValueArtifact.getValue("location"))) {
         setLocation(keyValueArtifact.getValue("location"));
      }
      if (Strings.isValid(keyValueArtifact.getValue("blamName"))) {
         setBlamName(keyValueArtifact.getValue("blamName"));
      }
   }

   @Override
   public void saveKeyValues(KeyValueArtifact keyValueArtifact) {
      keyValueArtifact.setValue("location", location);
      keyValueArtifact.setValue("blamName", blamName);
   }

   public void setRunDate(Date runDate) {
      this.runDate = runDate;
   }

   @Override
   public Date getDate() {
      return getRunDate();
   }

   public void addImportRecordFile(File file) throws OseeArgumentException {
      if (!file.exists()) {
         throw new OseeArgumentException(String.format("Import Record file [%s] doesn't exist.", file));
      }
      importRecordFiles.add(file);
   }

   public List<File> getImportRecordFiles() {
      return importRecordFiles;
   }

   public String getImportDirectory() {
      return importDirectory;
   }

   public void setImportDirectory(String importDirectory) {
      this.importDirectory = importDirectory;
   }
}
