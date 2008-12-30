/*
 * Created on Dec 22, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
