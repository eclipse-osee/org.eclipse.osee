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

import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.END_DATE;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.FAILED;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.IS_BATCH_MODE_ALLOWED;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.LAST_MODIFIED_DATE;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.PASSED;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.RAN_IN_BATCH_MODE;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.SCRIPT_ABORTED;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.START_DATE;
import static org.eclipse.osee.ote.define.AUTOGEN.OteAttributeTypes.TOTAL_TEST_POINTS;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
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
   private static Map<TestRunField, AttributeTypeId> outfileFieldToAttributeMap;

   private OteToAttributeMap() {
      outfileFieldToAttributeMap = new HashMap<>();

      outfileFieldToAttributeMap.put(TestRunField.USER_ID, CoreAttributeTypes.UserId);

      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_REVISION, OteAttributeTypes.REVISION);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_MODIFIED_FLAG, OteAttributeTypes.MODIFIED_FLAG);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_AUTHOR, OteAttributeTypes.LAST_AUTHOR);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_MODIFIED, OteAttributeTypes.LAST_MODIFIED_DATE);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_URL, OteAttributeTypes.TEST_SCRIPT_URL);

      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_ARCH, OteAttributeTypes.OS_ARCHITECTURE);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_NAME, OteAttributeTypes.OS_NAME);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_VERSION, OteAttributeTypes.OS_VERSION);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_VERSION, OteAttributeTypes.OSEE_VERSION);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_TITLE, OteAttributeTypes.OSEE_SERVER_TITLE);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS,
         OteAttributeTypes.OSEE_SERVER_JAR_VERSION);

      outfileFieldToAttributeMap.put(TestRunField.PROCESSOR_ID, OteAttributeTypes.PROCESSOR_ID);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_START_DATE, OteAttributeTypes.START_DATE);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_END_DATE, OteAttributeTypes.END_DATE);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_ELAPSED_TIME, OteAttributeTypes.ELAPSED_DATE);

      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_PASSED, OteAttributeTypes.PASSED);
      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_FAILED, OteAttributeTypes.FAILED);
      outfileFieldToAttributeMap.put(TestRunField.TOTAL_TEST_POINTS, OteAttributeTypes.TOTAL_TEST_POINTS);
      outfileFieldToAttributeMap.put(TestRunField.TEST_ABORT_STATUS, OteAttributeTypes.SCRIPT_ABORTED);

      outfileFieldToAttributeMap.put(TestRunField.QUALIFICATION_LEVEL, OteAttributeTypes.QUALIFICATION_LEVEL);

      outfileFieldToAttributeMap.put(TestRunField.BUILD_ID, OteAttributeTypes.BUILD_ID);

      outfileFieldToAttributeMap.put(TestRunField.IS_BATCH_MODE_ALLOWED, OteAttributeTypes.IS_BATCH_MODE_ALLOWED);
      outfileFieldToAttributeMap.put(TestRunField.RAN_IN_BATCH_MODE, OteAttributeTypes.RAN_IN_BATCH_MODE);

      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_TIME,
      // OTE_SKYNET_ATTRIBUTES.EgetName());
      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_RESULTS,
      // OTE_SKYNET_ATTRIBUTES);
      // outfileFieldToAttributeMap.put(TestRunField.SCRIPT_EXECUTION_ERRORS,
      // OTE_SKYNET_ATTRIBUTES);
   }

   public static OteToAttributeMap getInstance() {
      if (instance == null) {
         instance = new OteToAttributeMap();
      }
      return instance;
   }

   public AttributeTypeId getAttributeType(String rawName) {
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

   public Object asTypedObject(AttributeTypeId attributeType, String value) throws Exception {
      Object toReturn = null;
      if (isDate(attributeType)) {
         toReturn = getFormat(attributeType).parse(value);
      } else if (isInteger(attributeType) != false) {
         if (Strings.isValid(value) != true) {
            value = "0";
         }
         toReturn = new Integer(value);
      } else if (isBoolean(attributeType) != false) {
         if (Strings.isValid(value) != true) {
            value = "false";
         }
         toReturn = new Boolean(value);
      } else {
         toReturn = value;
      }
      return toReturn;
   }

   private SimpleDateFormat getFormat(AttributeTypeId attributeType) {
      if (attributeType.equals(LAST_MODIFIED_DATE)) {
         return lastModifiedFormat;
      }
      return scriptStartEndDataFormat;
   }

   private boolean isDate(AttributeTypeId attributeType) {
      return attributeType.matches(LAST_MODIFIED_DATE, START_DATE, END_DATE);
   }

   private boolean isInteger(AttributeTypeId attributeType) {
      return attributeType.matches(TOTAL_TEST_POINTS, PASSED, FAILED);
   }

   private boolean isBoolean(AttributeTypeId attributeType) {
      return attributeType.matches(SCRIPT_ABORTED, RAN_IN_BATCH_MODE, IS_BATCH_MODE_ALLOWED);
   }
}