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

import java.sql.SQLException;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;

/**
 * @author Ryan D. Brooks
 */
public class AttributeMapRow {
   private static final ConfigurationPersistenceManager configurationPersistencManager =
         ConfigurationPersistenceManager.getInstance();
   private final Branch branch;
   private SkynetTypesImporter importer;
   private String artifactSuperTypeName;
   private String attributeName;
   private Logger logger = ConfigUtil.getConfigFactory().getLogger(AttributeMapRow.class);

   /**
    * 
    */
   public AttributeMapRow(SkynetTypesImporter importer, String[] row, Branch branch) {
      super();
      this.importer = importer;
      this.branch = branch;
      artifactSuperTypeName = row[0];
      attributeName = row[1];
   }

   public void persist() throws SQLException {
      DynamicAttributeDescriptor attributeType = configurationPersistencManager.getDynamicAttributeType(attributeName);

      for (String artifactTypeName : importer.determineConcreateTypes(artifactSuperTypeName)) {
         ArtifactSubtypeDescriptor artifactType =
               configurationPersistencManager.getArtifactSubtypeDescriptor(artifactTypeName);
         configurationPersistencManager.persistAttributeValidity(artifactType, attributeType);
      }
   }
}
