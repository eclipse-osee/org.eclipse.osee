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
package org.eclipse.osee.framework.jini.service.test;

import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.osee.framework.jini.service.core.FormmatedEntry;
import org.eclipse.osee.framework.jini.service.test.enums.ScriptTypeEnum;

public class SupportedScriptTypes extends FormmatedEntry {

   private static final long serialVersionUID = 8403281090133318485L;
   public HashSet<ScriptTypeEnum> supportedClasses;

   public SupportedScriptTypes() {
      supportedClasses = new HashSet<ScriptTypeEnum>();
   }

   public void add(ScriptTypeEnum scriptType) {
      supportedClasses.add(scriptType);
   }

   /*
    * public HashSet<ScriptTypeEnum> getSupportedClasses(){ return supportedClasses; }
    */

   public boolean isSupported(ScriptTypeEnum arg) {
      Iterator<ScriptTypeEnum> it = supportedClasses.iterator();
      while (it.hasNext()) {
         if (arg.getValue() == it.next().getValue()) {
            return true;
         }
      }
      return false;
   }

   public String getFormmatedString() {
      return "Supported Script Types : " + supportedClasses + "\n";
   }
}
