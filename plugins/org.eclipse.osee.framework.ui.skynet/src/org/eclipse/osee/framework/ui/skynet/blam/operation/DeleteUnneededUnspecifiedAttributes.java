/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class DeleteUnneededUnspecifiedAttributes extends AbstractBlam {

   @Override
   public String getName() {
      return "Delete Unneeded Unspecified Attributes";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch("Branch");
      AttributeTypeToken attributeType = variableMap.getAttributeType("Attribute Type");
      List<Artifact> artifacts =
         ArtifactQuery.getArtifactListFromAttribute(attributeType, AttributeId.UNSPECIFIED, branch);
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch, "BLAM: Delete unneeded unspecified attributes");

      for (Artifact artifact : artifacts) {
         Collection<Attribute<String>> attributes = artifact.getAttributes(attributeType);
         for (Attribute<String> attribute1 : attributes) {
            if (!attribute1.getValue().equals(AttributeId.UNSPECIFIED)) {
               for (Attribute<String> attribute : attributes) {
                  if (attribute.getValue().equals(AttributeId.UNSPECIFIED)) {
                     attribute.delete();
                  }
               }
               artifact.persist(transaction);
               break;
            }
         }
      }
      transaction.execute();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"Attribute Type\" />" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategoriesStr() {
      return Arrays.asList("Admin");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}