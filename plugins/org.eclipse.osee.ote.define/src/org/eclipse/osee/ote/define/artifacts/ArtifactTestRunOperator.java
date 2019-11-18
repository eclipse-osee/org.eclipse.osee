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
import org.eclipse.osee.ote.define.AUTOGEN.OteArtifactTypes;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTestRunOperator implements TestRunOperator {
   private static final OteArtifactFetcher<Artifact> TEST_RUN_ARTIFACT_FETCHER =
      new OteArtifactFetcher<>(OteArtifactTypes.TestRun);

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
      if (!artifact.isOfType(OteArtifactTypes.TestRun)) {
         throw new OseeArgumentException("Unable to operate on type [%s]. Only [%s] allowed.",
            artifact.getArtifactTypeName(), OteArtifactTypes.TestRun);
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
      return artifact.getSoleAttributeValue(OteAttributeTypes.Revision, "");
   }

   public String getScriptUrl() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.TestScriptUrl, "");
   }

   public void setLastDateUploaded(Date value) {
      artifact.setSoleAttributeValue(OteAttributeTypes.LastDateUploaded, value);
   }

   @Override
   public Date getLastDateUploaded() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.LastDateUploaded, null);
   }

   public void setChecksum(String value) {
      artifact.setSoleAttributeValue(OteAttributeTypes.Checksum, value);
   }

   @Override
   public String getChecksum() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.Checksum, "");
   }

   public String getOutfileExtension() {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.Extension, "");
   }

   public void setOutfileExtension(String outfile) {
      artifact.setSoleAttributeValue(CoreAttributeTypes.Extension, outfile);
   }

   public boolean isFromLocalWorkspace() {
      return getLastDateUploaded() == null;
   }

   public void setLocalOutfileURI(String uri) {
      IAttributeDataProvider provider = getOutfileAttribute().getAttributeDataProvider();
      if (provider instanceof MappedAttributeDataProvider) {
         ((MappedAttributeDataProvider) provider).setLocalUri(uri);
      }
   }

   @Override
   public String getOutfileUrl() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OutfileUrl);
   }

   public String getOutfileContents() {
      try {
         return Lib.inputStreamToString(new URI(getOutfileUrl()).toURL().openStream());
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @SuppressWarnings("deprecation")
   public Attribute<InputStream> getOutfileAttribute() {
      List<Attribute<InputStream>> attributes = artifact.getAttributes(OteAttributeTypes.OutfileUrl);
      return attributes != null && attributes.size() > 0 ? attributes.get(0) : null;
   }

   public boolean isScriptRevisionValid() {
      boolean toReturn = false;
      try {
         new URI(getScriptUrl());
         String revision = getScriptRevision();
         if (Strings.isValid(revision)) {
            toReturn = true;
         }
      } catch (Exception ex) {
         // do nothing
      }
      return toReturn;
   }

   public boolean hasNotBeenCommitted() {
      Artifact fetched = null;
      try {
         fetched = getTestRunFetcher().searchForUniqueArtifactMatching(OteAttributeTypes.Checksum, getChecksum(),
            artifact.getBranch());
      } catch (Exception ex) {
         // do nothing
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
      return artifact.getSoleAttributeValue(OteAttributeTypes.Passed);
   }

   @Override
   public int getTestPointsFailed() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.Failed);
   }

   @Override
   public int getTotalTestPoints() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.TotalTestPoints);
   }

   @Override
   public Date getEndDate() {
      return processDateAttribute(OteAttributeTypes.EndDate);
   }

   @Override
   public Date getLastModifiedDate() {
      return processDateAttribute(OteAttributeTypes.LastModifiedDate);
   }

   @Override
   public Date getTestStartDate() {
      return processDateAttribute(OteAttributeTypes.StartDate);
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
         toReturn = artifact.getSoleAttributeValue(OteAttributeTypes.ScriptAborted, false);
      } catch (Exception ex) {
         // do nothing
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
         toReturn = artifact.getSoleAttributeValue(OteAttributeTypes.IsBatchModeAllowed, false);
      } catch (Exception ex) {
         // do nothing
      }
      return toReturn;
   }

   @Override
   public String getOseeVersion() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OseeVersion, "").trim();
   }

   @Override
   public String getOseeServerTitle() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OseeServerTitle, "").trim();
   }

   @Override
   public String getOseeServerVersion() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OseeServerJarVersion, "").trim();
   }

   @Override
   public String getProcessorId() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.ProcessorId, "");
   }

   @Override
   public String getRunDuration() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.ElapsedDate, "");
   }

   @Override
   public String getQualificationLevel() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.QualificationLevel, "");
   }

   @Override
   public String getBuildId() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.BuildId, "");
   }

   @Override
   public String getRanOnOperatingSystem() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.OsName, "");
   }

   @Override
   public String getLastAuthor() {
      return artifact.getSoleAttributeValue(OteAttributeTypes.LastAuthor, null);
   }

   @Override
   public String getScriptSimpleName() {
      String rawName = getDescriptiveName();
      String[] qualifiers = rawName.split("\\.");
      return qualifiers[qualifiers.length - 1];
   }
}