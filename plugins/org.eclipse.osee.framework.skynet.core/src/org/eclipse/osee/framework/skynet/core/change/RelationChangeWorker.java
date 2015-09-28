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
package org.eclipse.osee.framework.skynet.core.change;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderMergeUtility;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderParser;

/**
 * @author Jeff C. Phillips
 * @author Wilik Karol
 */
public class RelationChangeWorker implements IChangeWorker {
   private final RelationChange change;
   private final Artifact artifact;
   private RelationLink link;

   public RelationChangeWorker(Change change, Artifact artifact) {
      this.change = (RelationChange) change;
      this.artifact = artifact;
   }

   private RelationLink setRelationLink() throws OseeCoreException {
      if (link == null) {
         link =
            RelationManager.getOrCreate(change.getArtId(), change.getBArtId(), artifact.getBranch(),
               change.getRelationType(), change.getRelLinkId(), (int) change.getGamma(), change.getRationale(),
               change.getModificationType());
      }
      return link;
   }

   @Override
   public void revert() throws OseeCoreException {

      setRelationLink();

      if (change.isBaseline()) {
         link.replaceWithVersion((int) change.getBaselineGamma());

         Artifact otherSideCurrent =
            ArtifactQuery.getArtifactFromId(link.getArtifactOnOtherSide(artifact).getArtId(), artifact.getBranch());

         if (hasRelationOrder(otherSideCurrent)) {

            Artifact otherSideBase =
               ArtifactQuery.getHistoricalArtifactFromId(link.getArtifactOnOtherSide(artifact).getArtId(),
                  artifact.getFullBranch().getBaseTransaction(), DeletionFlag.INCLUDE_DELETED);

            RelationOrderData mergedOrderData =
               RelationOrderMergeUtility.mergeRelationOrder(otherSideBase, otherSideCurrent);

            if (mergedOrderData != null) {

               int baselineOrderGamma = otherSideBase.getSoleAttribute(CoreAttributeTypes.RelationOrder).getGammaId();

               Attribute<?> relationOrder = setRelationOrder(otherSideCurrent, mergedOrderData);

               Object currentRelationOrder =
                  otherSideCurrent.getSoleAttribute(CoreAttributeTypes.RelationOrder).getValue();

               Object baseRelationOrder = otherSideBase.getSoleAttribute(CoreAttributeTypes.RelationOrder).getValue();

               //If the relationOrder merge is the same as baseline there will be no change.
               if (currentRelationOrder.equals(baseRelationOrder)) {
                  relationOrder.replaceWithVersion(baselineOrderGamma);
               }
            } else {
               //Append to the relation order
               RelationOrderData relationOrderData =
                  new RelationOrderFactory().createRelationOrderData(otherSideCurrent);

               List<String> guids = new ArrayList<>();

               for (Entry<Pair<String, String>, Pair<String, List<String>>> relationOrderGuidData : relationOrderData.entrySet()) {
                  guids.addAll(relationOrderGuidData.getValue().getSecond());
               }
               guids.add(artifact.getGuid());

               relationOrderData.addOrderList(link.getRelationType(), link.getSide(artifact),
                  RelationOrderBaseTypes.USER_DEFINED, guids);
               setRelationOrder(otherSideCurrent, relationOrderData);
            }
            otherSideCurrent.persist("Replace With Baseline Version resolved by appending");
         }
      } else {
         link.delete(true);
      }
   }

   private boolean hasRelationOrder(Artifact artifact) throws OseeCoreException {
      return artifact.getAttributeCount(CoreAttributeTypes.RelationOrder) > 0;
   }

   private Attribute<?> setRelationOrder(Artifact artifact, RelationOrderData relationOrderData) throws OseeCoreException {
      Attribute<?> relationOrder;

      RelationOrderParser parser = new RelationOrderParser();
      String attributeXMLValue = parser.toXml(relationOrderData);
      relationOrder = artifact.getSoleAttribute(CoreAttributeTypes.RelationOrder);
      relationOrder.setFromString(attributeXMLValue);
      return relationOrder;
   }
}
