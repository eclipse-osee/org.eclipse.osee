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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.ide.traceability.report.ISimpleTable;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityTable {
   private static final Pattern TABLE_START_PATTERN = Pattern.compile("(<Table.*?>)");
   private static final Pattern COLUMN_PATTERN = Pattern.compile("(\\s*<Column.*?/>\\s*)");
   private static final Pattern STYLE_PATTERN = Pattern.compile("<Styles>.*</Styles>\\s*", Pattern.DOTALL);

   private CharSequence result;
   private final StringWriter stringWriter;
   private final ExcelXmlWriter sheetWriter;
   private final ISimpleTable[] styles;

   public TraceabilityTable(StringWriter stringWriter, ExcelXmlWriter sheetWriter, ISimpleTable... styles) {
      this.stringWriter = stringWriter;
      this.sheetWriter = sheetWriter;
      this.styles = styles;
      this.result = null;
   }

   public void setOptions(IVariantData data) {
      for (ISimpleTable style : styles) {
         if (style != null && style instanceof ICustomizable) {
            ((ICustomizable) style).setOptions(data);
         }
      }
   }

   public void run(IProgressMonitor monitor) throws Exception {
      for (ISimpleTable style : styles) {
         style.initializeSheet(sheetWriter);
         sheetWriter.startSheet(style.getWorksheetName(), style.getColumnCount());
         style.generateBody(sheetWriter);
         sheetWriter.endSheet();
      }
      sheetWriter.endWorkbook();
      postProcess();
   }

   private void postProcess() throws Exception {
      String source = stringWriter.toString();
      ChangeSet changeSet = new ChangeSet(source);
      Matcher styleMatcher = STYLE_PATTERN.matcher(source);
      Matcher tableMatcher = TABLE_START_PATTERN.matcher(source);
      Matcher columnMatcher = COLUMN_PATTERN.matcher(source);

      // if more than one style present, this will only apply once, so first style's header is applied
      if (styleMatcher.find()) {
         changeSet.replace(styleMatcher.start(), styleMatcher.end(), styles[0].getHeaderStyles());
      }

      for (ISimpleTable style : styles) {
         if (tableMatcher.find()) {
            changeSet.insertBefore(tableMatcher.end(), style.getHeader());
         }
      }

      while (columnMatcher.find()) {
         changeSet.delete(columnMatcher.start(), columnMatcher.end());
      }
      result = changeSet.applyChangesToSelf();
   }

   @Override
   public String toString() {
      return result.toString();
   }
}
