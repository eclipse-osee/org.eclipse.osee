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

import java.util.ArrayList;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class RelationValidity {
   private final ArrayList<ValidityRow> validityArray;
   private final SkynetTypesImporter importer;
   private final Branch branch;

   public RelationValidity(SkynetTypesImporter importer, Branch branch) {
      super();
      this.importer = importer;
      this.branch = branch;
      validityArray = new ArrayList<ValidityRow>();
   }

   public void addValidityConstraints(String[] row) {
      validityArray.add(new ValidityRow(row[0], row[1], SkynetTypesImporter.getQuantity(row[2]),
            SkynetTypesImporter.getQuantity(row[3])));
   }

   public void persist() throws OseeTypeDoesNotExist, OseeDataStoreException, OseeArgumentException {
      for (ValidityRow row : validityArray) {
         for (String artifactTypeName : importer.determineConcreteTypes(row.artifactSuperTypeName)) {
            ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
            RelationType relationType = RelationTypeManager.getType(row.relationTypeName);

            RelationTypeManager.createRelationLinkValidity(branch, artifactType, relationType, row.sideAmax,
                  row.sideBmax);
         }
      }
   }

   private class ValidityRow {
      public String artifactSuperTypeName;
      public String relationTypeName;
      public int sideAmax;
      public int sideBmax;

      public ValidityRow(String artifactSuperTypeName, String relationTypeName, int sideAmax, int sideBmax) {
         this.artifactSuperTypeName = artifactSuperTypeName;
         this.relationTypeName = relationTypeName;
         this.sideAmax = sideAmax;
         this.sideBmax = sideBmax;
      }
   }
}
