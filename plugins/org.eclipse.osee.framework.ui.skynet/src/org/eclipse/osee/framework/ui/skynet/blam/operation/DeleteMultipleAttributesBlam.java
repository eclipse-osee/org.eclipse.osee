/*******************************************************************************
 * Copyright (c) 2014 Boeing.
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
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author John Misinco
 */
public class DeleteMultipleAttributesBlam extends AbstractBlam {

   private static String SELECT_BRANCH_LABEL = "Select Branch";
   private static String SELECT_ATTRIBUTE_TYPE = "Attribute Type";
   private static String ATTRIBUTE_TO_KEEP = "Attribute To Keep";

   @Override
   public String getName() {
      return "Delete Multiple Attributes";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" multiSelect=\"false\" displayName=\"");
      builder.append(SELECT_BRANCH_LABEL);
      builder.append("\" />");
      builder.append("<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"");
      builder.append(SELECT_ATTRIBUTE_TYPE);
      builder.append("\" />");
      builder.append("<XWidget xwidgetType=\"XRadioButtons(Newest,Oldest)\" displayName=\"");
      builder.append(ATTRIBUTE_TO_KEEP);
      builder.append("\" defaultValue=\"Newest\"/>");
      builder.append("\" />");
      builder.append("</XWidgets>");
      return builder.toString();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      boolean keepOldest = "Oldest".equals(variableMap.getRadioSelection(ATTRIBUTE_TO_KEEP));
      BranchId branch = variableMap.getBranch(SELECT_BRANCH_LABEL);
      if (branch == null) {
         log("ERROR: A branch must be selected!");
         return;
      }

      List<AttributeType> attributeTypes = variableMap.getAttributeTypes(SELECT_ATTRIBUTE_TYPE);
      if (!Conditions.hasValues(attributeTypes)) {
         log("ERROR: At least one attribute type must be selected!");
         return;
      }

      for (AttributeType type : attributeTypes) {
         if (type.getMaxOccurrences() != 1) {
            log("ERROR: The selected type [" + type.getName() + "] does not have a max occurrance of 1!");
            return;
         }
      }

      QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(branch);
      for (AttributeType type : attributeTypes) {
         query.andExists(type);
      }

      SkynetTransaction txs = TransactionManager.createTransaction(branch, "Delete multiple attributes");

      for (Artifact art : query.getResults()) {
         for (AttributeType type : attributeTypes) {
            // get attributes of this type
            @SuppressWarnings("deprecation")
            List<Attribute<Object>> attributes = art.getAttributes(type);
            // if there are more than 1
            if (attributes.size() > 1) {
               // determine oldest / newest
               Attribute<Object> toKeep = attributes.get(0);
               for (Attribute<Object> attr : attributes) {
                  if (keepOldest) {
                     if (attr.getGammaId().isLessThan(toKeep.getGammaId())) {
                        toKeep = attr;
                     }
                  } else {
                     if (attr.getGammaId().isGreaterThan(toKeep.getGammaId())) {
                        toKeep = attr;
                     }
                  }
               }
               // delete the rest
               attributes.remove(toKeep);
               for (Attribute<Object> attr : attributes) {
                  art.deleteAttribute(attr);
               }
               art.persist(txs);
            }
         }
      }
      txs.execute();
   }

   @Override
   public String getDescriptionUsage() {
      return "Deletes attributes that appear multiple times when only a single instance is defined for its type.  " + //
         "If multiple instances are detected, leaves a single instance on the artifact based on user's selection of " + //
         "either newest or oldest.";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}