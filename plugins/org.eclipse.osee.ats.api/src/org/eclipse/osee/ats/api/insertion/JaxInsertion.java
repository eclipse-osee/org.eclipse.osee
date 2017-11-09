/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.insertion;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.config.JaxAtsObject;

/**
 * @author David W. Miller
 */
@XmlRootElement
public class JaxInsertion extends JaxAtsObject {

   private long programId;

   public long getProgramId() {
      return programId;
   }

   public void setProgramId(long programId) {
      this.programId = programId;
   }

}
