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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyTripleHashMap;

/**
 * @author Roberto E. Escobar
 */
public class RelationLinkUniqueness extends DatabaseHealthOperation {

   private static final String SQL = "select *  from osee_relation_link t1, osee_relation_link t2" + //
   "where t1.a_art_id = t2.a_art_id" + //
   "and t1.b_art_id = t2.b_art_id" + //
   "and t1.rel_link_type_id = t2.rel_link_type_id" + //
   "and NOT t1.rel_link_id = t2.rel_link_id order by t1.rel_link_id, t2.rel_link_id";

   private class Link {
      private final int relationLinkId;
      private final ModificationType modificationType;
      private final int relationLinkType;
      private final int aArtifactId;
      private final int bArtifactId;
      private final int aOrder;
      private final int bOrder;
      private final String rationale;
      private final int gammaId;

      public Link(int relationLinkId, ModificationType modificationType, int relationLinkType, int aArtifactId, int bArtifactId, int aOrder, int bOrder, String rationale, int gammaId) {
         super();
         this.relationLinkId = relationLinkId;
         this.modificationType = modificationType;
         this.relationLinkType = relationLinkType;
         this.aArtifactId = aArtifactId;
         this.bArtifactId = bArtifactId;
         this.aOrder = aOrder;
         this.bOrder = bOrder;
         this.rationale = rationale;
         this.gammaId = gammaId;
      }
   }

   /**
    * @param operationName
    */
   public RelationLinkUniqueness() {
      super("Relation Link Uniqueness");
      // TODO Auto-generated constructor stub
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {

      CompositeKeyTripleHashMap<Integer, Integer, Integer, Link> items = getRelationLinkErrors();

      setItemsToFix(items.size());
      if (isFixOperationEnabled()) {

      }
      getSummary().append(String.format("Found [%s] non-unique relation links\n", getItemsToFixCount()));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getCheckDescription()
    */
   @Override
   public final String getCheckDescription() {
      return "Checks that only one relation link of a particular type exists between two artifacts";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getFixDescription()
    */
   @Override
   public final String getFixDescription() {
      return "None Yet";
   }

   private final CompositeKeyTripleHashMap<Integer, Integer, Integer, Link> getRelationLinkErrors() throws OseeDataStoreException {
      CompositeKeyTripleHashMap<Integer, Integer, Integer, Link> items =
            new CompositeKeyTripleHashMap<Integer, Integer, Integer, Link>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      while (chStmt.next()) {
         //         new Link();
         //         items.put(key, value);
      }

      return items;
   }
}
