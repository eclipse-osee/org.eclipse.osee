/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * @author Roberto E. Escobar
 */
public final class CriteriaCommitIds extends CriteriaMainTableField implements TxCriteria {
   public CriteriaCommitIds(Collection<ArtifactId> artifactIds) {
      super(artifactIds);
   }

   public CriteriaCommitIds(ArtifactId id) {
      super(id);
   }
}