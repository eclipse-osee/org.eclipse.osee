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
package org.eclipse.osee.framework.core.widget;

import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * Core (server-safe) provider that resolves the default WidgetId and XOptions for a given attribute type. Does NOT
 * depend on XWidget or any UI classes. Can be used on both client and server.
 * <p>
 * The IDE subclass (AttributeXWidgetProviderIde) extends this to add the XWidget registration cache.
 *
 * @author Donald G. Dunne
 */
public class AttributeXWidgetProviderCore {

   private static AttributeXWidgetProviderCore instance;

   public static AttributeXWidgetProviderCore get() {
      if (instance == null) {
         instance = new AttributeXWidgetProviderCore();
      }
      return instance;
   }

   /**
    * Allow subclass (IDE) to replace the singleton with an extended version.
    */
   protected static void setInstance(AttributeXWidgetProviderCore provider) {
      instance = provider;
   }

   /**
    * Resolves the default WidgetId and XOptions for the given attribute type. Override in subclass to check the XWidget
    * registration cache first.
    */
   public XWidgetData getDefaultXWidgetData(ArtifactTypeToken artType, AttributeTypeToken attributeType) {
      XWidgetData widData = new XWidgetData();
      widData.setArtifactType(artType);
      widData.setAttributeType(attributeType);
      widData.setLabel(attributeType.getUnqualifiedName());

      try {
         // Hook for subclass (IDE) to check registered XWidget first
         XWidgetData registered = resolveFromRegisteredWidget(attributeType);
         if (registered != null) {
            return registered;
         }

         // Resolve to defaults based on attribute type metadata
         widData.setLabel(attributeType.getUnqualifiedName());
         widData.setToolTip(attributeType.getDescription());
         int minOccurrence = artType.getMin(attributeType);
         int maxOccurrence = artType.getMax(attributeType);
         // Attribute is automatically required if types model says there needs to be at least 1
         if (minOccurrence > 0) {
            widData.set(XOption.REQUIRED);
         }
         // Attribute may have zero instances, so it can be cleared (deleted), not just changed
         if (minOccurrence == 0) {
            widData.set(XOption.CLEAR);
         }
         // Do not FILL_HORIZ here, individual widgets upon use in WorkDef / SWt Composite should do that independently
         widData.set(XOption.COLON);

         if (attributeType.notRenderable()) {
            String attributeEditStatus = " (not editable here)";
            widData.setLabel(attributeType.getUnqualifiedName().concat(attributeEditStatus));
            widData.setWidgetId(WidgetId.XXLabelValueWidget);
            return widData;
         }

         Set<DisplayHint> displayHints = attributeType.getDisplayHints();

         // Attempt to get from DisplayHints if type declared which WidgetId
         if (displayHints.contains(DisplayHint.XTextFlat)) {
            widData.setWidgetId(XXFlatAttrsWidget);
         } else if (displayHints.contains(DisplayHint.XArtRef)) {
            widData.setWidgetId(XXArtifactSelWidget);
         } else if (displayHints.contains(DisplayHint.XBranchSel)) {
            widData.setWidgetId(XXBranchSelWidget);
         }

         // Else select widget depending on type
         else if (attributeType.isEnumerated()) {
            widData.setWidgetId(XXStringsWidget);
            if (maxOccurrence == 1) {
               widData.set(XOption.VAL_SINGLE_SEL);
            } else {
               widData.set(XOption.VAL_MULTI_SEL);
            }
         } else if (attributeType.isBoolean()) {
            widData.setWidgetId(XXBooleanWidget);
         } else if (attributeType.isDate()) {
            widData.setWidgetId(XXDateSelWidget);
         } else if (attributeType.isInteger()) {
            widData.setWidgetId(XXIntegerSTextWidget);
         } else if (attributeType.isLong()) {
            widData.setWidgetId(XXLongSTextWidget);
         } else if (attributeType.isDouble()) {
            widData.setWidgetId(XXDoubleSTextWidget);
         } else if (attributeType.isInputStream()) {
            widData.setWidgetId(XXLabelValueWidget);
         } else if (attributeType.isBranchId()) {
            widData.setWidgetId(XXBranchSelWidget);
         } else if (attributeType.isArtifactId()) {
            if (maxOccurrence == 1) {
               widData.setWidgetId(XXArtifactSelWidget);
            } else {
               widData.setWidgetId(XXListArtifactDropWidget);
            }
         } else if (attributeType.isString()) {
            if (attributeType.hasDisplayHint(DisplayHint.NoSpell)) {
               widData.set(XOption.NO_SPELL);
            }
            if (maxOccurrence == 1) {
               if (attributeType.isMultiLine()) {
                  widData.setWidgetId(XXStringSTextHEditWidget);
                  widData.set(XOption.VAL_MULTI_TEXT);
                  widData.set(XOption.FILL_BOTH);
                  widData.set(XOption.BORDER);
                  widData.set(XOption.WRITE);
               } else {
                  widData.setWidgetId(XXStringSTextHEditWidget);
                  widData.set(XOption.COLON);
                  widData.set(XOption.VAL_SINGLE_TEXT);
               }
            } else {
               // Self-labeling widgets draw their own label row, so suppress the base hyperlink/label (NO_LABEL) to
               // avoid a duplicate label rendering above them.
               if (attributeType.isSingleLine()) {
                  widData.setWidgetId(XXFlatAttrsWidget);
                  widData.set(XOption.VAL_SINGLE_TEXT);
                  widData.set(XOption.NO_LABEL);
               } else {
                  widData.setWidgetId(XXStackedWidget);
                  widData.set(XOption.VAL_MULTI_TEXT);
                  widData.set(XOption.NO_LABEL);
               }
            }
         } else {
            widData.setWidgetId(XXStackedWidget);
            widData.set(XOption.NO_LABEL);
         }
      } catch (OseeCoreException ex) {
         widData.setWidgetId(XXTextWidget);
         StringBuilder builder = new StringBuilder();
         builder.append("Unable to determine base type for attribute type");
         builder.append(String.format("[%s]", attributeType));
         builder.append(Lib.exceptionToString(ex));
         widData.setDefaultValue(builder.toString());
      }

      return widData;
   }

   /**
    * Hook for subclass to check registered XWidget cache. Returns null if no registered widget found (falls through to
    * default resolution).
    */
   protected XWidgetData resolveFromRegisteredWidget(AttributeTypeToken attributeType) {
      return null;
   }

}
