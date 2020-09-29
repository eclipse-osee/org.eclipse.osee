/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class XResultDataHeaders {

   public static XResultData getXResultAccessHeader(String title, Artifact artifact, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      String release = getReleaseStr();
      rd.logf("%s\n\nArtifact: %s\nArifact Type %s\nBranch: %s\nUser: %s\nRelease: %s\n\n", title,
         artifact.toStringWithId(), artifact.getArtifactType().toStringWithId(), artifact.getBranch().toStringWithId(),
         UserManager.getUser().toStringWithId(), release);
      return rd;
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      String release = getReleaseStr();
      rd.logf("%s\n\nArtifact(s):\nAttrType: %s\nUser: %s\nRelease: %s\nBranch: %s\n\n", title,
         Artifacts.toStringWithIds(artifacts), attributeType, UserManager.getUser().toStringWithId(), release,
         artifacts.iterator().next().getBranch().toStringWithId());
      return rd;
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts, RelationTypeSide relTypeSide, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      String release = getReleaseStr();
      rd.logf("%s\n\nArtifact(s):\nRelType: %s\nUser: %s\nRelease: %s\nBranch: %s\n\n", title,
         Artifacts.toStringWithIds(artifacts), relTypeSide, UserManager.getUser().toStringWithId(), release,
         artifacts.iterator().next().getBranch().toStringWithId());
      return rd;
   }

   public static XResultData getXResultAccessHeader(String title, BranchToken branch, XResultData rd) {
      if (rd == null) {
         rd = new XResultData();
      }
      String release = getReleaseStr();
      rd.logf("%s\n\nBranch: %s\nUser: %s\nRelease: %s\n\n", title, branch.toStringWithId(),
         UserManager.getUser().toStringWithId(), release);
      return rd;
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      return null;
   }

   private static String getReleaseStr() {
      String verStr = OseeCodeVersion.getVersion();
      String bundleVerStr = OseeCodeVersion.getBundleVersion();
      String release = verStr;
      if (!verStr.equals(bundleVerStr)) {
         release = String.format("%s / %s", verStr, bundleVerStr);
      }
      return release;
   }

}
