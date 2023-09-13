/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractTestResult;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Extension;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.FailedCount;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.LastAuthor;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.LastModifiedDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ModifiedFlag;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OsArchitecture;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OsName;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OsVersion;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OseeServerJarVersion;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OseeServerTitle;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.OseeVersion;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PassedCount;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ProcessorId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.QualificationLevel;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Revision;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ScriptAborted;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.TestScriptGuid;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.TotalTestPoints;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.ote;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.BuildId;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.Checksum;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ElapsedDate;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.EndDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.IsBatchModeAllowed;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.LastDateUploaded;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OutfileUrl;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.RanInBatchMode;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.StartDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.TestDisposition;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.TestScriptUrl;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

public interface OteArtifactTypes {

   // @formatter:off
   ArtifactTypeToken TestRun = ote.add(ote.artifactType(85L, "Test Run", false, AbstractTestResult)
      .zeroOrOne(BuildId, "unknown")
      .zeroOrOne(Checksum)
      .zeroOrOne(ElapsedDate)
      .zeroOrOne(EndDate)
      .zeroOrOne(Extension)
      .exactlyOne(FailedCount)
      .exactlyOne(IsBatchModeAllowed, Boolean.TRUE)
      .zeroOrOne(LastAuthor)
      .zeroOrOne(LastDateUploaded)
      .zeroOrOne(LastModifiedDate)
      .zeroOrOne(ModifiedFlag)
      .zeroOrOne(OsArchitecture)
      .zeroOrOne(OsName)
      .zeroOrOne(OsVersion)
      .zeroOrOne(OseeServerJarVersion)
      .zeroOrOne(OseeServerTitle)
      .zeroOrOne(OseeVersion)
      .exactlyOne(OutfileUrl, "\"\"")
      .exactlyOne(PassedCount)
      .zeroOrOne(ProcessorId)
      .zeroOrOne(QualificationLevel, "DEVELOPMENT")
      .exactlyOne(RanInBatchMode)
      .zeroOrOne(Revision)
      .exactlyOne(ScriptAborted, Boolean.TRUE)
      .zeroOrOne(StartDate)
      .zeroOrOne(TestScriptGuid)
      .zeroOrOne(TestScriptUrl)
      .exactlyOne(TotalTestPoints)
      .zeroOrOne(UserId));
   ArtifactTypeToken TestRunDisposition = ote.add(ote.artifactType(84L, "Test Run Disposition", false, Artifact)
      .zeroOrOne(TestDisposition));
   // @formatter:on
}