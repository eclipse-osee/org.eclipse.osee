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
package org.eclipse.osee.framework.core.access;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationType;
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
   public XResultData isDeleteableRelation(ArtifactToken artifact, IRelationType relationType, XResultData results) {
      return new XResultData();
   }
}