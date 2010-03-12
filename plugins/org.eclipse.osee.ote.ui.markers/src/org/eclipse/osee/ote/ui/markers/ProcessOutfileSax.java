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
package org.eclipse.osee.ote.ui.markers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.osee.ote.core.framework.saxparse.IBaseSaxElementListener;
import org.eclipse.osee.ote.core.framework.saxparse.OteSaxHandler;
import org.eclipse.osee.ote.core.framework.saxparse.elements.StacktraceData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Andrew M. Finkbeiner
 */
public class ProcessOutfileSax implements IExceptionableRunnable {

   private IFile file;
   private MarkerPlugin plugin;

   private static final int _1_MB = 1048576;
   private static final int _20_MB = _1_MB * 20;
   private static final int _30_MB = _1_MB * 30;

   private List<TestPointData> testPointDatas = new ArrayList<TestPointData>();
   private TestPointData currentData = null;
   private CheckPointData currentCheckPoint = null;
   protected StackTraceCollection currentStackTrace;

   public ProcessOutfileSax(MarkerPlugin plugin, IFile file) {
      this.file = file;
      this.plugin = plugin;
   }

   public IStatus run(IProgressMonitor monitor) throws Exception {
      File outfile = AWorkspace.iFileToFile(file);
      if (outfile.length() > _20_MB) {
         OseeLog.log(MarkerPlugin.class, Level.WARNING, String.format(
               "%s has a length of [%d], the max size processed is [%d].", file.getName(), outfile.length(), _20_MB));
         return Status.OK_STATUS;
      }
      if (!file.isSynchronized(0)) {
         OseeLog.log(MarkerPlugin.class, Level.WARNING, String.format("%s is not synchronized.", file.getName()));
         file.refreshLocal(0, monitor);
      }

      monitor.setTaskName(String.format("Computing overview information for [%s].", file.getName()));

      InputStream contents = file.getContents();
      
      parseContents(contents);

      OteMarkerHelper helper = new OteMarkerHelper(this.testPointDatas);
      plugin.updateMarkerInfo(file, helper.getMarkers());

      return Status.OK_STATUS;
   }

   /**
    * @param contents
    * @throws SAXException
    * @throws Exception
    * @throws SAXNotRecognizedException
    * @throws SAXNotSupportedException
    * @throws IOException
    */
   private void parseContents(InputStream contents) throws SAXException, Exception, SAXNotRecognizedException, SAXNotSupportedException, IOException {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      OteSaxHandler handler = new OteSaxHandler();
      xmlReader.setContentHandler(handler);
      xmlReader.setProperty("http://xml.org/sax/properties/lexical-handler", handler); // This is the important part

      final Stack<String> elementStack = new Stack<String>();
      handler.getHandler("*").addListener(new IBaseSaxElementListener() {
         
         @Override
         public void onStartElement(Object obj) {
            elementStack.push((String)obj);
         }
         
         @Override
         public void onEndElement(Object obj) {
            elementStack.pop();
         }
      });
      
      handler.getHandler("TestPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentData.isFailed()) {
               testPointDatas.add(currentData);
            }
            currentData = null;
         }

         @Override
         public void onStartElement(Object obj) {
            currentData = new TestPointData();
         }
      });
      handler.getHandler("CheckPoint").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentCheckPoint.isFailed()) {
               currentData.add(currentCheckPoint);
            }
            currentCheckPoint = null;
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentData != null) {
               currentCheckPoint = new CheckPointData();
            }
         }
      });

      handler.getHandler("Result").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            boolean failed = false;
            if ("FAILED".equals(obj)) {
               failed = true;
            }

            if (currentCheckPoint != null) {
               currentCheckPoint.setFailed(failed);
            } else if (currentData != null && elementStack.peek().equals("TestPoint")) {
               currentData.setFailed(failed);
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });

      handler.getHandler("TestPointName").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentCheckPoint != null) {
               currentCheckPoint.setName(obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Expected").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentCheckPoint != null) {
               currentCheckPoint.setExpected(spaceProcessing(obj.toString()));
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Actual").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentCheckPoint != null) {
               currentCheckPoint.setActual(spaceProcessing(obj.toString()));
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Number").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentData != null) {
               currentData.setNumber(obj.toString());
            }
         }

         @Override
         public void onStartElement(Object obj) {
         }
      });
      handler.getHandler("Location").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
            if (currentStackTrace != null) {
               currentData.setStackTrace(currentStackTrace);
               currentStackTrace = null;
            }
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentData != null) {
               currentStackTrace = new StackTraceCollection();
            }
         }
      });
      handler.getHandler("Stacktrace").addListener(new IBaseSaxElementListener() {
         @Override
         public void onEndElement(Object obj) {
         }

         @Override
         public void onStartElement(Object obj) {
            if (currentStackTrace != null) {
               currentStackTrace.addTrace((StacktraceData) obj);
            }
         }
      });

      xmlReader.parse(new InputSource(contents));
   }

   private String spaceProcessing(String expected) {
      int index = 0, preCount = 0, postCount = 0;
      while (index < expected.length() && expected.charAt(index) == ' ') {
         index++;
         preCount++;
      }
      index = expected.length() - 1;
      while (index >= 0 && expected.charAt(index) == ' ') {
         index--;
         postCount++;
      }

      expected = expected.trim();
      if (preCount > 0) {
         expected = String.format("#sp%d#%s", preCount, expected);
      }
      if (postCount > 0) {
         expected = String.format("%s#sp%d#", expected, postCount);
      }

      return expected;
   }
}
