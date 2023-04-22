/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.publishing.datarights;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.token.DataRightsClassificationAttributeType;
import org.eclipse.osee.framework.core.enums.token.PageOrientationAttributeType;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;

/**
 * This class defines defaults and constants for the Data Rights Manager.
 *
 * @author Loren K. Ashley
 */

class DataRightConfiguration {

   /**
    * The attribute definition for the attribute of the publishing artifact to obtain the data right classification
    * from. When the publishing artifact does not have an attribute of this type, the {@link #defaultClassification}
    * value will be used.
    */

   static final DataRightsClassificationAttributeType classificationAttribute =
      CoreAttributeTypes.DataRightsClassification;

   /**
    * The data rights classification to use for an artifact when the artifact does not have the
    * {@link #classificationAttribute} defined or it contains an invalid value or invalid values.
    */

   static final String defaultClassification = "Unspecified";

   /**
    * The {@link WordCoreUtil.pageType} to use for an artifact when the artifact does not have the
    * {@link #pageOrientationAttribute} defined or it contains an invalid or invalid values.
    */

   static final WordCoreUtil.pageType defaultPageOrientation = WordCoreUtil.pageType.PORTRAIT;

   /**
    * The {@link ArtifactToken} for the artifact to extract data right classification mappings from.
    */

   static final ArtifactToken mappingArtifact = CoreArtifactTokens.DataRightsFooters;

   /**
    * The attribute definition for the attribute of the {@link #mappingArtifact} to extract the data right
    * classification mappings from.
    *
    * @implSpec This attribute is expected to be multi-value {@link AttributeTypeString} attribute. The first line of
    * each attribute value is expected to contain the data right classification name. The remaining lines of the
    * attribute are expected to contain the Word ML for that data right's footer.
    */

   static final AttributeTypeString mappingAttribute = CoreAttributeTypes.GeneralStringData;

   /**
    * The attribute definition for the attribute of the publishing artifact to obtain the page orientation from. When
    * the publishing artifact does not have an attribute of this type, the {@link #defaultPageOrientation} value will be
    * used.
    */

   static final PageOrientationAttributeType pageOrientationAttribute = CoreAttributeTypes.PageOrientation;

   /**
    * The default Word ML to use for an unspecified data rights footer.
    */

   //@formatter:off
   static final String unspecifiedValue =
      new StringBuilder( 4096 )
         .append( "<w:r><w:t>" )
         .append(    "<w:ftr w:type=\"odd\">" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:pStyle w:val=\"para8pt\"/><w:jc w:val=\"center\"/></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"begin\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:instrText> PAGE </w:instrText></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"separate\"/></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/><w:noProof/></w:rPr><w:t>5</w:t></w:r><w:r><w:rPr><w:rStyle w:val=\"PageNumber\"/></w:rPr><w:fldChar w:fldCharType=\"end\"/></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>UNSPECIFIED - PLEASE TAG WITH CORRECT DATA RIGHTS ATTRIBUTE!!!</w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contract No.: </w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contractor Name: Acme Corporation</w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Contractor Address: 1234 W. World Road; Jersy City, NJ 07002</w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t></w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>The Government's rights to use, modify, reproduce, release, perform, display, or disclose this software are restricted by paragraph (b)(3) of the Rights in Noncommercial Computer Software and Noncommercial Computer Software Documentation clause contained in the above identified contract.  Any reproduction of computer software or portions thereof marked with this legend must also reproduce the markings.  Any person, other than the Government, who has been provided access to such software must promptly notify the above named Contractor. </w:t></w:r>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(       "</w:p>" )
         .append(       "<w:p>" )
         .append(          "<w:pPr><w:spacing w:before=\"0\" w:after=\"0\" w:line=\"240\" w:line-rule=\"auto\"/><w:jc w:val=\"both\"/><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr></w:pPr>" )
         .append(          "<w:r><w:rPr><w:rFonts w:cs=\"Arial\"/><w:sz w:val=\"16\"/></w:rPr><w:t>Copyright (c) 2017 – Acme Corporation</w:t></w:r>" )
         .append(       "</w:p>" )
         .append(    "</w:ftr>" )
         .append( "</w:t></w:r>" )
         .toString();
   //@formatter:on

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private DataRightConfiguration() {
   }

}

/* EOF */
