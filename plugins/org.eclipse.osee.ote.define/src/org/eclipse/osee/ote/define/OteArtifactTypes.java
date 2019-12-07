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
package org.eclipse.osee.ote.define;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractTestResult;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Extension;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.TestScriptGuid;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId;
import static org.eclipse.osee.ote.define.OteAttributeTypes.BuildId;
import static org.eclipse.osee.ote.define.OteAttributeTypes.Checksum;
import static org.eclipse.osee.ote.define.OteAttributeTypes.ElapsedDate;
import static org.eclipse.osee.ote.define.OteAttributeTypes.EndDate;
import static org.eclipse.osee.ote.define.OteAttributeTypes.Failed;
import static org.eclipse.osee.ote.define.OteAttributeTypes.IsBatchModeAllowed;
import static org.eclipse.osee.ote.define.OteAttributeTypes.LastAuthor;
import static org.eclipse.osee.ote.define.OteAttributeTypes.LastDateUploaded;
import static org.eclipse.osee.ote.define.OteAttributeTypes.LastModifiedDate;
import static org.eclipse.osee.ote.define.OteAttributeTypes.ModifiedFlag;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OsArchitecture;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OsName;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OsVersion;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OseeServerJarVersion;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OseeServerTitle;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OseeVersion;
import static org.eclipse.osee.ote.define.OteAttributeTypes.OutfileUrl;
import static org.eclipse.osee.ote.define.OteAttributeTypes.Passed;
import static org.eclipse.osee.ote.define.OteAttributeTypes.ProcessorId;
import static org.eclipse.osee.ote.define.OteAttributeTypes.QualificationLevel;
import static org.eclipse.osee.ote.define.OteAttributeTypes.RanInBatchMode;
import static org.eclipse.osee.ote.define.OteAttributeTypes.Revision;
import static org.eclipse.osee.ote.define.OteAttributeTypes.ScriptAborted;
import static org.eclipse.osee.ote.define.OteAttributeTypes.StartDate;
import static org.eclipse.osee.ote.define.OteAttributeTypes.TestDisposition;
import static org.eclipse.osee.ote.define.OteAttributeTypes.TestScriptUrl;
import static org.eclipse.osee.ote.define.OteAttributeTypes.TotalTestPoints;
import static org.eclipse.osee.ote.define.OteTypeTokenProvider.ote;
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