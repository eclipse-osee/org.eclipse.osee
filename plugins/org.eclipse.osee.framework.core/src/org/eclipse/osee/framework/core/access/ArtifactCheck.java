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

package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCheck implements IArtifactCheck {

   @Override
   public XResultData isDeleteable(Collection<ArtifactToken> artifacts, XResultData results) {
      return new XResultData();
   }

   @Override
   public XResultData isRenamable(Collection<ArtifactToken> artifacts, XResultData results) {
      return new XResultData();
   }

   @Override
   public XResultData isDeleteableRelation(ArtifactToken artifact, RelationTypeToken relationType, XResultData results) {
      return new XResultData();
   }
}