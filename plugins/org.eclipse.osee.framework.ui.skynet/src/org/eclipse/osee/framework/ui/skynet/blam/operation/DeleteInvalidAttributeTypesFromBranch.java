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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Megumi Telles
 */
public class DeleteInvalidAttributeTypesFromBranch extends AbstractBlam {

   private static final String ARTIFACT_IDS_WIDGET_NAME = "List of Artifact GUIDs (comma separated)";
   private static final String DESCRIPTION =
      "Provide list of artifact GUIDs that contain an invalid attribute type to delete from the selected branch";

   public DeleteInvalidAttributeTypesFromBranch() {
      super(null, DESCRIPTION, BlamUiSource.FILE);
   }

   @Override
   public String getName() {
      return "Delete Invalid Attribute Types From Branch";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) {
      BranchId branch = variableMap.getBranch("Branch");
      List<AttributeType> attributeTypes = variableMap.getAttributeTypes("Attribute Type");
      String input = variableMap.getString(ARTIFACT_IDS_WIDGET_NAME);
      Conditions.checkNotNullOrEmpty(input, ARTIFACT_IDS_WIDGET_NAME);
      List<String> inputGuids = Arrays.asList(input.split("[,\\s]+"));
      List<Artifact> arts = ArtifactQuery.getArtifactListFromIds(inputGuids, branch);
      if (arts != null && !arts.isEmpty()) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(branch, "BLAM: Delete invalid attribute type");
         for (AttributeType attrType : attributeTypes) {
            deleteInvalidAttributeType(arts, attrType, transaction);
         }
         transaction.execute();
      }

   }

   private void deleteInvalidAttributeType(List<Artifact> artifacts, AttributeType attrType, SkynetTransaction transaction) {
      for (Artifact art : artifacts) {
         if (!art.isAttributeTypeValid(attrType)) {
            delete(art, attrType);
            art.persist(transaction);
         } else {
            logf("Artifact [%s] attribute type [%s] is valid - did not delete\n", art, attrType);
         }
      }
   }

   private void delete(Artifact art, AttributeType attrType) {
      List<Attribute<?>> attrs = art.getAttributes();
      for (Attribute<?> attr : attrs) {
         if (attr.isOfType(attrType)) {
            attr.delete();
            logf("Artifact [%s] attribute type [%s] deleted: [%s]\n", art, attrType, attr);
         }
      }
   }

   @Override
   public String getXWidgetsXml() {
      return getXWidgetsXmlFromUiFile(getClass().getSimpleName(), Activator.PLUGIN_ID);
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}