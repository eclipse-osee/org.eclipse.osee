/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability;

import java.io.StringWriter;
import java.io.Writer;
import org.eclipse.osee.define.traceability.report.ISimpleTable;
import org.eclipse.osee.define.traceability.report.StdCsciToTestTable;
import org.eclipse.osee.define.traceability.report.StdTestToCsciTable;
import org.eclipse.osee.define.traceability.report.StpCsciToTestTable;
import org.eclipse.osee.define.traceability.report.StpTestToCsciTable;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityFactory {

   public enum TraceabilityStyle {
      STP_Test_to_CSCI,
      STP_CSCI_to_Test,
      STD_Test_to_CSCI,
      STD_CSCI_to_Test;

      @Override
      public String toString() {
         return name().toUpperCase();
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }
   }

   public enum OutputFormat {
      EXCEL;
   }

   protected static ISheetWriter getSheetWriter(OutputFormat outputFormat, Writer writer) throws Exception {
      ISheetWriter toReturn = null;
      switch (outputFormat) {
         case EXCEL:
         default:
            toReturn = new ExcelXmlWriter(writer);
            break;
      }
      return toReturn;
   }

   public static TraceabilityTable getTraceabilityTable(OutputFormat outputFormat, TraceabilityStyle style, RequirementTraceabilityData sourceData) throws Exception {
      ISimpleTable simpleTable = null;
      StringWriter stringWriter = new StringWriter();
      ISheetWriter sheetWriter = getSheetWriter(outputFormat, stringWriter);
      switch (style) {
         case STP_Test_to_CSCI:
            simpleTable = new StpTestToCsciTable(sourceData);
            break;
         case STP_CSCI_to_Test:
            simpleTable = new StpCsciToTestTable(sourceData);
            break;
         case STD_Test_to_CSCI:
            simpleTable = new StdTestToCsciTable(sourceData);
            break;
         case STD_CSCI_to_Test:
            simpleTable = new StdCsciToTestTable(sourceData);
            break;
         default:
            throw new OseeArgumentException("Unsupported Style [%s]", style);
      }
      return new TraceabilityTable(stringWriter, sheetWriter, simpleTable);
   }
}
