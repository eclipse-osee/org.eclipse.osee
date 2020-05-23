/*********************************************************************
 * Copyright (c) 2020 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.compare;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderParser;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.swt.graphics.Image;

/**
 * @author Branden W. Phillips
 */
public class RelationOrderCompareItem extends CompareItem {
   private static final Pattern orderListPattern = Pattern.compile("<OrderList>");
   private static final Pattern editPattern = Pattern.compile("EDIT START[\\s\\S]+?EDIT END");
   private static final Pattern idPattern = Pattern.compile("'\\d+'");
   private final XResultData results;

   private final AttributeConflict attributeConflict;
   private final RelationOrderData relationData;

   public RelationOrderCompareItem(AttributeConflict attributeConflict, RelationOrderData relationData, String name, String contents, boolean isEditable, Image image, String diffFilename) {
      super(name, contents, System.currentTimeMillis(), isEditable, image, diffFilename);

      this.attributeConflict = attributeConflict;
      this.relationData = relationData;
      this.results = new XResultData();
   }

   @Override
   public void persistContent() {
      try {
         String stringContent = getStringContent();
         Iterator<Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>>> relationDataIterator =
            relationData.getOrderedEntrySet().iterator();

         Matcher editMatch = editPattern.matcher(stringContent);
         while (editMatch.find()) {
            if (relationDataIterator.hasNext()) {
               Entry<Pair<RelationTypeToken, RelationSide>, Pair<RelationSorter, List<String>>> relationOrder =
                  relationDataIterator.next();

               String editedList = stringContent.substring(editMatch.start(), editMatch.end());
               List<String> guidList = getGuidList(editedList);

               relationOrder.getValue().setSecond(guidList);
            } else {
               results.errorf("A relation type was removed or there was an error - Save Failed\n");
               break;
            }
         }

         if (relationDataIterator.hasNext()) {
            results.errorf(
               "Not all relation types were processed, make sure the edit tags are correct along with the number of relation types - Save Failed\n");
         }

         if (results.isErrors()) {
            results.log("Please reset the conflict by right clicking, and try the merge again");
            XResultDataUI.report(results, "Relation Order Errors");
            results.clear();
         } else {
            RelationOrderParser parser = new RelationOrderParser();
            String xml = parser.toXml(relationData);

            attributeConflict.setAttributeValue(xml);
         }
      } catch (Exception ex) {
         OseeLog.log(RelationOrderCompareItem.class, Level.SEVERE, ex);
      }
   }

   /**
    * Extracts the guids from the edited list of artifact tokens. Checks to see if the artifact exists on the current
    * branch, if no, gets artifact from destination branch. If it does exist but was deleted, logs an error because a
    * deleted artifact should not be in the relations. If it does exist normally, the guid is added to the list.
    * Otherwise, an unknown error is thrown.
    */
   private List<String> getGuidList(String editedList) {
      List<String> guidList = new LinkedList<>();

      Matcher idMatch = idPattern.matcher(editedList);
      while (idMatch.find()) {
         String artIdString = editedList.substring(idMatch.start() + 1, idMatch.end() - 1);
         Artifact art = ArtifactQuery.checkArtifactFromId(ArtifactId.valueOf(artIdString),
            attributeConflict.getSourceBranch(), DeletionFlag.INCLUDE_DELETED);
         if (art == null) {
            ArtifactId artId = ArtifactId.valueOf(artIdString);
            if (artId.isValid()) {
               art = ArtifactQuery.checkArtifactFromId(artId, attributeConflict.getDestBranch());
            }
            if (art != null) {
               guidList.add(art.getGuid());
            } else {
               results.errorf(
                  "Artifact with id " + artId.toString() + " cannot be found on either Source or Destination branch - Save Failed\n");
            }
         } else if (art.isDeleted()) {
            results.errorf(
               "Artifact '" + art.getName() + "' with id " + art.getArtId() + " was deleted on this source branch - Save Failed\n");
         } else if (!art.isDeleted()) {
            guidList.add(art.getGuid());
         } else {
            results.errorf(
               "There was a problem with Artifact '" + art.getName() + "' with id " + art.getArtId() + " Save Failed\n");
         }
      }

      return guidList;
   }
}
