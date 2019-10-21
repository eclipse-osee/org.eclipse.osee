/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.util;

/**
 * @author Morgan E. Cook
 */
public enum RendererOption {
   // Boolean
   PUBLISH_DIFF("Publish Diff", OptionType.Boolean),
   ORIG_PUBLISH_AS_DIFF("Orig Publish As Diff", OptionType.Boolean),
   ADD_MERGE_TAG("Add Merge Tag", OptionType.Boolean),
   INCLUDE_UUIDS("Include Uuids", OptionType.Boolean),
   UPDATE_PARAGRAPH_NUMBERS("Update Paragraph Numbers", OptionType.Boolean),
   USE_PARAGRAPH_NUMBERS("Use Paragraph Numbers", OptionType.Boolean),
   SKIP_ERRORS("Skip Errors", OptionType.Boolean),
   EXCLUDE_FOLDERS("Exclude Folders", OptionType.Boolean),
   USE_TEMPLATE_ONCE("Use Template Once", OptionType.Boolean),
   RECURSE("Recurse", OptionType.Boolean),
   RECURSE_ON_LOAD("Recurse On Load", OptionType.Boolean),
   USE_ARTIFACT_NAMES("Use Artifact Names", OptionType.Boolean),
   ALL_ATTRIBUTES("All Attributes", OptionType.Boolean),
   MAINTAIN_ORDER("Maintain Order", OptionType.Boolean),
   NO_DISPLAY("No Display", OptionType.Boolean),
   SKIP_DIALOGS("Skip Dialogs", OptionType.Boolean),
   FIRST_TIME("First Time", OptionType.Boolean),
   SECOND_TIME("SecondTime", OptionType.Boolean),
   TEMPLATE_ONLY("Template Only", OptionType.Boolean),
   IN_PUBLISH_MODE("In Publish Mode", OptionType.Boolean),
   PUBLISH_EMPTY_HEADERS("Push Empty Headers", OptionType.Boolean),

   // BranchId
   BRANCH("Branch", OptionType.BranchId),
   COMPARE_BRANCH("Compare Branch", OptionType.BranchId),
   WAS_BRANCH("Was Branch", OptionType.BranchId),

   // Artifact
   VIEW("View", OptionType.ArtifactId),
   TEMPLATE_ARTIFACT("Template Artifact", OptionType.ArtifactId),

   // LinkType
   LINK_TYPE("linkType", OptionType.LinkType),

   // ArtifactType
   EXCLUDE_ARTIFACT_TYPES("Exclude Artifact Types", OptionType.ArtifactType),

   // ProgressMonitor
   PROGRESS_MONITOR("Progress Monitor", OptionType.ProgressMonitor),

   // SkynetTransaction
   TRANSACTION_OPTION("Transaction Option", OptionType.Transaction),

   // String
   ORCS_QUERY("Orcs Query", OptionType.String),
   OVERRIDE_DATA_RIGHTS("Override Data Rights", OptionType.String),
   ATTRIBUTE_NAME("Attribute Name", OptionType.String),
   PARAGRAPH_NUMBER("Paragraph Number", OptionType.String),
   OUTLINE_TYPE("Outline Type", OptionType.String),
   RESULT_PATH_RETURN("resultPath", OptionType.String),
   OPEN_OPTION("open.option", OptionType.String),
   EXECUTE_VB_SCRIPT("execute.vb.script", OptionType.String),
   TEMPLATE_OPTION("Template", OptionType.String),
   PREVIEW_ALL_NO_ATTRIBUTES_VALUE("PREVIEW_ALL_NO_ATTRIBUTES", OptionType.String),
   PREVIEW_WITH_RECURSE_VALUE("PREVIEW_WITH_RECURSE", OptionType.String),
   PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE("PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", OptionType.String),
   DIFF_VALUE("DIFF", OptionType.String),
   DIFF_NO_ATTRIBUTES_VALUE("DIFF_NO_ATTRIBUTES", OptionType.String),
   THREE_WAY_MERGE("THREE_WAY_MERGE", OptionType.String),
   ID("Id", OptionType.String),
   NAME("Name", OptionType.String);

   private final String key;
   private final OptionType type;

   RendererOption(String key, OptionType type) {
      this.key = key;
      this.type = type;
   }

   public String getKey() {
      return key;
   }

   public OptionType getType() {
      return type;
   }

}
