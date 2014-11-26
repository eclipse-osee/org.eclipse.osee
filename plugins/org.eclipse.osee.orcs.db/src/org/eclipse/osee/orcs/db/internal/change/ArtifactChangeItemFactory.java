/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.change;

import java.util.HashMap;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ArtifactChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.db.internal.change.ChangeItemLoader.ChangeItemFactory;

public final class ArtifactChangeItemFactory implements ChangeItemFactory {
   private static final String SELECT_ARTIFACTS_BY_GAMMAS =
      "select art_id, art_type_id, txj.gamma_id from osee_artifact id, osee_join_transaction txj where id.gamma_id = txj.gamma_id and txj.query_id = ?";

   private final HashMap<Long, ModificationType> changeByGammaId;

   public ArtifactChangeItemFactory(HashMap<Long, ModificationType> changeByGammaId) {
      super();
      this.changeByGammaId = changeByGammaId;
   }

   @Override
   public String getLoadByGammaQuery() {
      return SELECT_ARTIFACTS_BY_GAMMAS;
   }

   @Override
   public ChangeItem createItem(JdbcStatement chStmt) throws OseeCoreException {
      int artId = chStmt.getInt("art_id");
      long artTypeId = chStmt.getLong("art_type_id");

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