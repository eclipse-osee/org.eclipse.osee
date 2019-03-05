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
package org.eclipse.osee.orcs.data;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactTypes extends IdCollection<ArtifactTypeToken> {

   Collection<? extends ArtifactTypeToken> getAllDescendantTypes(ArtifactTypeId artType);

   boolean isValidAttributeType(ArtifactTypeToken artType, BranchId branch, AttributeTypeId attributeType);

   Collection<AttributeTypeToken> getAttributeTypes(ArtifactTypeToken artType, BranchId branch);

   boolean inheritsFrom(ArtifactTypeToken artType, ArtifactTypeId... otherTypes);
}