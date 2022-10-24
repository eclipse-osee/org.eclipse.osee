/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.core.util;

import java.util.Map;

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
   ID("Id", OptionType.String),
   NAME("Name", OptionType.String),
   OVERRIDE_DATA_RIGHTS("Override Data Rights", OptionType.String),
   ATTRIBUTE_NAME("Attribute Name", OptionType.String),
   PARAGRAPH_NUMBER("Paragraph Number", OptionType.String),
   OUTLINE_TYPE("Outline Type", OptionType.String),
   RESULT_PATH_RETURN("resultPath", OptionType.String),
   EXECUTE_VB_SCRIPT("execute.vb.script", OptionType.String),

   /*
    * Open Option and Values
    */

   /**
    * A key used in a {@link Map} of {@link RendererOption} key value pairs to specify how a rendered document is to be
    * opened for display.
    */

   OPEN_OPTION("open.option", OptionType.String),

   /**
    * A document display option value that may be used with the {@link RendererOption#OPEN_OPTION} key.
    */

   OPEN_IN_MS_WORD_VALUE("MS Word", OptionType.String),

   /*
    * Template Option and Values
    */

   /**
    * A key used in a {@link Map} of {@link RendererOption} key value pairs to specify the Publishing Template selection
    * option.
    */

   TEMPLATE_OPTION("Template", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_VALUE("PreviewAll", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_NO_ATTRIBUTES_VALUE("PREVIEW_ALL_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_WITH_RECURSE_VALUE("PREVIEW_WITH_RECURSE", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_RECURSE_VALUE("PREVIEW_ALL_RECURSE", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_WITH_RECURSE_NO_ATTRIBUTES_VALUE("PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", OptionType.String),

   DIFF_VALUE("DIFF", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   DIFF_NO_ATTRIBUTES_VALUE("DIFF_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   THREE_WAY_MERGE("THREE_WAY_MERGE", OptionType.String);

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
