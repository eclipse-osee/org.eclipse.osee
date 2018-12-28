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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Perform keyword search (in word order) for a list of keywords. Results will be in an excel spreadsheet where each
 * match will show all the keyword groups that it matched with.
 *
 * @author Donald G. Dunne
 */
public class AdvancedKeywordSearchBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Advanced Keyword Search";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      IArtifactType ofArtifactType = variableMap.getArtifactType("Filter results by Artifact Type");
      BranchId branch = variableMap.getBranch("Branch");
      if (branch == null) {
         AWorkbench.popup("Must enter Branch");
         return;
      }
      String keywordgroups = variableMap.getString("Keyword groups (one set of keywords per line)");
      if (!Strings.isValid(keywordgroups)) {
         AWorkbench.popup("Must enter keyword groups");
         return;
      }
      Collection<AttributeType> attrTypes = variableMap.getAttributeTypes("Include Attribute Values in Results");
      HashCollection<Artifact, String> artifactToMatch = new HashCollection<>(100);
      for (String keywords : keywordgroups.split(System.getProperty("line.separator"))) {
         for (Artifact art : ArtifactQuery.getArtifactListFromAttributeKeywords(branch, keywords, true, EXCLUDE_DELETED,
            false)) {
            if (ofArtifactType == null || art.isOfType(ofArtifactType)) {
               artifactToMatch.put(art, keywords);
            }
         }
      }
      XResultData resultData = new XResultData(false);
      resultData.log(String.format("[%s] on branch [%s] (keyword groups listed below results)", getName(), branch));
      resultData.addRaw(AHTML.beginMultiColumnTable(100, 1));
      List<String> headerList = new ArrayList<>();
      headerList.addAll(Arrays.asList("Artifact", "Keywords matched", "Guid", "Link", "Artifact Type"));
      for (AttributeType attrType : attrTypes) {
         headerList.add(attrType.getName());
      }
      resultData.addRaw(AHTML.addHeaderRowMultiColumnTable(headerList));
      for (Entry<Artifact, List<String>> entry : artifactToMatch.entrySet()) {
         List<String> valueList = new ArrayList<>();
         valueList.addAll(Arrays.asList(entry.getKey().getName(), Collections.toString(";", entry.getValue()),
            entry.getKey().getGuid(), XResultDataUI.getHyperlink("open", entry.getKey()),
            entry.getKey().getArtifactTypeName()));
         for (AttributeType attrType : attrTypes) {
            valueList.add(entry.getKey().getAttributesToString(attrType));
         }
         resultData.addRaw(AHTML.addRowMultiColumnTable(valueList.toArray(new String[valueList.size()]), null));
      }
      resultData.addRaw(AHTML.endMultiColumnTable());
      resultData.log("Keywordgroups: \n" + keywordgroups);
      XResultDataUI.report(resultData, String.format("[%s] on branch [%s]", getName(), branch));
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" />" + //
         "<XWidget xwidgetType=\"XText\" fill=\"Vertically\"  displayName=\"Keyword groups (one set of keywords per line)\" />" + //
         "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"Filter results by Artifact Type\" />" + //
         "<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\" displayName=\"Include Attribute Values in Results\" />" + //
         "</xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }

   @Override
   public String getDescriptionUsage() {
      return "Perform keyword quick search (in word order) for a list of keywords.  Results will show artifacts paired with matching keyword groups.";
   }
}