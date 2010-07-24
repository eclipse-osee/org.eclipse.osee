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
package org.eclipse.osee.framework.branch.management.change;

import java.util.HashMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.message.ArtifactChangeItem;
import org.eclipse.osee.framework.core.message.AttributeChangeItem;
import org.eclipse.osee.framework.core.message.ChangeItem;
import org.eclipse.osee.framework.core.message.RelationChangeItem;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility.IdJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class ChangeItemLoader {

   private final IOseeDatabaseServiceProvider oseeDatabaseProvider;
   private final HashMap<Long, ModificationType> changeByGammaId;

   public static interface ChangeItemFactory {

      String getItemTableName();

      String getItemIdColumnName();

      String getItemValueColumnName();

      String getLoadByGammaQuery();

      ChangeItem createItem(IOseeStatement statement) throws OseeDataStoreException;
   }

   public ChangeItemLoader(IOseeDatabaseServiceProvider oseeDatabaseProvider, HashMap<Long, ModificationType> changeByGammaId) {
      this.oseeDatabaseProvider = oseeDatabaseProvider;
      this.changeByGammaId = changeByGammaId;
   }

   public ChangeItemFactory createArtifactChangeItemFactory() {
      return new ArtifactChangeItemFactory();
   }

   public ChangeItemFactory createAttributeChangeItemFactory() {
      return new AttributeChangeItemFactory();
   }

   public ChangeItemFactory createRelationChangeItemFactory() {
      return new RelationChangeItemFactory();
   }

   public void loadItemIdsBasedOnGammas(IProgressMonitor monitor, ChangeItemFactory factory, int queryId, HashMap<Integer, ChangeItem> changesByItemId, IdJoinQuery idJoin) throws OseeDataStoreException {
      IOseeStatement chStmt = oseeDatabaseProvider.getOseeDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(10000, factory.getLoadByGammaQuery(), queryId);
         while (chStmt.next()) {
            ChangeItem item = factory.createItem(chStmt);
            Integer itemId = item.getItemId();
            changesByItemId.put(itemId, item);
            idJoin.add(itemId);
         }
      } finally {
         chStmt.close();
      }
   }

   private final class ArtifactChangeItemFactory implements ChangeItemFactory {
      private static final String SELECT_ARTIFACTS_BY_GAMMAS =
         "select art_id, art_type_id, txj.gamma_id from osee_artifact id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      @Override
      public String getLoadByGammaQuery() {
         return SELECT_ARTIFACTS_BY_GAMMAS;
      }

      @Override
      public ChangeItem createItem(IOseeStatement chStmt) throws OseeDataStoreException {
         int artId = chStmt.getInt("art_id");
         int artTypeId = chStmt.getInt("art_type_id");

         long gammaId = chStmt.getLong("gamma_id");
         ModificationType modType = changeByGammaId.get(gammaId);

         return new ArtifactChangeItem(artId, artTypeId, gammaId, modType);
      }

      @Override
      public String getItemIdColumnName() {
         return "art_id";
      }

      @Override
      public String getItemTableName() {
         return "osee_artifact";
      }

      @Override
      public String getItemValueColumnName() {
         return null;
      }
   }

   private final class AttributeChangeItemFactory implements ChangeItemFactory {
      private static final String SELECT_ATTRIBUTES_BY_GAMMAS =
         "select art_id, attr_id, value, attr_type_id, txj.gamma_id from osee_attribute id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      @Override
      public String getLoadByGammaQuery() {
         return SELECT_ATTRIBUTES_BY_GAMMAS;
      }

      @Override
      public ChangeItem createItem(IOseeStatement chStmt) throws OseeDataStoreException {
         int attrId = chStmt.getInt("attr_id");
         int attrTypeId = chStmt.getInt("attr_type_id");
         int artId = chStmt.getInt("art_id");

         long gammaId = chStmt.getLong("gamma_id");
         ModificationType modType = changeByGammaId.get(gammaId);

         String value = chStmt.getString("value");

         return new AttributeChangeItem(attrId, attrTypeId, artId, gammaId, modType, value);
      }

      @Override
      public String getItemIdColumnName() {
         return "attr_id";
      }

      @Override
      public String getItemTableName() {
         return "osee_attribute";
      }

      @Override
      public String getItemValueColumnName() {
         return "value";
      }
   }

   private final class RelationChangeItemFactory implements ChangeItemFactory {
      private static final String SELECT_RELATIONS_BY_GAMMAS =
         "select a_art_id, b_art_id, rel_link_id, rel_link_type_id, rationale, txj.gamma_id from osee_relation_link id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

      @Override
      public String getLoadByGammaQuery() {
         return SELECT_RELATIONS_BY_GAMMAS;
      }

      @Override
      public ChangeItem createItem(IOseeStatement chStmt) throws OseeDataStoreException {
         int relLinkId = chStmt.getInt("rel_link_id");
         int relTypeId = chStmt.getInt("rel_link_type_id");

         long gammaId = chStmt.getLong("gamma_id");
         ModificationType modType = changeByGammaId.get(gammaId);

         int aArtId = chStmt.getInt("a_art_id");
         int bArtId = chStmt.getInt("b_art_id");
         String rationale = chStmt.getString("rationale");

         return new RelationChangeItem(relLinkId, relTypeId, gammaId, modType, aArtId, bArtId, rationale);
      }

      @Override
      public String getItemIdColumnName() {
         return "rel_link_id";
      }

      @Override
      public String getItemTableName() {
         return "osee_relation_link";
      }

      @Override
      public String getItemValueColumnName() {
         return "rationale";
      }
   }

}
