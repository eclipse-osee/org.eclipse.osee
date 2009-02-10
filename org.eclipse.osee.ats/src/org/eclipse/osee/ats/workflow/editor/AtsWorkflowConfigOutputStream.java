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
package org.eclipse.osee.ats.workflow.editor;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * @author Donald G. Dunne
 *
 */
public class AtsWorkflowConfigOutputStream extends ObjectOutputStream {

   /**
    * @throws IOException
    * @throws SecurityException
    */
   public AtsWorkflowConfigOutputStream() throws IOException, SecurityException {
   }

   /**
    * @param out
    * @throws IOException
    */
   public AtsWorkflowConfigOutputStream(OutputStream out) throws IOException {
      super(out);
   }

}
