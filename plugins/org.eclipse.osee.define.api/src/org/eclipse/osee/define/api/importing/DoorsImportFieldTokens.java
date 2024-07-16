/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.api.importing;

import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author David W. Miller
 */
public final class DoorsImportFieldTokens {
   private static final String BLOCK_ATTR_REGEX = "^[^:]+:(?:\\s|[^\\p{ASCII}])*(.*)";
   private static final String IDEN_REGEX = "^Object I[dD].*";
   private static final String LEVE_REGEX = "^Object Leve.*";
   private static final String TYPE_REGEX = "^Object Type.*";
   private static final String CAPTION_REGEX = "^Caption Type.*";
   private static final String HEAD_REGEX = "^Object Head.*";
   private static final String NUMB_REGEX = "^Object Numb.*";
   private static final String TEXT_REGEX = "^Object Text.*";
   private static final String PLAIN_TEXT_REGEX = "^Object Plain Text.*";
   private static final String STATE_REGEX = "^Object State.*";
   private static final String MODIFIED_REGEX = "^Object Modified.*";
   private static final String DELETED_REGEX = "^Object Del.*";
   private static final String COMMENTS_REGEX = "^Comments.*";
   private static final String EXT_LINK_REGEX = "^Ext Doc Link.*";
   private static final String DESCRIPTION_REGEX = "^Functional Description.*";
   private static final String DEFINITION_REGEX = "^Definition.*";
   private static final String REFDOC_NAME_REGEX = "^RefDocFilename.*";
   private static final String SAFETY_HAZARD_REGEX = "^SafetyHazardDescription.*";

   private static final String OBJECT_TEXT_REGEX = "(.*?)<w:r>(.*?)</w:r>(.*)";

   // @formatter:off
   public static final BlockFieldToken blockAttrIdentifier =  BlockFieldToken.valueOf(1, "Identifier", IDEN_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.DoorsId);
   public static final BlockFieldToken blockAttrLevel =       BlockFieldToken.valueOf(2, "Level", LEVE_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrHeading =     BlockFieldToken.valueOf(3, "Heading", HEAD_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrNumber =      BlockFieldToken.valueOf(4, "Number", NUMB_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.DoorsHierarchy);
   public static final BlockFieldToken blockAttrType =        BlockFieldToken.valueOf(6, "Type", TYPE_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrCaption =     BlockFieldToken.valueOf(13, "Caption", CAPTION_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrText =        BlockFieldToken.valueOf(7, "Text", TEXT_REGEX, OBJECT_TEXT_REGEX, BlockFieldText::new, CoreAttributeTypes.WordTemplateContent);
   public static final BlockFieldToken blockAttrState =       BlockFieldToken.valueOf(8, "State", STATE_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrModified =    BlockFieldToken.valueOf(10, "Modified", MODIFIED_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrDeleted =     BlockFieldToken.valueOf(11, "Deleted", DELETED_REGEX, BLOCK_ATTR_REGEX, BlockField::new);
   public static final BlockFieldToken blockAttrComments =    BlockFieldToken.valueOf(12, "Comments", COMMENTS_REGEX, OBJECT_TEXT_REGEX, BlockField::new, CoreAttributeTypes.Annotation);
   public static final BlockFieldToken blockAttrExtLink =     BlockFieldToken.valueOf(14, "ExternalLink", EXT_LINK_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.ContentUrl);
   public static final BlockFieldToken blockAttrPlainText =   BlockFieldToken.valueOf(15, "Plain Text", PLAIN_TEXT_REGEX, OBJECT_TEXT_REGEX, BlockField::new); // TODO write a plain text converter - see BlockFieldText. Use that constructor here
   public static final BlockFieldToken blockAttrDescription = BlockFieldToken.valueOf(16, "Description", DESCRIPTION_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.Description);
   public static final BlockFieldToken blockAttrAcronym =     BlockFieldToken.valueOf(17, "Definition", DEFINITION_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.Acronym);
   public static final BlockFieldToken blockAttrNative =      BlockFieldToken.valueOf(18, "Native Content", REFDOC_NAME_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.NativeContent);
   public static final BlockFieldToken blockAttrSafetyHazard = BlockFieldToken.valueOf(19, "Hazard", SAFETY_HAZARD_REGEX, BLOCK_ATTR_REGEX, BlockField::new, CoreAttributeTypes.Hazard);
   // @formatter:on
}
