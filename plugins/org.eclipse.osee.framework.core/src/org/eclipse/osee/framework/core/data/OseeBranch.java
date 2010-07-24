/*
 * Created on Jul 19, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

/**
 * @author Donald G. Dunne
 */
public class OseeBranch implements IOseeBranch {

   private final String name;
   private final String guid;

   public OseeBranch(String name, String guid) {
      this.name = name;
      this.guid = guid;

   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getName() {
      return name;
   }

}
