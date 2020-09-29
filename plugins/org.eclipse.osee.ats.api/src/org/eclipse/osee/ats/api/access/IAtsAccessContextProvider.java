/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.access;

import java.util.Collection;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * Provides extension to allow ATS and ATS Configurations to provide Framework requests for IAtsProvider through
 * AtsAccessProvider.
 *
 * @author Donald G. Dunne
 */
public interface IAtsAccessContextProvider {

   boolean isApplicable(AtsUser atsUser, Object object);

   default public XResultData hasArtifactContextWriteAccess(AtsUser atsUser, Collection<? extends ArtifactToken> artifacts, XResultData rd) {
      return rd;
   }

   default public XResultData hasAttributeTypeContextWriteAccess(AtsUser atsUser, Collection<? extends ArtifactToken> artifacts, AttributeTypeToken attributeType, XResultData rd) {
      return rd;
   }

   default public XResultData hasRelationContextWriteAccess(AtsUser atsUser, ArtifactToken artifact, RelationTypeToken relationType, XResultData rd) {
      return rd;
   }

}
