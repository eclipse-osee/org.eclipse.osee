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

package org.eclipse.osee.framework.core.publishing;

import java.util.Map;

/**
 * @author Morgan E. Cook
 */

public enum RendererOption {

   /*
    * Boolean
    */

   ADD_MERGE_TAG("Add Merge Tag", OptionType.Boolean),

   ALL_ATTRIBUTES("All Attributes", OptionType.Boolean),

   ATTRIBUTE_NAME("Attribute Name", OptionType.String),

   BRANCH("Branch", OptionType.BranchId),

   /**
    * Renderers can do the rendering in the client or on server. Server side renderers will return an input stream from
    * the web server. As the data arrives the FileSystemRenderer base code can write the data to the content file in the
    * workspace. Client side renderes can write either write all of there data into a buffer and create an input stream
    * that reads from that buffer after the render is complete; or they can write there data to a piped output stream
    * that was provided to it.
    */

   CLIENT_RENDERER_CAN_STREAM("Client Renderer Can Stream", OptionType.Boolean),

   COMPARE_BRANCH("Compare Branch", OptionType.BranchId),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   DIFF_NO_ATTRIBUTES_VALUE("DIFF_NO_ATTRIBUTES", OptionType.String),

   DIFF_VALUE("DIFF", OptionType.String),

   EXCLUDE_ARTIFACT_TYPES("Exclude Artifact Types", OptionType.ArtifactTypes),

   EXCLUDE_FOLDERS("Exclude Folders", OptionType.Boolean),

   EXECUTE_VB_SCRIPT("execute.vb.script", OptionType.String),

   /**
    * Flag used to track the number of Publishing Template requests when {@link #USE_TEMPLATE_ONCE} is
    * <code>true</code>.
    */

   FIRST_TIME("First Time", OptionType.Boolean),

   ID("Id", OptionType.String),

   IN_PUBLISH_MODE("In Publish Mode", OptionType.Boolean),

   INCLUDE_UUIDS("Include Uuids", OptionType.Boolean),

   LINK_TYPE("linkType", OptionType.LinkType),

   MAINTAIN_ORDER("Maintain Order", OptionType.Boolean),

   MAX_OUTLINE_DEPTH("Maximum Outlining Depth", OptionType.Integer),

   NAME("Name", OptionType.String),

   NO_DISPLAY("No Display", OptionType.Boolean),

   /**
    * A document display option value that may be used with the {@link RendererOption#OPEN_OPTION} key.
    */

   OPEN_IN_MS_WORD_VALUE("MS Word", OptionType.String),

   /**
    * A key used in a {@link Map} of {@link RendererOption} key value pairs to specify how a rendered document is to be
    * opened for display.
    */

   OPEN_OPTION("open.option", OptionType.String),

   ORIG_PUBLISH_AS_DIFF("Orig Publish As Diff", OptionType.Boolean),

   OUTLINE_TYPE("Outline Type", OptionType.String),

   OUTPUT_STREAM("Output Stream", OptionType.OutputStream),

   OVERRIDE_DATA_RIGHTS("Override Data Rights", OptionType.String),

   PARAGRAPH_NUMBER("Paragraph Number", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_NO_ATTRIBUTES_VALUE("PREVIEW_ALL_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_RECURSE_NO_ATTRIBUTES_VALUE("PREVIEW_WITH_RECURSE_NO_ATTRIBUTES", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_RECURSE_VALUE("PREVIEW_ALL_RECURSE", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_ALL_VALUE("PreviewAll", OptionType.String),

   /**
    * A Publishing Template selection option value that may be used with the {@link RenderOption#TEMPLATE_OPTION} key.
    */

   PREVIEW_WITH_RECURSE_VALUE("PREVIEW_WITH_RECURSE", OptionType.String),

   PROGRESS_MONITOR("Progress Monitor", OptionType.ProgressMonitor),

   PUBLISH_DIFF("Publish Diff", OptionType.Boolean),

   /**
    * When <code>false</code>, "Heading - MS Word" artifacts that do not have children that are publishable will be
    * excluded from the publish.
    */

   PUBLISH_EMPTY_HEADERS("Push Empty Headers", OptionType.Boolean),

   /**
    * This option is used to specify the Publishing Template Manager's identifier for a Publishing Template to the
    * renderer. The {@link MSWordTemplateClientRenderer} will use the specified template when:
    * <ul>
    * <li>{@link RendererOption#USE_TEMPLATE_ONCE} is unset or <code>false</code>;</li>
    * <li>{@link RendererOption#FIRST_TIME} is set and <code>true</code>; or</li>
    * <li>{@link RendererOption#SECOND_TIME} is set and <code>true</code>.</li>
    * </ul>
    */

   PUBLISHING_TEMPLATE_IDENTIFIER("Publishing Template Identifier", OptionType.String),

   RECURSE("Recurse", OptionType.Boolean),

   RECURSE_ON_LOAD("Recurse On Load", OptionType.Boolean),

   RESULT_PATH_RETURN("resultPath", OptionType.String),

   /**
    * Flag used to track the number of Publishing Template requests when {@link #USE_TEMPLATE_ONCE} is
    * <code>true</code>.
    */

   SECOND_TIME("SecondTime", OptionType.Boolean),

   /*
    * For client side streaming renderers, this option is set with the output stream for the render to use.
    */

   SKIP_DIALOGS("Skip Dialogs", OptionType.Boolean),

   /*
    * Open Option and Values
    */

   SKIP_ERRORS("Skip Errors", OptionType.Boolean),

   TEMPLATE_ONLY("Template Only", OptionType.Boolean),

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

   THREE_WAY_MERGE("THREE_WAY_MERGE", OptionType.String),

   TRANSACTION_OPTION("Transaction Option", OptionType.Transaction),

   UPDATE_PARAGRAPH_NUMBERS("Update Paragraph Numbers", OptionType.Boolean),

   USE_ARTIFACT_NAMES("Use Artifact Names", OptionType.Boolean),

   USE_PARAGRAPH_NUMBERS("Use Paragraph Numbers", OptionType.Boolean),

   /**
    * When <code>true</code> the first two Publishing Template requests will be by match criteria and the remaining will
    * be by the Publishing Template artifact identifier.
    */

   USE_TEMPLATE_ONCE("Use Template Once", OptionType.Boolean),

   VIEW("View", OptionType.ArtifactId),

   WAS_BRANCH("Was Branch", OptionType.BranchId);

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
