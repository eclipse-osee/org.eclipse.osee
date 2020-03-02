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
package org.eclipse.osee.define.ide.traceability;

import java.io.StringWriter;
import org.eclipse.osee.define.ide.traceability.report.ISimpleTable;
import org.eclipse.osee.define.ide.traceability.report.StdCsciToTestTable;
import org.eclipse.osee.define.ide.traceability.report.StdTestToCsciTable;
import org.eclipse.osee.define.ide.traceability.report.StpCsciToTestTable;
import org.eclipse.osee.define.ide.traceability.report.StpTestToCsciTable;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityFactory {

   public enum TraceabilityStyle {
      STP_Test_to_CSCI,
      STP_CSCI_to_Test,
      STD_Test_to_CSCI,
      STD_CSCI_to_Test,
      STD_Test_to_CSCI_One_Per_Row,
      STD_CSCI_to_Test_One_Per_Row;

      @Override
      public String toString() {
         return name().toUpperCase();
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }
   }

   public static TraceabilityTable getTraceabilityTable(TraceabilityStyle style, RequirementTraceabilityData sourceData) throws Exception {
      ISimpleTable simpleTable = null;
      StringWriter stringWriter = new StringWriter();
      ExcelXmlWriter sheetWriter = new ExcelXmlWriter(stringWriter);
      switch (style) {
         case STP_Test_to_CSCI:
            simpleTable = new StpTestToCsciTable(sourceData);
            break;
         case STP_CSCI_to_Test:
            simpleTable = new StpCsciToTestTable(sourceData);
            break;
         case STD_Test_to_CSCI:
            simpleTable = new StdTestToCsciTable(sourceData, false);
            break;
         case STD_CSCI_to_Test:
            simpleTable = new StdCsciToTestTable(sourceData, false);
            break;
         case STD_Test_to_CSCI_One_Per_Row:
            simpleTable = new StdTestToCsciTable(sourceData, true);
            break;
         case STD_CSCI_to_Test_One_Per_Row:
            simpleTable = new StdCsciToTestTable(sourceData, true);
            break;
         default:
            throw new OseeArgumentException("Unsupported Style [%s]", style);
      }
      return new TraceabilityTable(stringWriter, sheetWriter, simpleTable);
   }
}
