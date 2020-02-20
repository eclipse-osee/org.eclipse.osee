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
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Theron Virgin
 */
public class EmbededAttributeEditorFactory {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$";
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$";

   public static IEmbeddedAttributeEditor getEmbeddedEditor(AttributeTypeGeneric<?> attributeType, String displayName, final Collection<?> attributeHolder, boolean persist) {
      try {
         if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
            return new EmbeddedDateAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
            return new EmbeddedStringAttributeEditor(VALID_FLOAT_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
            return new EmbeddedStringAttributeEditor(VALID_INTEGER_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
            return new EmbeddedStringAttributeEditor(VALID_INTEGER_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
            return new EmbeddedBooleanAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType)) {
            return new EmbeddedEnumAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeType)) {
            return new EmbeddedStringAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else {
            AWorkbench.popup("ERROR", "Unhandled attribute type.  No editor defined for this type");
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;

   }
}
