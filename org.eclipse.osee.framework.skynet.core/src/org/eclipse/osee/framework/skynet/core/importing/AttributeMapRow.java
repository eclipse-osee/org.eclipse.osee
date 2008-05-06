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

import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class AttributeMapRow {
   private SkynetTypesImporter importer;
   private String artifactSuperTypeName;
   private String attributeName;

   public AttributeMapRow(SkynetTypesImporter importer, String[] row) {
      super();
      this.importer = importer;
      artifactSuperTypeName = row[0];
      attributeName = row[1];
   }

   public void persist() throws Exception {
      ConfigurationPersistenceManager configurationManager = ConfigurationPersistenceManager.getInstance();
      DynamicAttributeDescriptor attributeType = configurationManager.getDynamicAttributeType(attributeName);

      for (String artifactTypeName : importer.determineConcreateTypes(artifactSuperTypeName)) {
         ArtifactSubtypeDescriptor artifactType = configurationManager.getArtifactSubtypeDescriptor(artifactTypeName);
         configurationManager.persistAttributeValidity(artifactType, attributeType);
      }
   }
}
