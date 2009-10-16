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
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.coverage.util.CoverageImage;
import org.eclipse.osee.coverage.util.ISaveable;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.KeyValueArtifact;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

public class CoveragePackage extends CoveragePackageBase implements ISaveable, ICoverageUnitProvider, ICoverageEditorItem {

   public static String ARTIFACT_NAME = "Coverage Package";
   private Date creationDate;
   private final List<CoverageImport> coverageImports = new ArrayList<CoverageImport>();

   public CoveragePackage(String name) {
      this(name, new Date());
   }

   public CoveragePackage(String name, Date runDate) {
      super(name);
      this.creationDate = runDate;
   }

   public CoveragePackage(Artifact artifact) {
      super(artifact);
   }

   public void addCoverageImport(CoverageImport CoverageImport) {
      coverageImports.add(CoverageImport);
   }

   public List<CoverageImport> getCoverageImports() {
      return coverageImports;
   }

   public Date getRunDate() {
      return creationDate;
   }

   @Override
   public OseeImage getTitleImage() {
      return CoverageImage.COVERAGE_PACKAGE;
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

}
