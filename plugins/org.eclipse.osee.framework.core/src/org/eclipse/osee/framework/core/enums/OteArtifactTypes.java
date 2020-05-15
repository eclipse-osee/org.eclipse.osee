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
package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractTestResult;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Extension;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.TestScriptGuid;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId;
import static org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider.ote;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.BuildId;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.Checksum;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.ElapsedDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.EndDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.Failed;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.IsBatchModeAllowed;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.LastAuthor;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.LastDateUploaded;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.LastModifiedDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.ModifiedFlag;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OsArchitecture;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OsName;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OsVersion;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OseeServerJarVersion;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OseeServerTitle;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OseeVersion;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.OutfileUrl;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.Passed;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.ProcessorId;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.QualificationLevel;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.RanInBatchMode;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.Revision;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.ScriptAborted;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.StartDate;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.TestDisposition;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.TestScriptUrl;
import static org.eclipse.osee.framework.core.enums.OteAttributeTypes.TotalTestPoints;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

public interface OteArtifactTypes {

   // @formatter:off
   ArtifactTypeToken TestRun = ote.add(ote.artifactType(85L, "Test Run", false, AbstractTestResult)
      .zeroOrOne(BuildId, "unknown")
      .zeroOrOne(Checksum, "")
      .zeroOrOne(ElapsedDate, "")
      .zeroOrOne(EndDate, "")
      .zeroOrOne(Extension, "")
      .exactlyOne(Failed, "0")
      .exactlyOne(IsBatchModeAllowed, "true")
      .zeroOrOne(LastAuthor, "")
      .zeroOrOne(LastDateUploaded, "")
      .zeroOrOne(LastModifiedDate, "")
      .zeroOrOne(ModifiedFlag, "")
      .zeroOrOne(OsArchitecture, "")
      .zeroOrOne(OsName, "")
      .zeroOrOne(OsVersion, "")
      .zeroOrOne(OseeServerJarVersion, "")
      .zeroOrOne(OseeServerTitle, "")
      .zeroOrOne(OseeVersion, "")
      .exactlyOne(OutfileUrl, "\"\"")
      .exactlyOne(Passed, "0")
      .zeroOrOne(ProcessorId, "")
      .zeroOrOne(QualificationLevel, "DEVELOPMENT")
      .exactlyOne(RanInBatchMode, "false")
      .zeroOrOne(Revision, "")
      .exactlyOne(ScriptAborted, "true")
      .zeroOrOne(StartDate, "")
      .zeroOrOne(TestScriptGuid, "")
      .zeroOrOne(TestScriptUrl, "")
      .exactlyOne(TotalTestPoints, "0")
      .zeroOrOne(UserId, ""));
   ArtifactTypeToken TestRunDisposition = ote.add(ote.artifactType(84L, "Test Run Disposition", false, Artifact)
      .zeroOrOne(TestDisposition, ""));
   // @formatter:on
}