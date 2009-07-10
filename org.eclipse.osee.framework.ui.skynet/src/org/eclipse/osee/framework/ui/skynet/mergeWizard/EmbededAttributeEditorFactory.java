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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.Collection;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Theron Virgin
 */
public class EmbededAttributeEditorFactory {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$";
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$";
   private final static String VALID_PERCENT_REG_EX =
         "^(0*100{1,1}\\.?((?<=\\.)0*)?%?$)|(^0*\\d{0,2}\\.?((?<=\\.)\\d*)?%?)$";

   public static IEmbeddedAttributeEditor getEmbeddedEditor(String attributeName, String displayName, final Collection<?> attributeHolder, boolean persist) {
      try {
         Class<? extends Attribute> attClass;
         attClass = AttributeTypeManager.getType(attributeName).getBaseAttributeClass();

         if (attClass.equals(DateAttribute.class)) {
            return new EmbeddedDateAttributeEditor(null, attributeHolder, displayName, attributeName, persist);
         } else if (attClass.equals(FloatingPointAttribute.class)) {
            return new EmbeddedStringAttributeEditor(VALID_FLOAT_REG_EX, attributeHolder, displayName, attributeName,
                  persist);
         } else if (attClass.equals(IntegerAttribute.class)) {
            return new EmbeddedStringAttributeEditor(VALID_INTEGER_REG_EX, attributeHolder, displayName, attributeName,
                  persist);
         } else if (attClass.equals(BooleanAttribute.class)) {
            return new EmbeddedBooleanAttributeEditor(null, attributeHolder, displayName, attributeName, persist);
         } else if (attClass.equals(EnumeratedAttribute.class)) {
            return new EmbeddedEnumAttributeEditor(null, attributeHolder, displayName, attributeName, persist);
         } else if (attClass.equals(StringAttribute.class)) {
            return new EmbeddedStringAttributeEditor(null, attributeHolder, displayName, attributeName, persist);
         } else
            AWorkbench.popup("ERROR", "Unhandled attribute type.  No editor defined for this type");
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;

   }
}
