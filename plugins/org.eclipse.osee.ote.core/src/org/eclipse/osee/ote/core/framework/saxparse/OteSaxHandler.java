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
package org.eclipse.osee.ote.core.framework.saxparse;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.io.xml.AbstractSaxHandler;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Actual;
import org.eclipse.osee.ote.core.framework.saxparse.elements.AdditionalInfo;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Argument;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Attention;
import org.eclipse.osee.ote.core.framework.saxparse.elements.CheckGroup;
import org.eclipse.osee.ote.core.framework.saxparse.elements.CheckPoint;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Config;
import org.eclipse.osee.ote.core.framework.saxparse.elements.CurrentProcessor;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Debug;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ElapsedTime;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Environment;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ExecutedBy;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ExecutionDate;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Expected;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Global;
import org.eclipse.osee.ote.core.framework.saxparse.elements.GroupName;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Info;
import org.eclipse.osee.ote.core.framework.saxparse.elements.InfoGroup;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Location;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Message;
import org.eclipse.osee.ote.core.framework.saxparse.elements.MethodArguments;
import org.eclipse.osee.ote.core.framework.saxparse.elements.MethodName;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Name;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Notes;
import org.eclipse.osee.ote.core.framework.saxparse.elements.NumberOfTransmissions;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ObjectName;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OfpErrorEntry;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OfpLoggingInfo;
import org.eclipse.osee.ote.core.framework.saxparse.elements.OteLog;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Qualification;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Requirement;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Result;
import org.eclipse.osee.ote.core.framework.saxparse.elements.RetryGroup;
import org.eclipse.osee.ote.core.framework.saxparse.elements.RuntimeVersions;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ScriptInit;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ScriptName;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ScriptResult;
import org.eclipse.osee.ote.core.framework.saxparse.elements.ScriptVersion;
import org.eclipse.osee.ote.core.framework.saxparse.elements.SoftKeyInfoGroup;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Stacktrace;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Summary;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Support;
import org.eclipse.osee.ote.core.framework.saxparse.elements.SystemInfo;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestCase;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPoint;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointName;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestPointResults;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TestScript;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Time;
import org.eclipse.osee.ote.core.framework.saxparse.elements.TimeSummary;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Tracability;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Trace;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Type;
import org.eclipse.osee.ote.core.framework.saxparse.elements.User;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Value;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Version;
import org.eclipse.osee.ote.core.framework.saxparse.elements.VersionInformation;
import org.eclipse.osee.ote.core.framework.saxparse.elements.Witnesses;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Andrew M. Finkbeiner
 *
 *XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      CollectionParser handler = new CollectionParser(collectors);
      xmlReader.setContentHandler(handler);
      xmlReader.parse(new InputSource(inputStream));
 *
 */
public class OteSaxHandler extends AbstractSaxHandler {

   Map<String, ElementHandlers> handlers;

   public OteSaxHandler() throws Exception {
      handlers = new HashMap<String, ElementHandlers>();
      addHandlers(new Global());
      addHandlers(new AdditionalInfo());
      addHandlers(new Actual());
      addHandlers(new Argument());
      addHandlers(new Attention());
      addHandlers(new CheckGroup());
      addHandlers(new CheckPoint());
      addHandlers(new Config());
      addHandlers(new CurrentProcessor());
      addHandlers(new Debug());
      addHandlers(new ElapsedTime());
      addHandlers(new Environment());
      addHandlers(new ExecutedBy());
      addHandlers(new ExecutionDate());
      addHandlers(new Expected());
      addHandlers(new GroupName());
      addHandlers(new Info());
      addHandlers(new InfoGroup());
      addHandlers(new Location());
      addHandlers(new Message());
      addHandlers(new MethodArguments());
      addHandlers(new MethodName());
      addHandlers(new Name());
      addHandlers(new Notes());
      addHandlers(new org.eclipse.osee.ote.core.framework.saxparse.elements.Number());
      addHandlers(new NumberOfTransmissions());
      addHandlers(new ObjectName());
      addHandlers(new OfpErrorEntry());
      addHandlers(new OfpLoggingInfo());
      addHandlers(new OteLog());
      addHandlers(new Qualification());
      addHandlers(new Requirement());
      addHandlers(new Result());
      addHandlers(new RetryGroup());
      addHandlers(new RuntimeVersions());
      addHandlers(new ScriptInit());
      addHandlers(new ScriptName());
      addHandlers(new ScriptResult());
      addHandlers(new ScriptVersion());
      addHandlers(new SoftKeyInfoGroup());
      addHandlers(new Stacktrace());
      addHandlers(new Summary());
      addHandlers(new Support());
      addHandlers(new SystemInfo());
      addHandlers(new TestCase());
      addHandlers(new TestPoint());
      addHandlers(new TestPointName());
      addHandlers(new TestPointResults());
      addHandlers(new TestScript());
      addHandlers(new Time());
      addHandlers(new TimeSummary());
      addHandlers(new Tracability());
      addHandlers(new Trace());
      addHandlers(new Type());
      addHandlers(new User());
      addHandlers(new Value());
      addHandlers(new Version());
      addHandlers(new VersionInformation());
      addHandlers(new Witnesses());
   }

   @Override
   public void endElementFound(String uri, String localName, String name) throws SAXException {
      ElementHandlers handler;

      handler = handlers.get("*");
      if(handler != null){
         handler.endElementFound(uri, localName, name, stripCData(getContents().trim()));
      }
      
      handler = handlers.get(name);
      if(handler != null){
         handler.endElementFound(uri, localName, name, stripCData(getContents().trim()));
      }
   }

   private String stripCData(String content){
      if(content.startsWith("<![CDATA[")){
         return content.subSequence(9, content.length()-3).toString();
      } else {
         return content;
      }
         
   }
   
   @Override
   public void startElementFound(String uri, String localName, String name, Attributes attributes) throws SAXException {
      ElementHandlers handler;
      
      handler = handlers.get("*");
      if(handler != null){
         handler.startElementFound(uri, localName, name, attributes);
      }
      
      handler = handlers.get(name);
      if(handler != null){
         handler.startElementFound(uri, localName, name, attributes);
      } else {
         System.out.println(name);
      }
   }
   
   public void addHandlers(ElementHandlers handler) throws Exception{
      Object obj = handlers.put(handler.getElementName(), handler);
      if(obj != null){
         throw new Exception("Duplicate handler.");
      }
   }
   
   public ElementHandlers getHandler(String elementName){
      return handlers.get(elementName);
   }
}