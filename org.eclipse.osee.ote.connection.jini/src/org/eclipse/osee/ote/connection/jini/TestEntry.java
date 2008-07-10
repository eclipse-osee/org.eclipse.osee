/*
 * Created on Jul 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.connection.jini;

import net.jini.entry.AbstractEntry;

/**
 * @author b1529404
 */
public class TestEntry extends AbstractEntry {
   /**
    * 
    */
   private static final long serialVersionUID = -2239353039479522642L;
   public final String data;

   public TestEntry() {
      data = "<none>";
   }

   /**
    * @param data
    */
   public TestEntry(String data) {
      super();
      this.data = data;
   }

   /**
    * @return the data
    */
   public String getData() {
      return data;
   }

}
