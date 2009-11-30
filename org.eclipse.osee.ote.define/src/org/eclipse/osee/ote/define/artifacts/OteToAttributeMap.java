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
package org.eclipse.osee.ote.define.artifacts;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.define.TestRunField;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes;

/**
 * @author Roberto E. Escobar
 */
public class OteToAttributeMap {
   private static final SimpleDateFormat scriptStartEndDataFormat = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
   private static final SimpleDateFormat lastModifiedFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");

   private static OteToAttributeMap instance = null;
   private static Map<TestRunField, String> outfileFieldToAttributeMap;

   private OteToAttributeMap() {
      outfileFieldToAttributeMap = new HashMap<TestRunField, String>();

      outfileFieldToAttributeMap.put(TestRunField.USER_ID, CoreAttributeTypes.USER_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_REVISION, OteAttributeTypes.REVISION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_MODIFIED_FLAG, OteAttributeTypes.MODIFIED_FLAG.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_AUTHOR, OteAttributeTypes.LAST_AUTHOR.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_MODIFIED, OteAttributeTypes.LAST_MODIFIED_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_URL, OteAttributeTypes.TEST_SCRIPT_URL.getName());

      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_ARCH, OteAttributeTypes.OS_ARCHITECTURE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_NAME, OteAttributeTypes.OS_NAME.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_VERSION, OteAttributeTypes.OS_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_VERSION, OteAttributeTypes.OSEE_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_TITLE, OteAttributeTypes.OSEE_SERVER_TITLE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS,
            OteAttributeTypes.OSEE_SERVER_JAR_VERSION.getName());

      outfileFieldToAttributeMap.put(TestRunField.PROCESSOR_ID, OteAttributeTypes.PROCESSOR_ID.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_START_DATE, OteAttributeTypes.START_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_END_DATE, OteAttributeTypes.END_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_ELAPSED_TIME, OteAttributeTypes.ELAPSED_DATE.getName());

      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_PASSED, OteAttributeTypes.PASSED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_FAILED, OteAttributeTypes.FAILED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TOTAL_TEST_POINTS, OteAttributeTypes.TOTAL_TEST_POINTS.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_ABORT_STATUS, OteAttributeTypes.SCRIPT_ABORTED.getName());

      outfileFieldToAttributeMap.put(TestRunField.QUALIFICATION_LEVEL, OteAttributeTypes.QUALIFICATION_LEVEL.getName());

      outfileFieldToAttributeMap.put(TestRunField.BUILD_ID, OteAttributeTypes.BUILD_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.IS_BATCH_MODE_ALLOWED, OteAttributeTypes.IS_BATCH_MODE_ALLOWED.getName());
      outfileFieldToAttributeMap.put(TestRunField.RAN_IN_BATCH_MODE, OteAttributeTypes.RAN_IN_BATCH_MODE.getName());

      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_TIME,
      // OTE_SKYNET_ATTRIBUTES.EgetName());
      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_RESULTS,
      // OTE_SKYNET_ATTRIBUTES.getName());
      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_ERRORS,
      // OTE_SKYNET_ATTRIBUTES.getName());
   }

   public static OteToAttributeMap getInstance() {
      if (instance == null) {
         instance = new OteToAttributeMap();
      }
      return instance;
   }

   public String getAttributeName(String rawName) {
      TestRunField field = getFieldId(rawName);
      return outfileFieldToAttributeMap.get(field);
   }

   private TestRunField getFieldId(String name) {
      TestRunField field = TestRunField.INVALID;
      try {
         field = TestRunField.valueOf(name);
      } catch (Exception ex) {
         field = TestRunField.INVALID;
      }
      return field;
   }

   public Object asTypedObject(String attribute, String value) throws Exception {
      Object toReturn = null;
      if (isDate(attribute) != false) {
         toReturn = getFormat(attribute).parse(value);
      } else if (isInteger(attribute) != false) {
         if (Strings.isValid(value) != true) {
            value = "0";
         }
         toReturn = new Integer(value);
      } else if (isBoolean(attribute) != false) {
         if (Strings.isValid(value) != true) {
            value = "false";
         }
         toReturn = new Boolean(value);
      } else {
         toReturn = value;
      }
      return toReturn;
   }

   private SimpleDateFormat getFormat(String attribute) {
      SimpleDateFormat toReturn = scriptStartEndDataFormat;
      if (attribute.equals(OteAttributeTypes.LAST_MODIFIED_DATE.getName())) {
         toReturn = lastModifiedFormat;
      }
      return toReturn;
   }

   private boolean isDate(String attribute) {
      return attribute.equals(OteAttributeTypes.LAST_MODIFIED_DATE.getName()) || attribute.equals(OteAttributeTypes.START_DATE.getName()) || attribute.equals(OteAttributeTypes.END_DATE.getName());
   }

   private boolean isInteger(String attribute) {
      return attribute.equals(OteAttributeTypes.TOTAL_TEST_POINTS.getName()) || attribute.equals(OteAttributeTypes.PASSED.getName()) || attribute.equals(OteAttributeTypes.FAILED.getName());
   }

   private boolean isBoolean(String attribute) {
      return attribute.equals(OteAttributeTypes.SCRIPT_ABORTED.getName()) || attribute.equals(OteAttributeTypes.RAN_IN_BATCH_MODE.getName()) || attribute.equals(OteAttributeTypes.IS_BATCH_MODE_ALLOWED.getName());
   }

}
