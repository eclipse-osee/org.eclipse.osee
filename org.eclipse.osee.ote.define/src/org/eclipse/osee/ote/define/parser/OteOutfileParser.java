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
package org.eclipse.osee.ote.define.parser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.ote.define.parser.handlers.ConfigHandler;
import org.eclipse.osee.ote.define.parser.handlers.CurrentProcessorHandler;
import org.eclipse.osee.ote.define.parser.handlers.DemoInfoHandler;
import org.eclipse.osee.ote.define.parser.handlers.ElapsedTimeHandler;
import org.eclipse.osee.ote.define.parser.handlers.ExecutionStatusHandler;
import org.eclipse.osee.ote.define.parser.handlers.RuntimeVersionsHandler;
import org.eclipse.osee.ote.define.parser.handlers.SystemInfoHandler;
import org.eclipse.osee.ote.define.parser.handlers.TestPointResultsHandler;
import org.eclipse.osee.ote.define.parser.handlers.VersionInformationHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Roberto E. Escobar
 */
public class OteOutfileParser extends BaseOutfileParser {
   private ArrayList<SaxChunkCollector> collectors;

   public OteOutfileParser() {
      super();
      collectors = new ArrayList<SaxChunkCollector>();
      collectors.add(new SaxChunkCollector(new ConfigHandler(), "Config"));
      collectors.add(new SaxChunkCollector(new RuntimeVersionsHandler(), "RuntimeVersions"));
      collectors.add(new SaxChunkCollector(new SystemInfoHandler(), "SystemInfo"));
      collectors.add(new SaxChunkCollector(new CurrentProcessorHandler(), "CurrentProcessor"));
      collectors.add(new SaxChunkCollector(new ExecutionStatusHandler(), "ExecutionStatus"));
      collectors.add(new SaxChunkCollector(new ElapsedTimeHandler(), "TimeSummary"));
      collectors.add(new SaxChunkCollector(new VersionInformationHandler(), "Version"));
      collectors.add(new SaxChunkCollector(new TestPointResultsHandler(), "TestPointResults"));
      collectors.add(new SaxChunkCollector(new DemoInfoHandler(), "Qualification"));
   }

   public void registerListener(IDataListener listener) {
      super.registerListener(listener);
      for (SaxChunkCollector collector : collectors) {
         collector.getHandler().addListener(listener);
      }
   }

   public void deregisterListener(IDataListener listener) {
      super.deregisterListener(listener);
      for (SaxChunkCollector collector : collectors) {
         collector.getHandler().removeListener(listener);
      }
   }

   @Override
   protected void doParse(IProgressMonitor monitor, String fileName, InputStream inputStream) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      CollectionParser handler = new CollectionParser(collectors);
      xmlReader.setContentHandler(handler);
      xmlReader.parse(new InputSource(inputStream));
   }

   @Override
   public boolean isValidParser(URL fileToParse) {
      boolean result = false;
      if (fileToParse != null) {
         String extension = Lib.getExtension(fileToParse.getFile());
         result = extension != null && extension.contains("tmo");
      }
      return result;
   }
}
