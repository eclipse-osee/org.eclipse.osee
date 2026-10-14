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

package org.eclipse.osee.framework.core.widget.validate;

import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultData.Type;

/**
 * @author Donald G. Dunne
 */
public class XWidgetStatus {

   public static XResultData OK = new XResultData("OK");
   public static XResultData NOT_VALID_STRING = new XResultData(Type.Severe, "Entered must be a valid String");
   public static XResultData NOT_INTEGER = new XResultData(Type.Severe, "Entered is not an Integer");
   public static XResultData NOT_LONG = new XResultData(Type.Severe, "Entered is not a Long");
   public static XResultData NOT_DOUBLE = new XResultData(Type.Severe, "Entered is not an Double");
   public static XResultData NOT_VALID_DOUBLE = new XResultData(Type.Severe, "Entered is not a valid Double");
   public static XResultData NOT_PERCENT = new XResultData("Entered is not a percent; 0..100");
   public static XResultData UNHANDLED_OBJECT_TYPE = new XResultData("Unhandled Object Type");

   public XWidgetStatus(XResultData.Type type, String message, Object... data) {
      new XResultData(type, message, data);
   }

}
