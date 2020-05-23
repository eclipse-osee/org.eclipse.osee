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

package org.eclipse.osee.framework.skynet.core.importing;

import java.util.Collection;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeDataTypeProcessor {

   public void onArtifactTypeInheritance(String ancestor, Collection<String> descendants);

   public void onAttributeType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String attributeName, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId);

   public void onArtifactType(boolean isAbstract, String artifactTypeName);

   public void onRelationType(String relationTypeName, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String defaultOrderTypeGuid);

   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteTypes);

   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax);

   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName);

   public void onFinish();
}
