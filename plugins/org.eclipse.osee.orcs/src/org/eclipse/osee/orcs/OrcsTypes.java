/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs;

import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   Callable<Void> purgeArtifactsByArtifactType(Collection<? extends ArtifactTypeToken> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(Collection<? extends AttributeTypeId> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(Collection<? extends RelationTypeToken> relationTypes);

}