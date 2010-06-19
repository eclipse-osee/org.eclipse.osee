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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderData;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderFactory;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.xml.sax.SAXException;

public class RelationOrderRepairBlam extends AbstractBlam {
   SkynetTransaction transaction;
   boolean recurse;

   @Override
   public List<DynamicXWidgetLayoutData> getLayoutDatas() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, CoreException {
      return super.getLayoutDatas();
   }

   @Override
   public String getName() {
      return "Relation Order Repair";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder widgets = new StringBuilder();
      widgets.append("<xWidgets>");
      widgets.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" />)");
      widgets.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Recurse Over Hierarchy\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      widgets.append("</xWidgets>");
      return widgets.toString();
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.asCollection("Admin");
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      recurse = variableMap.getBoolean("Recurse Over Hierarchy");
      List<Artifact> inputArtifacts = variableMap.getArtifacts("Artifacts");
      if (inputArtifacts.isEmpty()) {
         return;
      }
      Branch branch = getBranch(inputArtifacts);
      transaction = new SkynetTransaction(branch, getName());
      for (Artifact art : inputArtifacts) {
         resetRelationOrder(art);
      }
      transaction.execute();
   }

   private Branch getBranch(List<Artifact> arts) throws OseeArgumentException {
      Branch branch = arts.get(0).getBranch();
      for (Artifact art : arts) {
         if (!art.getBranch().equals(branch)) {
            throw new OseeArgumentException("Input artifacts must be on same branch");
         }
      }

      return branch;
   }

   private void resetRelationOrder(Artifact art) throws OseeCoreException, IOException {
      RelationOrderData currentData = new RelationOrderFactory().createRelationOrderData(art);
      for (Pair<String, String> typeSide : currentData.getAvailableTypeSides()) {
         RelationType type;
         try {
            type = RelationTypeManager.getType(typeSide.getFirst());
         } catch (OseeTypeDoesNotExist ex) {
            getOutput().append(
                  String.format("Type [%s] on artifact [%s] does not exist\n", typeSide.getFirst(), art.getName()));
            return;
         }
         RelationSide side = RelationSide.fromString(typeSide.getSecond());
         String sorterGuid = currentData.getCurrentSorterGuid(type, side);

         if (sorterGuid.equals(RelationOrderBaseTypes.USER_DEFINED.getGuid())) {
            List<String> orderList = currentData.getOrderList(type, side);
            List<String> actualOrder = Artifacts.toGuids(art.getRelatedArtifacts(new RelationTypeSide(type, side)));
            if (!orderList.equals(actualOrder)) {
               getOutput().append(String.format("Incorrect order on %s (%s %s)\n", art.getName(), type, side));
               currentData.storeFromGuids(type, side, RelationOrderBaseTypes.USER_DEFINED, actualOrder);
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
