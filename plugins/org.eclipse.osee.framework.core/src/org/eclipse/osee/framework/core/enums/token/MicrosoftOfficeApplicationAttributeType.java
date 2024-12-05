/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.MicrosoftOfficeApplicationEnum;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.TaggerTypeToken;
import org.eclipse.osee.framework.core.enums.EnumToken;
import org.eclipse.osee.framework.core.enums.token.MicrosoftOfficeApplicationAttributeType.MicrosoftOfficeApplicationAttributeTypeEnum;

/**
 * @author Jaden W. Puckett
 */
public class MicrosoftOfficeApplicationAttributeType extends AttributeTypeEnum<MicrosoftOfficeApplicationAttributeTypeEnum> {

   // @formatter:off

   public final MicrosoftOfficeApplicationAttributeTypeEnum ExcelSpreadsheet = new MicrosoftOfficeApplicationAttributeTypeEnum(0, MicrosoftOfficeApplicationEnum.EXCEL_SPREADSHEET.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum WordDocument = new MicrosoftOfficeApplicationAttributeTypeEnum(1, MicrosoftOfficeApplicationEnum.WORD_DOCUMENT.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum PowerPointShow = new MicrosoftOfficeApplicationAttributeTypeEnum(2, MicrosoftOfficeApplicationEnum.POWERPOINT_SHOW.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum VisioDrawing = new MicrosoftOfficeApplicationAttributeTypeEnum(3, MicrosoftOfficeApplicationEnum.VISIO_DRAWING.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum PublisherDocument = new MicrosoftOfficeApplicationAttributeTypeEnum(4, MicrosoftOfficeApplicationEnum.PUBLISHER_DOCUMENT.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum AccessDatabase = new MicrosoftOfficeApplicationAttributeTypeEnum(5, MicrosoftOfficeApplicationEnum.ACCESS_DATABASE.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum InfoPathForm = new MicrosoftOfficeApplicationAttributeTypeEnum(6, MicrosoftOfficeApplicationEnum.INFOPATH_FORM.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum ProjectFile = new MicrosoftOfficeApplicationAttributeTypeEnum(7, MicrosoftOfficeApplicationEnum.PROJECT_FILE.getApplicationName());
   public final MicrosoftOfficeApplicationAttributeTypeEnum ExcelChart = new MicrosoftOfficeApplicationAttributeTypeEnum(8, MicrosoftOfficeApplicationEnum.EXCEL_CHART.getApplicationName());

   // @formatter:on

   public MicrosoftOfficeApplicationAttributeType(NamespaceToken namespace, int enumCount) {
      super(4175315953481922437L, namespace, "Microsoft Office Application", MediaType.TEXT_PLAIN, "",
         TaggerTypeToken.PlainTextTagger, enumCount);
   }

   public MicrosoftOfficeApplicationAttributeType() {
      this(NamespaceToken.OSEE, 9);
   }

   public class MicrosoftOfficeApplicationAttributeTypeEnum extends EnumToken {
      public MicrosoftOfficeApplicationAttributeTypeEnum(int ordinal, String name) {
         super(ordinal, name);
         addEnum(this);
      }
   }
}
