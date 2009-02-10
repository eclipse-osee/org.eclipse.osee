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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public class AttributeElement {
   private static final Pattern internalAttributeElementsPattern =
         Pattern.compile("<((\\w+:)?(Label|Outline|Name|Format|Editable))>(.*?)</\\1>",
               Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
   private String outlineNumber;
   private String label;
   private String attributeName;
   private String format;

   public AttributeElement(String element) {
      Matcher matcher = internalAttributeElementsPattern.matcher(element);

      this.outlineNumber = "";
      this.label = "";
      this.attributeName = "";
      this.format = "";

      while (matcher.find()) {
         String elementType = matcher.group(3);
         String value = matcher.group(4).trim();
         if (elementType.equals("Outline")) {
            value = WordUtil.textOnly(value);
            if (value.length() > 0) {
               outlineNumber = value;
            } else {
               outlineNumber = "1.0";
            }
         } else if (elementType.equals("Label")) {
            label = value;
         } else if (elementType.equals("Name")) {
            attributeName = WordUtil.textOnly(value);
         } else if (elementType.equals("Format")) {
            format = value;
         } else {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Unexpected element read in Attribute:" + elementType);
         }
      }
   }

   public String getAttributeName() {
      return attributeName;
   }

   public String getFormat() {
      return format;
   }

   public String getLabel() {
      return label;
   }

   public String getOutlineNumber() {
      return outlineNumber;
   }
}
