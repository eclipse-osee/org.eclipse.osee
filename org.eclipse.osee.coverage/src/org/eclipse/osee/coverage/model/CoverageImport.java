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

import java.util.Date;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
public class CoverageImport extends CoveragePackageBase implements ICoverage {

   private Date runDate;
   private String location = "";
   private String blamName = "";

   public CoverageImport(String name) {
      this(name, new Date());
   }

   public CoverageImport(String name, Date runDate) {
      super(name);
      this.runDate = runDate;
   }

   public Date getRunDate() {
      return runDate;
   }

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
   public void loadKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException {
      if (Strings.isValid(keyValueArtifact.getValue("location"))) {
         setLocation(keyValueArtifact.getValue("location"));
      }
      if (Strings.isValid(keyValueArtifact.getValue("blamName"))) {
         setBlamName(keyValueArtifact.getValue("blamName"));
      }
   }

   @Override
   public void saveKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException {
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

}
