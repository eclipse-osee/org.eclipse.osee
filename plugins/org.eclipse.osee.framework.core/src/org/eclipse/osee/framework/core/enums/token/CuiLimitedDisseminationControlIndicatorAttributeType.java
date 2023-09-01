/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.enums.token;

import java.util.stream.Stream;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.publishing.CuiLimitedDisseminationControlIndicator;

/**
 * Initialize enumeration of CUI Limited Dissemination Control Indicator
 *
 * @author Md I. Khan
 * @author Loren K. Ashley
 */

public class CuiLimitedDisseminationControlIndicatorAttributeType extends AttributeTypeEnum<CuiLimitedDisseminationControlIndicator.CuiLimitedDisseminationControlIndicatorEnum> {

   /**
    * Description of the attribute's content.
    */

   //@formatter:off
   private static String description =
        "This attribute is used by the following artifact types:\n"
      + "   * CoreArtifactTypes.Controlled:\n"
      + "        to specify the limits for dissemination of the data contained in the controlled artifact.\n"
      + "   * CoreArtifactTypes.DataRightsConfiguration:\n"
      + "        to specify the limits for dissemination of the published data.\n";
   //@formatter:on
   /**
    * The attribute type identifier.
    */

   private static long identifier = 6036586745962781830L;

   /**
    * Short name of the attribute type.
    */

   private static String name = "CUI Limited Dissemination Control";

   /**
    * Creates a new {@link AttributeTypeEnum} {@link AttributeTypeToken} with the {@link NamespaceToken} specified by
    * <code>namespace</code>. The enumeration members are created from the members of the
    * {@link CuiLimitedDisseminationControlIndicator} enumeration.
    *
    * @param the {@link NamespaceToken} to create the {@link AttributeTypeToken} with.
    */

   public CuiLimitedDisseminationControlIndicatorAttributeType(NamespaceToken namespace) {
      //@formatter:off
      super
         (
            CuiLimitedDisseminationControlIndicatorAttributeType.identifier,
            namespace,
            CuiLimitedDisseminationControlIndicatorAttributeType.name,
            MediaType.TEXT_PLAIN,
            CuiLimitedDisseminationControlIndicatorAttributeType.description,
            TaggerTypeToken.PlainTextTagger,
            CuiLimitedDisseminationControlIndicator.values().length
         );
      //@formatter:on
      Stream.of(CuiLimitedDisseminationControlIndicator.values()).map(
         CuiLimitedDisseminationControlIndicator::getEnumToken).forEach(this::addEnum);
   }

   /**
    * Creates a new {@link AttributeTypeToken} with the default {@link NameSpaceToken#OSEE}.
    */

   public CuiLimitedDisseminationControlIndicatorAttributeType() {
      this(NamespaceToken.OSEE);
   }

}

/* EOF */
