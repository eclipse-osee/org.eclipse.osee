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
package org.eclipse.osee.framework.ui.skynet.util;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public class SkynetViews {
   /**
    * @param memento
    * @return Returns a collection of <code>DynamicAttributeDescriptor</code> stored in a memento.
    * @throws SQLException
    */
   public static List<AttributeType> loadAttrTypesFromPreferenceStore(String preferenceKey, Branch branch) throws SQLException {
      List<AttributeType> attributeDescriptors = new LinkedList<AttributeType>();
      Collection<AttributeType> descriptors = AttributeTypeManager.getTypes(branch);

      IPreferenceStore preferenceStore = SkynetGuiPlugin.getInstance().getPreferenceStore();
      for (String attributeType : preferenceStore.getString(preferenceKey).split("\\|")) {
         for (AttributeType descriptor : descriptors) {
            if (attributeType.equals(descriptor.getName())) {
               attributeDescriptors.add(descriptor);
               break;
            }
         }
      }

      return attributeDescriptors;
   }
}