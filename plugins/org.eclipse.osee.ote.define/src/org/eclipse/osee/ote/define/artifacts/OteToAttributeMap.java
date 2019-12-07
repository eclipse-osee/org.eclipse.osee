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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.ote.define.OteAttributeTypes;
import org.eclipse.osee.ote.define.TestRunField;

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

      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_REVISION, OteAttributeTypes.Revision);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_MODIFIED_FLAG, OteAttributeTypes.ModifiedFlag);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_AUTHOR, OteAttributeTypes.LastAuthor);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_LAST_MODIFIED, OteAttributeTypes.LastModifiedDate);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_URL, OteAttributeTypes.TestScriptUrl);

      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_ARCH, OteAttributeTypes.OsArchitecture);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_NAME, OteAttributeTypes.OsName);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OS_VERSION, OteAttributeTypes.OsVersion);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_VERSION, OteAttributeTypes.OseeVersion);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_TITLE, OteAttributeTypes.OseeServerTitle);
      outfileFieldToAttributeMap.put(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS,
         OteAttributeTypes.OseeServerJarVersion);

      outfileFieldToAttributeMap.put(TestRunField.PROCESSOR_ID, OteAttributeTypes.ProcessorId);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_START_DATE, OteAttributeTypes.StartDate);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_END_DATE, OteAttributeTypes.EndDate);
      outfileFieldToAttributeMap.put(TestRunField.SCRIPT_ELAPSED_TIME, OteAttributeTypes.ElapsedDate);

      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_PASSED, OteAttributeTypes.Passed);
      outfileFieldToAttributeMap.put(TestRunField.TEST_POINTS_FAILED, OteAttributeTypes.Failed);
      outfileFieldToAttributeMap.put(TestRunField.TOTAL_TEST_POINTS, OteAttributeTypes.TotalTestPoints);
      outfileFieldToAttributeMap.put(TestRunField.TEST_ABORT_STATUS, OteAttributeTypes.ScriptAborted);

      outfileFieldToAttributeMap.put(TestRunField.QUALIFICATION_LEVEL, OteAttributeTypes.QualificationLevel);

      outfileFieldToAttributeMap.put(TestRunField.BUILD_ID, OteAttributeTypes.BuildId);

      outfileFieldToAttributeMap.put(TestRunField.IS_BATCH_MODE_ALLOWED, OteAttributeTypes.IsBatchModeAllowed);
      outfileFieldToAttributeMap.put(TestRunField.RAN_IN_BATCH_MODE, OteAttributeTypes.RanInBatchMode);

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
      if (attributeType.equals(OteAttributeTypes.LastModifiedDate)) {
         return lastModifiedFormat;
      }
      return scriptStartEndDataFormat;
   }

   private boolean isDate(AttributeTypeId attributeType) {
      return attributeType.matches(OteAttributeTypes.LastModifiedDate, OteAttributeTypes.StartDate,
         OteAttributeTypes.EndDate);
   }

   private boolean isInteger(AttributeTypeId attributeType) {
      return attributeType.matches(OteAttributeTypes.TotalTestPoints, OteAttributeTypes.Passed,
         OteAttributeTypes.Failed);
   }

   private boolean isBoolean(AttributeTypeId attributeType) {
      return attributeType.matches(OteAttributeTypes.ScriptAborted, OteAttributeTypes.RanInBatchMode,
         OteAttributeTypes.IsBatchModeAllowed);
   }
}