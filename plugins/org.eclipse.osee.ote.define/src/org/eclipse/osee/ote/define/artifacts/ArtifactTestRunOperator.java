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

package org.eclipse.osee.ote.define.artifacts;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.attribute.providers.MappedAttributeDataProvider;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTestRunOperator implements TestRunOperator {
   private static final OteArtifactFetcher<Artifact> TEST_RUN_ARTIFACT_FETCHER =
      new OteArtifactFetcher<>(CoreArtifactTypes.TestRun);

   private static final OteArtifactFetcher<Artifact> TEST_SCRIPT_ARTIFACT_FETCHER =
      new OteArtifactFetcher<>(CoreArtifactTypes.TestCase);

   private final Artifact artifact;

   public ArtifactTestRunOperator(Artifact artifact) {
      checkForNull(artifact);
      checkForType(artifact);
      this.artifact = artifact;
   }

   private void checkForNull(Artifact artifact) {
      if (artifact == null) {
         throw new OseeArgumentException("Artifact was null.");
      }
   }

   private void checkForType(Artifact artifact) {
      if (!artifact.isOfType(CoreArtifactTypes.TestRun)) {
         throw new OseeArgumentException("Unable to operate on type [%s]. Only [%s] allowed.",
            artifact.getArtifactTypeName(), CoreArtifactTypes.TestRun);
      }
   }

   public static ArtifactTestRunOperator getNewArtifactWithOperator(BranchId branch) {
      return new ArtifactTestRunOperator(TEST_RUN_ARTIFACT_FETCHER.getNewArtifact(branch));
   }

   public static OteArtifactFetcher<Artifact> getTestRunFetcher() {
      return TEST_RUN_ARTIFACT_FETCHER;
   }

   public static OteArtifactFetcher<Artifact> getTestScriptFetcher() {
      return TEST_SCRIPT_ARTIFACT_FETCHER;
   }

   public Artifact getTestRunArtifact() {
      return artifact;
   }

   public String getDescriptiveName() {
      return artifact.getName();
   }

   @Override
   public String getScriptRevision() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.REVISION, "");
   }

   public String getScriptUrl() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.TEST_SCRIPT_URL, "");
   }

   public void setLastDateUploaded(Date value) {
      artifact.setSoleAttributeValue(OteAttributeTypes.LAST_DATE_UPLOADED, value);
   }

   @Override
   public Date getLastDateUploaded() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.LAST_DATE_UPLOADED, null);
   }

   public void setChecksum(String value) {
      artifact.setSoleAttributeValue(OteAttributeTypes.CHECKSUM, value);
   }

   @Override
   public String getChecksum() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.CHECKSUM, "");
   }

   public String getOutfileExtension() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.EXTENSION, "");
   }

   public void setOutfileExtension(String outfile) {
      artifact.setSoleAttributeValue(OteAttributeTypes.EXTENSION, outfile);
   }

   public boolean isFromLocalWorkspace()  {
      return getLastDateUploaded() == null;
   }

   public void setLocalOutfileURI(String uri)  {
      IAttributeDataProvider provider = getOutfileAttribute().getAttributeDataProvider();
      if (provider instanceof MappedAttributeDataProvider) {
         ((MappedAttributeDataProvider) provider).setLocalUri(uri);
      }
   }

   @Override
   public String getOutfileUrl() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OUTFILE_URL);
   }

   public String getOutfileContents() {
      try {
         return Lib.inputStreamToString(new URI(getOutfileUrl()).toURL().openStream());
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   public Attribute<InputStream> getOutfileAttribute()  {
      List<Attribute<InputStream>> attributes = artifact.getAttributes(OteAttributeTypes.OUTFILE_URL);
      return attributes != null && attributes.size() > 0 ? attributes.get(0) : null;
   }

   public boolean isScriptRevisionValid() {
      boolean toReturn = false;
      try {
         URI url = new URI(getScriptUrl());

         String revision = getScriptRevision();
         if (Strings.isValid(revision)) {
            toReturn = true;
         }

      } catch (Exception ex) {
      }
      return toReturn;
   }

   public boolean hasNotBeenCommitted() {
      Artifact fetched = null;
      try {
         fetched = getTestRunFetcher().searchForUniqueArtifactMatching(OteAttributeTypes.CHECKSUM, getChecksum(),
            artifact.getBranch());
      } catch (Exception ex) {
      }
      return fetched == null;
   }

   public boolean isCommitAllowed() {
      return isScriptRevisionValid() && hasNotBeenCommitted();
   }

   public boolean hasValidArtifact() {
      return artifact != null && artifact.isDeleted() != true;
   }

   public void createTestScriptSoftLink() {
      Artifact testScript = getTestScriptFetcher().searchForUniqueArtifactMatching(CoreAttributeTypes.Name,
         artifact.getName(), artifact.getBranch());
      if (testScript != null) {
         artifact.setSoleAttributeValue(CoreAttributeTypes.TestScriptGuid, testScript.getGuid());
      }
   }

   @Override
   public String getPartition() {
      String name = artifact.getName();
      String[] data = name.split("\\.");
      if (data.length - 3 > 0) {
         name = data[data.length - 3];
      }
      return name;
   }

   @Override
   public String getSubsystem() {
      String name = artifact.getName();
      String[] data = name.split("\\.");
      if (data.length - 2 > 0) {
         name = data[data.length - 2];
      }
      return name;
   }

   @Override
   public int getTestPointsPassed() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.PASSED);
   }

   @Override
   public int getTestPointsFailed() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.FAILED);
   }

   @Override
   public int getTotalTestPoints() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.TOTAL_TEST_POINTS);
   }

   @Override
   public Date getEndDate() {
      return processDateAttribute(OteAttributeTypes.END_DATE);
   }

   @Override
   public Date getLastModifiedDate() {
      return processDateAttribute(OteAttributeTypes.LAST_MODIFIED_DATE);
   }

   @Override
   public Date getTestStartDate() {
      return processDateAttribute(OteAttributeTypes.START_DATE);
   }

   private Date processDateAttribute(AttributeTypeId attributeType) {
      Date date = artifact.getSoleAttributeValue(attributeType, null);
      if (date == null) {
         date = new Date(0);
      }
      return date;
   }

   @Override
   public boolean wasAborted() {
      boolean toReturn = true;
      try {
         toReturn = artifact.getSoleAttributeValue(OteAttributeTypes.SCRIPT_ABORTED, false);
      } catch (Exception ex) {
      }
      return toReturn;
   }

   @Override
   public String getTestResultStatus() {
      String result = "FAILED";
      if (wasAborted() != true) {
         int total = getTotalTestPoints();
         if (total > 0) {
            if (getTestPointsFailed() <= 0) {
               int passed = getTestPointsPassed();
               if (passed == total) {
                  result = "PASSED";
               }
            }
         }
      }
      return result;
   }

   @Override
   public boolean isBatchModeAllowed() {
      boolean toReturn = false;
      try {
         toReturn = artifact.getSoleAttributeValue(OteAttributeTypes.IS_BATCH_MODE_ALLOWED, false);
      } catch (Exception ex) {
      }
      return toReturn;
   }

   @Override
   public String getOseeVersion() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OSEE_VERSION, "").trim();
   }

   @Override
   public String getOseeServerTitle() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OSEE_SERVER_TITLE, "").trim();
   }

   @Override
   public String getOseeServerVersion() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OSEE_SERVER_JAR_VERSION, "").trim();
   }

   @Override
   public String getProcessorId() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.PROCESSOR_ID, "");
   }

   @Override
   public String getRunDuration() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.ELAPSED_DATE, "");
   }

   @Override
   public String getQualificationLevel() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.QUALIFICATION_LEVEL, "");
   }

   @Override
   public String getBuildId() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.BUILD_ID, "");
   }

   @Override
   public String getRanOnOperatingSystem() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OS_NAME, "");
   }

   @Override
   public String getLastAuthor() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.LAST_AUTHOR, null);
   }

   @Override
   public String getScriptSimpleName() {
      String rawName = getDescriptiveName();
      String[] qualifiers = rawName.split("\\.");
      return qualifiers[qualifiers.length - 1];
   }
}