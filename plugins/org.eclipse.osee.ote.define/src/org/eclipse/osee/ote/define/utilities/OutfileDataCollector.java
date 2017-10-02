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
package org.eclipse.osee.ote.define.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.ote.define.OteDefinePlugin;
import org.eclipse.osee.ote.define.TestRunField;
import org.eclipse.osee.ote.define.artifacts.OteToAttributeMap;
import org.eclipse.osee.ote.define.parser.IDataListener;

/**
 * @author Roberto E. Escobar
 */
public class OutfileDataCollector implements IDataListener {
   protected static final OteToAttributeMap oteToAttributeMap = OteToAttributeMap.getInstance();

   protected final Map<String, String> collectedData;

   public OutfileDataCollector() {
      this.collectedData = new HashMap<>();
   }

   @Override
   public void notifyDataEvent(String name, String value) {
      collectedData.put(name, value);
   }

   public void populate(Artifact artifact, Artifact parent)  {
      Conditions.checkNotNull(artifact, "artifact");
      Conditions.checkNotNull(parent, "parent");
      for (String fieldName : collectedData.keySet()) {
         AttributeTypeId attributeType = oteToAttributeMap.getAttributeType(fieldName);
         if (attributeType != null && artifact.isAttributeTypeValid(attributeType)) {
            try {
               String value = collectedData.get(fieldName);
               Object object = oteToAttributeMap.asTypedObject(attributeType, value);
               artifact.setSoleAttributeValue(attributeType, object);
            } catch (Exception ex) {
               OseeLog.log(OteDefinePlugin.class, Level.SEVERE, ex);
            }
         }
      }
      artifact.setName(getField(TestRunField.SCRIPT_NAME.name()));
      parent.addChild(artifact);
   }

   public String getField(String name) {
      String toReturn = collectedData.get(name);
      return Strings.isValid(toReturn) ? toReturn : "";
   }
}
