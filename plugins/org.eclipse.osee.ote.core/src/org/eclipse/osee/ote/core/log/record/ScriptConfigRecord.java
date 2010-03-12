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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;
import org.eclipse.osee.ote.core.log.TestLevel;
import org.eclipse.osee.ote.core.test.tags.BaseTestTags;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class ScriptConfigRecord extends TestRecord {

   private ScriptVersionConfig scriptVersionRecord;

   private String userName;

   private String userEmail;

   private String userId;

   /**
    * 
    */
   private static final long serialVersionUID = 6919229589873467398L;

   /**
    * ScriptConfigRecord Constructor. Constructs test script configuration log message with
    * timestamp.
    * 
    * @param script The test script who's configuration is to be recorded.
    * @param timeStamp <b>True </b> if a timestamp should be recorded, <b>False </b> if not.
    */
   public ScriptConfigRecord(TestScript script, boolean timeStamp) {
      super(script.getTestEnvironment(), TestLevel.TEST_POINT, script.getClass().getName(), timeStamp);
   }

   /**
    * ScriptConfigRecord Constructor. Constructs test script configuration log message.
    * 
    * @param script The test script who's configuration is to be recorded.
    */
   public ScriptConfigRecord(TestScript script) {
      this(script, true);
   }

   /**
    * Convert an element to XML format.
    * 
    * @return XML formated config element.
    */
   public Element toXml(Document doc) {
      Element configElement = doc.createElement(BaseTestTags.CONFIG_ENTRY);

      try {
         configElement.setAttribute("machineName", InetAddress.getLocalHost().getHostName());

      }
      catch (DOMException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }
      catch (UnknownHostException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
      }

      configElement.appendChild(Jaxp.createElement(doc, BaseTestTags.SCRIPT_NAME, getMessage()));
      Element user = doc.createElement("User");
      user.setAttribute("name", this.userName);
      user.setAttribute("email", this.userEmail);
      user.setAttribute("id", this.userId);
      configElement.appendChild(user);

      if (scriptVersionRecord != null) {
         configElement.appendChild(scriptVersionRecord.toXml(doc));
      }
      // if (executedBy != null) {
      // configElement.appendChild(executedBy.toXml(doc));
      // }

      DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
      configElement.appendChild(Jaxp.createElement(doc, BaseTestTags.EXECUTION_DATE, dateFormat.format(new Date()).toString()));

      TestScript script = ((TestEnvironment) this.getSource()).getTestScript();
      if (script != null) {
         configElement.appendChild(Jaxp.createElement(doc, BaseTestTags.ENVIRONMENT_FIELD, script.getType().toString()));
      }
      else {
    	  // script is null
         configElement.appendChild(Jaxp.createElement(doc, BaseTestTags.ENVIRONMENT_FIELD, "Null Script"));
      }

      return configElement;
   }

   public void setScriptVersion(ScriptVersionConfig scriptVersionRecord) {
      this.scriptVersionRecord = scriptVersionRecord;
   }

   public void setExecutedBy(String name, String email, String id) {
      this.userName = name;
      this.userEmail = email;
      this.userId = id;

   }
}