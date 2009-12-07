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

public class CoveragePackage extends CoveragePackageBase implements ICoverage {

   Date creationDate;

   public CoveragePackage(String name) {
      this(name, new Date());
   }

   public CoveragePackage(String name, Date runDate) {
      super(name);
      this.creationDate = runDate;
   }

   public void clearCoverageUnits() {
      coverageUnits.clear();
   }

   public Date getRunDate() {
      return creationDate;
   }

   @Override
   public void getOverviewHtmlHeader(XResultData xResultData) {
      xResultData.log(AHTML.bold("Coverage Package " + getName() + " as of " + XDate.getDateStr(new Date(),
            XDate.MMDDYYHHMM)) + AHTML.newline());
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   @Override
   public void saveKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException {
      keyValueArtifact.setValue("date", String.valueOf(creationDate.getTime()));
   }

   @Override
   public void loadKeyValues(KeyValueArtifact keyValueArtifact) throws OseeCoreException {
      if (Strings.isValid(keyValueArtifact.getValue("date"))) {
         Date date = new Date();
         date.setTime(new Long(keyValueArtifact.getValue("date")).longValue());
         setCreationDate(date);
      }
   }

   @Override
   public Date getDate() {
      return getRunDate();
   }

}
