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

package org.eclipse.osee.define.rest.internal;

/**
 * @author Jaden W. Puckett
 */
public final class SafetyReportConstants {
   public static final String TRACE_PRIMARY_REGEX = "\\^SRS\\s*([^;]+);?";
   public static final String TRACE_SECONDARY_REGEX = "\\[?(\\{[^\\}]+\\})(.*)";
   public static final String SOURCE_FILE_REGEX = ".*\\.(java|ada|ads|adb|c|h)";

   public static TraceMatch newTraceMatch() {
      return new TraceMatch(TRACE_PRIMARY_REGEX, TRACE_SECONDARY_REGEX);
   }

   public static TraceAccumulator newTraceAccumulator() {
      return new TraceAccumulator(SOURCE_FILE_REGEX, newTraceMatch());
   }

   private SafetyReportConstants() {
   }
}
