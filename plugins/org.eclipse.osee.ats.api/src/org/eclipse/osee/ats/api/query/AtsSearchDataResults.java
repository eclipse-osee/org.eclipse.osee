/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.ats.api.query;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

public class AtsSearchDataResults {

   private final Collection<ArtifactToken> artifacts;
   private final XResultData rd;

   public AtsSearchDataResults(Collection<ArtifactToken> artifacts, XResultData rd) {
      this.artifacts = artifacts;
      this.rd = rd;
   }

   public Collection<ArtifactToken> getArtifacts() {
      return artifacts;
   }

   public XResultData getRd() {
      return rd;
   }

}
