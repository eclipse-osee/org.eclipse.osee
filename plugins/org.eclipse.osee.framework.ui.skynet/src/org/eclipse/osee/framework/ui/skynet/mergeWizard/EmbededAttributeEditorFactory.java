/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
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
         if (attributeType.isDate()) {
            return new EmbeddedDateAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (attributeType.isDouble()) {
            return new EmbeddedStringAttributeEditor(VALID_FLOAT_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (attributeType.isInteger()) {
            return new EmbeddedStringAttributeEditor(VALID_INTEGER_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (attributeType.isLong()) {
            return new EmbeddedStringAttributeEditor(VALID_INTEGER_REG_EX, attributeHolder, displayName, attributeType,
               persist);
         } else if (attributeType.isBoolean()) {
            return new EmbeddedBooleanAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (attributeType.isEnumerated()) {
            return new EmbeddedEnumAttributeEditor(null, attributeHolder, displayName, attributeType, persist);
         } else if (attributeType.isString()) {
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
