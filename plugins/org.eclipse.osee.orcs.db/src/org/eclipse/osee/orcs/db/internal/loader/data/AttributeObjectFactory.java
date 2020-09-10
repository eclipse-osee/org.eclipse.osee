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

package org.eclipse.osee.orcs.db.internal.loader.data;

import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeObjectFactory extends VersionObjectFactory {

   <T> AttributeData<T> createAttributeData(VersionData version, Integer id, AttributeTypeGeneric<?> attributeType, ModificationType modType, ArtifactId artId, T value, String uri, ApplicabilityId applicId);

   <T> AttributeData<T> createAttributeData(VersionData version, Integer id, AttributeTypeGeneric<?> attributeType, ModificationType modType, ArtifactId artId, ApplicabilityId applicId);

   <T> AttributeData<T> createCopy(AttributeData<T> source);
}