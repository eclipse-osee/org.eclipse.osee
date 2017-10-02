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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

public class RelationOrderRepairBlam extends AbstractBlam {
   private SkynetTransaction transaction;
   private boolean recurse;

   @Override
   public String getName() {
      return "Relation Order Repair";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder widgets = new StringBuilder();
      widgets.append("<xWidgets>");
      widgets.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" />)");
      widgets.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Recurse Over Hierarchy\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      widgets.append("</xWidgets>");
      return widgets.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Admin");
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> inputArtifacts = variableMap.getArtifacts("Artifacts");
      if (inputArtifacts.isEmpty()) {
         return;
      }
      recurse = variableMap.getBoolean("Recurse Over Hierarchy");
      BranchId branch = getBranch(inputArtifacts);
      transaction = TransactionManager.createTransaction(branch, getName());
      for (Artifact art : inputArtifacts) {
         resetRelationOrder(art);
      }
      transaction.execute();
   }

   private BranchId getBranch(List<Artifact> arts) {
      Artifact firstArt = arts.get(0);
      for (Artifact art : arts) {
         if (!firstArt.isOnSameBranch(art)) {
            throw new OseeArgumentException("Input artifacts must be on same branch");
         }
      }

      return firstArt.getBranch();
   }

   private void resetRelationOrder(Artifact art) throws IOException {
      RelationOrderData currentData = new RelationOrderFactory().createRelationOrderData(art);
      for (Pair<RelationTypeToken, RelationSide> typeSide : currentData.getAvailableTypeSides()) {
         RelationType type;
         try {
            type = RelationTypeManager.getType(typeSide.getFirst());
         } catch (OseeTypeDoesNotExist ex) {
            logf("Type [%s] on artifact [%s] does not exist\n", typeSide.getFirst(), art.getName());
            return;
         }
         RelationSide side = typeSide.getSecond();
         RelationSorter sorterGuid = currentData.getCurrentSorterGuid(type, side);

         if (sorterGuid.equals(USER_DEFINED)) {
            List<String> orderList = currentData.getOrderList(type, side);
            List<String> actualOrder = Artifacts.toGuids(RelationManager.getRelatedArtifacts(art, type, side));
            if (!orderList.equals(actualOrder)) {
               logf("Incorrect order on %s (%s %s)\n", art.getName(), type, side);
               currentData.storeFromGuids(type, side, USER_DEFINED, actualOrder);
               art.persist(transaction);
            }
         }
      }

      if (recurse) {
         for (Artifact child : art.getChildren()) {
            resetRelationOrder(child);
         }
      }
   }
}
