/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.checks;

import java.util.Collection;
import org.eclipse.osee.framework.core.access.ArtifactCheck;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author John Misinco
 */
public class AssociatedArtifactCheck extends ArtifactCheck {

   @Override
   public XResultData isDeleteable(Collection<ArtifactToken> artifacts, XResultData results) {
      return BranchManager.isDeleteable(artifacts, results);
   }
}
