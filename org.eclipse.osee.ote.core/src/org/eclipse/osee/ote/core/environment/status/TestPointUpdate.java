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
package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;
import org.eclipse.osee.ote.core.environment.command.CommandDescription;

public class TestPointUpdate implements Serializable, IServiceStatusDataCommand {

   private static final long serialVersionUID = 7157851807444983673L;
   private int pass;
   private int fail;
   private CommandDescription description;
   private String testClassName;

   public TestPointUpdate(int pass, int fail, CommandDescription description) {
      this.pass = pass;
      this.fail = fail;
      this.description = description;
   }

   public TestPointUpdate(int pass, int fail, String testClassName) {
	      this.pass = pass;
	      this.fail = fail;
	      this.testClassName = testClassName;
	   }
   
   public TestPointUpdate() {
   }

   public int getFail() {
      return fail;
   }

   public int getPass() {
      return pass;
   }
   
   public String getClassName(){
	   return testClassName;
   }

   public CommandDescription getDescription() {
      return description;
   }

   public void set(int pass, int fail, CommandDescription description) {
      this.pass = pass;
      this.fail = fail;
      this.description = description;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.core.environment.status.IServiceStatusData#accept(org.eclipse.osee.ote.core.environment.status.IServiceStatusDataVisitor)
    */
   public void accept(IServiceStatusDataVisitor visitor) {
      if (visitor != null) {
         visitor.asTestPointUpdate(this);
      }
   }
}
