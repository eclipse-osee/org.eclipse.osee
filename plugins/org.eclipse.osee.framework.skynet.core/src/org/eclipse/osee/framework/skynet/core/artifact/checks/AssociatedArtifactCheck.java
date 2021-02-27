/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.skynet.core.artifact.checks;

import java.util.Collection;
import org.eclipse.osee.framework.core.access.ArtifactCheck;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author John Misinco
 */
public class AssociatedArtifactCheck implements ArtifactCheck {

   @Override
   public XResultData isDeleteable(Collection<? extends ArtifactToken> artifacts, XResultData results) {
      return BranchManager.isDeleteable(Collections.castAll(artifacts), results);
   }
}
