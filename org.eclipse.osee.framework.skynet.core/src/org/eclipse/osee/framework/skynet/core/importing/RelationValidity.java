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
import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Ryan D. Brooks
 */
public class RelationValidity {
   private final HashSet<ValidityConstraint> validitySet;
   private final ArrayList<ValidityRow> validityArray;
   private final SkynetTypesImporter importer;
   private final Branch branch;

   public RelationValidity(SkynetTypesImporter importer, Branch branch) {
      super();
      this.importer = importer;
      this.branch = branch;
      validitySet = new HashSet<ValidityConstraint>();
      validityArray = new ArrayList<ValidityRow>();
   }

   private class ValidityConstraint {
      private final ArtifactType artifactType;
      private final RelationType linkDescriptor;
      private final int sideAmax;
      private final int sideBmax;
      private final Branch branch;

      public ValidityConstraint(Branch branch, ArtifactType artifactType, RelationType linkDescriptor, int sideAmax, int sideBmax) {
         this.artifactType = artifactType;
         this.linkDescriptor = linkDescriptor;
         this.sideAmax = sideAmax;
         this.sideBmax = sideBmax;
         this.branch = branch;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj instanceof ValidityConstraint) {
            ValidityConstraint constraint = (ValidityConstraint) obj;
            return artifactType.equals(constraint.artifactType) && linkDescriptor.equals(constraint.linkDescriptor);
         }
         return false;
      }

      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode() {
         int result = 17;
         result = 37 * result + artifactType.hashCode();
         result = 37 * result + linkDescriptor.hashCode();
         return result;
      }
   }

   public void addValidityConstraints(String[] row) throws SQLException {
      validityArray.add(new ValidityRow(row[0], row[1], SkynetTypesImporter.getQuantity(row[2]),
            SkynetTypesImporter.getQuantity(row[3])));
   }

   public void persist() throws SQLException {
      for (ValidityRow row : validityArray) {
         for (String artifactTypeName : importer.determineConcreateTypes(row.artifactSuperTypeName)) {
            ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
            RelationType linkDescriptor = RelationTypeManager.getType(row.relationTypeName);

            RelationTypeManager.createRelationLinkValidity(branch, artifactType, linkDescriptor, row.sideAmax,
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
