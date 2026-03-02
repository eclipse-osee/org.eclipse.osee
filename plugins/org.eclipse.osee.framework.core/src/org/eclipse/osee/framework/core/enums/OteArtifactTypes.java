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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.*;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.*;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.*;
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