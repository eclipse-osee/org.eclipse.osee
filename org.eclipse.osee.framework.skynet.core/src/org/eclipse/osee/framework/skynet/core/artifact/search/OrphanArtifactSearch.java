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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.sql.SkynetDatabase;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Jeff C. Phillips
 */
public class OrphanArtifactSearch implements ISearchPrimitive {
   private static final String LABEL = "Orphan Search: ";
   private static final String tables = "osee_artifact";
   private static final String sql =
         "osee_artifact.art_type_id =? AND art_id NOT in (SELECT t2.art_id FROM osee_relation_link t1, osee_artifact t2, " + SkynetDatabase.TRANSACTIONS_TABLE + " t4, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t5, (SELECT Max(t1.gamma_id) AS gamma_id, t1.rel_link_id, t3.branch_id FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 WHERE t1.gamma_id = t2.gamma_id AND t2.transaction_id = t3.transaction_id AND t3.branch_id = ? GROUP BY t1.rel_link_id, t3.branch_id) t6 WHERE t1.rel_link_type_id =? AND t1.b_art_id = t2.art_id AND t1.gamma_id = t4.gamma_id AND t4.transaction_id = t5.transaction_id AND t1.rel_link_id = t6.rel_link_id AND t5.branch_id = t6.branch_id AND t1.gamma_id = t6.gamma_id AND t4.mod_type <> " + ModificationType.DELETED.getValue() + " GROUP BY t2.art_id)";
   private final ArtifactType aritfactType;
   private final int relationTypeId;

   public OrphanArtifactSearch(ArtifactType aritfactType) throws OseeCoreException {
      this.aritfactType = aritfactType;
      this.relationTypeId = RelationTypeManager.getType("Default Hierarchical").getId();
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      dataList.add(aritfactType.getId());
      dataList.add(branch.getBranchId());
      dataList.add(relationTypeId);

      return sql;
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return LABEL + aritfactType.getName();
   }

   public static OrphanArtifactSearch getPrimitive(String storageString) {
      storageString = storageString.replace(LABEL, "");
      if (storageString.endsWith(";")) {
         storageString = storageString.substring(0, storageString.length() - 1);
      }
      OrphanArtifactSearch search = null;
      try {
         ArtifactType artifactType = ArtifactTypeManager.getType(storageString);
         search = new OrphanArtifactSearch(artifactType);
      } catch (OseeCoreException ex) {
         throw new IllegalStateException("Value for " + OrphanArtifactSearch.class.getSimpleName() + " not parsable");
      }
      return search;
   }

   public String getStorageString() {
      return LABEL + aritfactType.getName() + ";";
   }
}
