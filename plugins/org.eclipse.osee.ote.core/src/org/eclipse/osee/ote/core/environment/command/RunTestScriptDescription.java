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
package org.eclipse.osee.ote.core.environment.command;

import java.util.logging.Level;
import org.eclipse.osee.ote.core.environment.config.ScriptVersionConfig;
import org.eclipse.osee.ote.core.environment.config.TesterConfig;


/**
 * @author Andrew M. Finkbeiner
 */
public class RunTestScriptDescription extends CommandDescription {

   private static final long serialVersionUID = 8824248434175577382L;
   private String clientOutfilePath;
   private String serverOutfilePath;
   private ScriptVersionConfig scriptVersion;
   private TesterConfig testerConfig;
   private Level logLevel;

   public RunTestScriptDescription(String description, String clientOutfilePath,
         ScriptVersionConfig scriptVersion, Level logLevel) {
      super(description);
      this.clientOutfilePath = clientOutfilePath;
      this.scriptVersion = scriptVersion;
      this.testerConfig = new TesterConfig();
      this.logLevel = logLevel;
      if(this.logLevel == null){
         this.logLevel = Level.ALL;
      }
      /*
      if(user != null){
         testerConfig.setEmail(user.getEmail());
         testerConfig.setName(user.getName());
         testerConfig.setId(user.getId());
      }
      */
   }

   public RunTestScriptDescription(String description, String clientOutfilePath,
         ScriptVersionConfig scriptVersion) {
      this(description, clientOutfilePath, scriptVersion, Level.WARNING);
   }
   /**
    * @return Returns the outfile.
    */
   public String getClientOutfilePath() {
      return clientOutfilePath;
   }

   /**
    * @return Returns the serverOutfilePath.
    */
   public String getServerOutfilePath() {
      return serverOutfilePath;
   }

   /**
    * @param serverOutfilePath The serverOutfilePath to set.
    */
   public void setServerOutfilePath(String serverOutfilePath) {
      this.serverOutfilePath = serverOutfilePath;
   }

   /**
    * @param clientOutfilePath The clientOutfilePath to set.
    */
   public void setClientOutfilePath(String clientOutfilePath) {
      this.clientOutfilePath = clientOutfilePath;
   }

   public void setScriptVersion(ScriptVersionConfig scriptVersion) {
      this.scriptVersion = scriptVersion;
   }

   public ScriptVersionConfig getScriptVersion() {
      return this.scriptVersion;
   }
   
   public TesterConfig getTesterConfig() {
      return this.testerConfig;
   }

   /**
    * @return Returns the logLevel.
    */
   public Level getLogLevel() {
      return logLevel;
   }
}
