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
import org.eclipse.osee.framework.core.cache.AbstractOseeCache;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationSorterProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.word.Producer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderRenderer {
   private static final String NO_DATA_TAG = "None";
   private final ArtifactGuidToWordML guidResolver;
   private final RelationSorterProvider sorterProvider;
   private final AbstractOseeCache<RelationType> relationCache;

   public RelationOrderRenderer(AbstractOseeCache<RelationType> relationCache, ArtifactGuidToWordML guidResolver, RelationSorterProvider sorterProvider) {
      this.relationCache = relationCache;
      this.guidResolver = guidResolver;
      this.sorterProvider = sorterProvider;
   }

   private String resolveSorter(String sorterGuid) {
      String toReturn = sorterGuid;
      try {
         IRelationSorter sorter = sorterProvider.getRelationOrder(toReturn);
         toReturn = sorter.getSorterId().prettyName();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      return toReturn;
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

   public void toWordML(Producer producer, Branch branch, RelationOrderData relationOrderData) throws OseeCoreException {
      try {
         WordMLProducer writer = (WordMLProducer) producer;
         writer.startTable();
         writer.addWordMl("<w:tblPr><w:tblW w:w=\"8200\" w:type=\"dxa\"/><w:jc w:val=\"center\"/></w:tblPr>");
         if (!relationOrderData.hasEntries()) {
            writer.addTableRow(NO_DATA_TAG);
         } else {
            writer.addTableRow("Relation Type", "Side Name", "Side", "Order Type", "Related Artifacts");
            for (Entry<Pair<String, String>, Pair<String, List<String>>> entry : relationOrderData.getOrderedEntrySet()) {
               String relationTypeName = entry.getKey().getFirst();
               String relationSide = entry.getKey().getSecond();
               String sorterGuid = entry.getValue().getFirst();

               List<String> guidList = entry.getValue().getSecond();
               List<String> mlLinks = guidResolver.resolveAsOseeLinks(branch, guidList);
               RelationType relationType = relationCache.getUniqueByName(relationTypeName);
               RelationSide side = RelationSide.fromString(relationSide);
               try {
                  writeTableRow(writer, relationType, side, resolveSorter(sorterGuid), mlLinks);
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
         writer.endTable();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }
}
