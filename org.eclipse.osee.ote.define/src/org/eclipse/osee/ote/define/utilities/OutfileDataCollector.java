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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
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
   private static final OteToAttributeMap oteToAttributeMap = OteToAttributeMap.getInstance();

   private Map<String, String> collectedData;

   public OutfileDataCollector() {
      this.collectedData = new HashMap<String, String>();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.ui.define.parser.IDataListener#notifyDataEvent(java.lang.String,
    *      java.lang.String)
    */
   public void notifyDataEvent(String name, String value) {
      collectedData.put(name, value);
   }

   public void populate(Artifact artifact) throws OseeCoreException {
      checkForNull(artifact);
      for (String fieldName : collectedData.keySet()) {
         String attribute = oteToAttributeMap.getAttributeName(fieldName);
         if (Strings.isValid(attribute) && artifact.isAttributeTypeValid(attribute)) {
            try {
               String value = collectedData.get(fieldName);
               Object object = oteToAttributeMap.asTypedObject(attribute, value);
               artifact.setSoleAttributeValue(attribute, object);
            } catch (Exception ex) {
               OseeLog.log(OteDefinePlugin.class, Level.SEVERE, ex);
            }
         }
      }
      artifact.setDescriptiveName(getField(TestRunField.SCRIPT_NAME.name()));
   }

   public String getField(String name) {
      String toReturn = collectedData.get(name);
      return Strings.isValid(toReturn) ? toReturn : "";
   }

   private void checkForNull(Object object) {
      if (object == null) {
         throw new IllegalArgumentException(String.format("Argument was null [%s]", object.getClass().getName()));
      }
   }
}
