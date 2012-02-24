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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.report.ISimpleTable;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;
import org.eclipse.osee.framework.jdk.core.type.IVariantData;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;

/**
 * @author Roberto E. Escobar
 */
public class TraceabilityTable {
   private static final Pattern WORKSHEET_START_PATTERN = Pattern.compile("(<Worksheet.*?>)");
   private static final Pattern TABLE_START_PATTERN = Pattern.compile("(<Table.*?>)");
   private static final Pattern STYLE_PATTERN = Pattern.compile("<Styles>.*</Styles>\\s*", Pattern.DOTALL);

   private CharSequence result;
   private final StringWriter stringWriter;
   private final ISheetWriter sheetWriter;
   private final ISimpleTable style;

   protected TraceabilityTable(StringWriter stringWriter, ISheetWriter sheetWriter, ISimpleTable style) {
      this.stringWriter = stringWriter;
      this.sheetWriter = sheetWriter;
      this.style = style;
      this.result = null;
   }

   public void setOptions(IVariantData data) {
      if (style != null && style instanceof ICustomizable) {
         ((ICustomizable) style).setOptions(data);
      }
   }

   public void run(IProgressMonitor monitor) throws Exception {
      style.initializeSheet(sheetWriter);
      style.generateBody(sheetWriter);
      sheetWriter.endSheet();
      sheetWriter.endWorkbook();
      postProcess();
   }

   private void postProcess() throws Exception {
      String source = stringWriter.toString();
      ChangeSet changeSet = new ChangeSet(source);

      Matcher match = STYLE_PATTERN.matcher(source);
      if (match.find()) {
         changeSet.replace(match.start(), match.end(), style.getHeaderStyles());
      }
      Matcher match2 = TABLE_START_PATTERN.matcher(source);
      if (match2.find()) {
         changeSet.insertBefore(match2.end(), style.getHeader());
      }
      result = changeSet.applyChangesToSelf();
   }

   @Override
   public String toString() {
      return result.toString();
   }
}
