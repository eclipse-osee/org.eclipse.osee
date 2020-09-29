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
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AccessControlArtifactUtil {

   public static XResultData getXResultAccessHeader(String title, Artifact artifact) {
      return XResultDataHeaders.getXResultAccessHeader(title, artifact, null);
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType) {
      return XResultDataHeaders.getXResultAccessHeader(title, artifacts, attributeType, null);
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts, RelationTypeSide relTypeSide) {
      return XResultDataHeaders.getXResultAccessHeader(title, artifacts, relTypeSide, null);
   }

   public static XResultData getXResultAccessHeader(String title, BranchToken branch) {
      return XResultDataHeaders.getXResultAccessHeader(title, branch, null);
   }

   public static XResultData getXResultAccessHeader(String title, Collection<? extends ArtifactToken> artifacts) {
      return XResultDataHeaders.getXResultAccessHeader(title, artifacts, null);
   }

}
