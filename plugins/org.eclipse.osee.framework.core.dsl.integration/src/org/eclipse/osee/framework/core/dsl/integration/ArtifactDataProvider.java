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

package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactDataProvider {

   public static interface ArtifactProxy extends ArtifactToken {

      Collection<RelationTypeToken> getValidRelationTypes();

      Collection<ArtifactProxy> getHierarchy();

      BranchToken getBranchToken();
   }

   boolean isApplicable(Object object);

   ArtifactProxy asCastedObject(Object object);
}