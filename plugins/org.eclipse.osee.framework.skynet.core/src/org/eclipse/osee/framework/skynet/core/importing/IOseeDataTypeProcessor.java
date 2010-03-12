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
package org.eclipse.osee.framework.skynet.core.importing;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeDataTypeProcessor {

   public void onArtifactTypeInheritance(String ancestor, Collection<String> descendants) throws OseeCoreException;

   public void onAttributeType(String attributeBaseType, String attributeProviderTypeName, String fileTypeExtension, String attributeName, String defaultValue, String validityXml, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException;

   public void onArtifactType(boolean isAbstract, String artifactTypeName) throws OseeCoreException;

   public void onRelationType(String relationTypeName, String sideAName, String sideBName, String artifactTypeSideA, String artifactTypeSideB, String multiplicity, String ordered, String defaultOrderTypeGuid) throws OseeCoreException;

   public void onAttributeValidity(String attributeName, String artifactSuperTypeName, Collection<String> concreteTypes) throws OseeCoreException;

   public void onRelationValidity(String artifactTypeName, String relationTypeName, int sideAMax, int sideBMax) throws OseeCoreException;

   public boolean doesArtifactSuperTypeExist(String artifactSuperTypeName) throws OseeCoreException;

   public void onFinish() throws OseeCoreException;
}
