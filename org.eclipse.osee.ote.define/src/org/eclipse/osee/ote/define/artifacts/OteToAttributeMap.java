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
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.define.TestRunField;
import org.eclipse.osee.ote.define.AUTOGEN.OTE_SKYNET_ATTRIBUTES;

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

      outfileFieldToAttributeMap.put(TestRunField.USER_ID, OTE_SKYNET_ATTRIBUTES.USER_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_REVISION, OTE_SKYNET_ATTRIBUTES.REVISION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_MODIFIED_FLAG, OTE_SKYNET_ATTRIBUTES.MODIFIED_FLAG.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_AUTHOR, OTE_SKYNET_ATTRIBUTES.LAST_AUTHOR.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_MODIFIED,
            OTE_SKYNET_ATTRIBUTES.LAST_MODIFIED_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_URL, OTE_SKYNET_ATTRIBUTES.TEST_SCRIPT_URL.getName());

      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_ARCH, OTE_SKYNET_ATTRIBUTES.OS_ARCHITECTURE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_NAME, OTE_SKYNET_ATTRIBUTES.OS_NAME.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_VERSION, OTE_SKYNET_ATTRIBUTES.OS_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_VERSION, OTE_SKYNET_ATTRIBUTES.OSEE_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_TITLE,
            OTE_SKYNET_ATTRIBUTES.OSEE_SERVER_TITLE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS,
            OTE_SKYNET_ATTRIBUTES.OSEE_SERVER_JAR_VERSION.getName());

      outfileFieldToAttributeMap.put(TestRunField.PROCESSOR_ID, OTE_SKYNET_ATTRIBUTES.PROCESSOR_ID.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_START_DATE, OTE_SKYNET_ATTRIBUTES.START_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_END_DATE, OTE_SKYNET_ATTRIBUTES.END_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_ELAPSED_TIME, OTE_SKYNET_ATTRIBUTES.ELAPSED_DATE.getName());

      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_PASSED, OTE_SKYNET_ATTRIBUTES.PASSED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_FAILED, OTE_SKYNET_ATTRIBUTES.FAILED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TOTAL_TEST_POINTS, OTE_SKYNET_ATTRIBUTES.TOTAL_TEST_POINTS.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_ABORT_STATUS, OTE_SKYNET_ATTRIBUTES.SCRIPT_ABORTED.getName());

      outfileFieldToAttributeMap.put(TestRunField.QUALIFICATION_LEVEL,
            OTE_SKYNET_ATTRIBUTES.QUALIFICATION_LEVEL.getName());

      outfileFieldToAttributeMap.put(TestRunField.BUILD_ID, OTE_SKYNET_ATTRIBUTES.BUILD_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.IS_BATCH_MODE_ALLOWED,
            OTE_SKYNET_ATTRIBUTES.IS_BATCH_MODE_ALLOWED.getName());
      outfileFieldToAttributeMap.put(TestRunField.RAN_IN_BATCH_MODE, OTE_SKYNET_ATTRIBUTES.RAN_IN_BATCH_MODE.getName());

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
      if (attribute.equals(OTE_SKYNET_ATTRIBUTES.LAST_MODIFIED_DATE.getName())) {
         toReturn = lastModifiedFormat;
      }
      return toReturn;
   }

   private boolean isDate(String attribute) {
      return attribute.equals(OTE_SKYNET_ATTRIBUTES.LAST_MODIFIED_DATE.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.START_DATE.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.END_DATE.getName());
   }

   private boolean isInteger(String attribute) {
      return attribute.equals(OTE_SKYNET_ATTRIBUTES.TOTAL_TEST_POINTS.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.PASSED.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.FAILED.getName());
   }

   private boolean isBoolean(String attribute) {
      return attribute.equals(OTE_SKYNET_ATTRIBUTES.SCRIPT_ABORTED.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.RAN_IN_BATCH_MODE.getName()) || attribute.equals(OTE_SKYNET_ATTRIBUTES.IS_BATCH_MODE_ALLOWED.getName());
   }

}
