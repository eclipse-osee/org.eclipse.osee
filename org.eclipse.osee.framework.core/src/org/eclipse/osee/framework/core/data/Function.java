/*
 * Created on Nov 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Megumi Telles
 */
public enum Function {

   BRANCHCOMMIT, CHANGEREPORT, CREATEFULLBRANCH;

   public static Function fromString(String name) throws OseeCoreException {
      if (!Strings.isValid(name)) {
         throw new OseeArgumentException("Name cannot be null or empty");
      }
      String toMatch = name.toUpperCase();
      for (Function function : Function.values()) {
         if (function.name().equals(toMatch)) {
            return function;
         }
      }
      throw new OseeCoreException("Invalid name - Function was not found");
   }
}
