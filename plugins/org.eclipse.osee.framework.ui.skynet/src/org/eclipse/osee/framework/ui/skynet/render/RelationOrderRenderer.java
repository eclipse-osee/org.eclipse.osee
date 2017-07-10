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
package org.eclipse.osee.framework.ui.skynet.render;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderRenderer {
   private static final String NO_DATA_TAG = "None";
   private final ArtifactGuidToWordML guidResolver;
   private final AbstractOseeCache<RelationType> relationCache;

   public RelationOrderRenderer(AbstractOseeCache<RelationType> relationCache, ArtifactGuidToWordML guidResolver) {
      this.relationCache = relationCache;
      this.guidResolver = guidResolver;
   }

   private void writeTableRow(WordMLProducer writer, RelationType relationType, RelationSide side, String sorterName, List<String> orderedData) throws OseeCoreException {
      writer.startTableRow();
      String relationName = relationType.getName();
      String relationSideName = relationType.getSideName(side);
      String sideName = side.name().toLowerCase();
      if (orderedData.isEmpty()) {
         writer.addTableColumns(relationName, relationSideName, sideName, sorterName, NO_DATA_TAG);
      } else {
         writer.addTableColumns(relationName, relationSideName, sideName, sorterName);
         writer.startTableColumn();
         for (String link : orderedData) {
            writer.addParagraphNoEscape(link);
         }
         writer.endTableColumn();
      }
      writer.endTableRow();

   }

   public void toWordML(WordMLProducer producer, BranchId branch, RelationOrderData relationOrderData) {
      WordMLProducer writer = producer;
      try {
         writer.startTable();
         writer.addWordMl("<w:tblPr><w:tblW w:w=\"8200\" w:type=\"dxa\"/><w:jc w:val=\"center\"/></w:tblPr>");
         if (!relationOrderData.hasEntries()) {
            writer.addTableRow(NO_DATA_TAG);
         } else {
            writer.addTableRow("Relation Type", "Side Name", "Side", "Order Type", "Related Artifacts");
            for (Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> entry : relationOrderData.getOrderedEntrySet()) {
               IRelationType relationTypeId = entry.getKey().getFirst();
               RelationSide relationSide = entry.getKey().getSecond();
               RelationSorter sorterGuid = entry.getValue().getFirst();

               List<String> guidList = entry.getValue().getSecond();
               List<String> mlLinks = guidResolver.resolveAsOseeLinks(branch, guidList);
               RelationType relationType = relationCache.get(relationTypeId);
               try {
                  writeTableRow(writer, relationType, relationSide, sorterGuid.toString(), mlLinks);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      } finally {
         try {
            writer.endTable();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}
