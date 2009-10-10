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
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.ote.define.TestRunField;
import org.eclipse.osee.ote.define.AUTOGEN.OteAttributes;

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

      outfileFieldToAttributeMap.put(TestRunField.USER_ID, CoreAttributes.USER_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_REVISION, OteAttributes.REVISION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_MODIFIED_FLAG, OteAttributes.MODIFIED_FLAG.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_AUTHOR, OteAttributes.LAST_AUTHOR.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_MODIFIED, OteAttributes.LAST_MODIFIED_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_URL, OteAttributes.TEST_SCRIPT_URL.getName());

      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_ARCH, OteAttributes.OS_ARCHITECTURE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_NAME, OteAttributes.OS_NAME.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_VERSION, OteAttributes.OS_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_VERSION, OteAttributes.OSEE_VERSION.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_TITLE, OteAttributes.OSEE_SERVER_TITLE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS,
            OteAttributes.OSEE_SERVER_JAR_VERSION.getName());

      outfileFieldToAttributeMap.put(TestRunField.PROCESSOR_ID, OteAttributes.PROCESSOR_ID.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_START_DATE, OteAttributes.START_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_END_DATE, OteAttributes.END_DATE.getName());
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_ELAPSED_TIME, OteAttributes.ELAPSED_DATE.getName());

      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_PASSED, OteAttributes.PASSED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_FAILED, OteAttributes.FAILED.getName());
      outfileFieldToAttributeMap.put(TestRunField.TOTAL_TEST_POINTS, OteAttributes.TOTAL_TEST_POINTS.getName());
      outfileFieldToAttributeMap.put(TestRunField.TEST_ABORT_STATUS, OteAttributes.SCRIPT_ABORTED.getName());

      outfileFieldToAttributeMap.put(TestRunField.QUALIFICATION_LEVEL, OteAttributes.QUALIFICATION_LEVEL.getName());

      outfileFieldToAttributeMap.put(TestRunField.BUILD_ID, OteAttributes.BUILD_ID.getName());

      outfileFieldToAttributeMap.put(TestRunField.IS_BATCH_MODE_ALLOWED, OteAttributes.IS_BATCH_MODE_ALLOWED.getName());
      outfileFieldToAttributeMap.put(TestRunField.RAN_IN_BATCH_MODE, OteAttributes.RAN_IN_BATCH_MODE.getName());

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
      if (attribute.equals(OteAttributes.LAST_MODIFIED_DATE.getName())) {
         toReturn = lastModifiedFormat;
      }
      return toReturn;
   }

   private boolean isDate(String attribute) {
      return attribute.equals(OteAttributes.LAST_MODIFIED_DATE.getName()) || attribute.equals(OteAttributes.START_DATE.getName()) || attribute.equals(OteAttributes.END_DATE.getName());
   }

   private boolean isInteger(String attribute) {
      return attribute.equals(OteAttributes.TOTAL_TEST_POINTS.getName()) || attribute.equals(OteAttributes.PASSED.getName()) || attribute.equals(OteAttributes.FAILED.getName());
   }

   private boolean isBoolean(String attribute) {
      return attribute.equals(OteAttributes.SCRIPT_ABORTED.getName()) || attribute.equals(OteAttributes.RAN_IN_BATCH_MODE.getName()) || attribute.equals(OteAttributes.IS_BATCH_MODE_ALLOWED.getName());
   }

}
