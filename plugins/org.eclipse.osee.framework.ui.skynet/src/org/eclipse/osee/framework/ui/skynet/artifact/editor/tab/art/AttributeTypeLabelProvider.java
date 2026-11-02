/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.tab.art;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Displays attribute types as "UnqualifiedName (namespace)" and sorts by unqualified name. For example,
 * "ats.Baseline Branch Id" is displayed as "Baseline Branch Id (ats)". Types without a namespace dot show as-is.
 *
 * @author Donald G. Dunne
 */
public class AttributeTypeLabelProvider extends LabelProvider {

   @Override
   public String getText(Object element) {
      return getDisplayName(element);
   }

   /**
    * Returns display name as "UnqualifiedName (namespace)" or just the name if no namespace dot.
    */
   public static String getDisplayName(Object element) {
      if (element instanceof AttributeTypeToken) {
         AttributeTypeToken attrType = (AttributeTypeToken) element;
         String name = attrType.getName();
         int dotIndex = name.indexOf('.');
         if (dotIndex > 0) {
            String namespace = name.substring(0, dotIndex);
            String unqualified = name.substring(dotIndex + 1);
            return unqualified + " (" + namespace + ")";
         }
         return name;
      }
      return String.valueOf(element);
   }

   /**
    * ViewerComparator that sorts by the display name (unqualified name).
    */
   public static class Comparator extends ViewerComparator {
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         return getDisplayName(e1).compareToIgnoreCase(getDisplayName(e2));
      }
   }
}
