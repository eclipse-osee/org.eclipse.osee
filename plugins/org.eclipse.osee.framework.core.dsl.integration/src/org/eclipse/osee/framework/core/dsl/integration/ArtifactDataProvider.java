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
package org.eclipse.osee.framework.core.dsl.integration;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ArtifactDataProvider {

   public static interface ArtifactProxy extends ArtifactToken {

      @Override
      String getName();

      @Override
      String getGuid();

      @Override
      ArtifactType getArtifactType();

      boolean isAttributeTypeValid(IAttributeType attributeType) throws OseeCoreException;

      Collection<RelationType> getValidRelationTypes() throws OseeCoreException;

      Collection<ArtifactProxy> getHierarchy();

      IBasicArtifact<?> getObject();

      IOseeBranch getBranchToken();
   }

   boolean isApplicable(Object object);

   ArtifactProxy asCastedObject(Object object) throws OseeCoreException;
}