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
package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Donald G. Dunne
 */
public interface IRelationResolver {

   Collection<ArtifactToken> getRelated(ArtifactId artifact, IRelationTypeSide relationType);

   <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, IRelationTypeSide relationType, Class<T> clazz);

   <T extends IAtsObject> Collection<T> getRelated(IAtsObject atsObject, IRelationTypeSide relationType, DeletionFlag flag, Class<T> clazz);

   boolean areRelated(ArtifactId artifact1, IRelationTypeSide relationType, ArtifactId artifact2);

   boolean areRelated(IAtsObject atsObject1, IRelationTypeSide relationType, IAtsObject atsObject2);

   ArtifactId getRelatedOrNull(ArtifactId artifact, IRelationTypeSide relationType);

   ArtifactId getRelatedOrNull(IAtsObject atsObject, IRelationTypeSide relationType);

   <T> T getRelatedOrNull(IAtsObject atsObject, IRelationTypeSide relationType, Class<T> clazz);

   int getRelatedCount(IAtsWorkItem workItem, IRelationTypeSide relationType);

   List<ArtifactId> getRelatedArtifacts(IAtsWorkItem workItem, IRelationTypeSide relationTypeSide);

   Collection<ArtifactToken> getRelated(IAtsObject atsObject, IRelationTypeSide relationTypeSide);

}
