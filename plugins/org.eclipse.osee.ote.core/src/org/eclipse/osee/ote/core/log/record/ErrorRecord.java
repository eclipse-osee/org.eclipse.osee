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
package org.eclipse.osee.ote.core.log.record;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.XmlSupport;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ErrorRecord extends TestRecord {

   private static final long serialVersionUID = -9083013356154141017L;
   private String executionResult;
   private String executionMessage;
   private boolean printTimeStamp;
   private long timeStamp;
   
   public ErrorRecord(ITestEnvironmentAccessor source, Level level, 
         String executionResult, Throwable throwable, boolean timeStamp) {
      super(source, level, executionResult, false);
      this.executionResult = executionResult;
      this.executionMessage = getThrowbableMessage(throwable);
      
      this.printTimeStamp = timeStamp;
      
      if (timeStamp) {
         if(source !=null) {
            if(source instanceof TestEnvironment){
               TestEnvironment env = (TestEnvironment) source;
               if(env.getTestScript() != null){
            	   this.timeStamp = System.currentTimeMillis() - (env.getTestScript().getStartTime().getTime());
               } else {
            	   this.timeStamp = (new Date()).getTime();
               }
            	   
            }
         } else {
            this.timeStamp = (new Date()).getTime();
            try{ throw new Exception("source was null");}
            catch(Exception e){
            	OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
            }
         }
      }
      
   }
   
   private String getThrowbableMessage(Throwable throwable){
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      pw.println("Exception " + throwable.getClass().getName() + ": " + throwable.getMessage());
      throwable.printStackTrace(new PrintWriter(sw));
	  return sw.toString();
   }
   
   public Element toXml(Document doc){
      Element root = doc.createElement("ExecutionStatus");

      if(this.printTimeStamp) {        
         root.appendChild(Jaxp.createElement(doc, "Time", Long.toString(timeStamp)));
      }
      root.appendChild(Jaxp.createElement(doc, "ExecutionResult", XmlSupport.sanitizeXMLContent(executionResult)));
      root.appendChild(Jaxp.createElement(doc, "ExecutionDetails", XmlSupport.sanitizeXMLContent(executionMessage)));
      return root;
   }
}
