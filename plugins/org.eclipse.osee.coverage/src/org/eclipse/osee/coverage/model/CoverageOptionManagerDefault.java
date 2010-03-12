/*
 * Created on Dec 22, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class CoverageOptionManagerDefault extends CoverageOptionManager {

   public static List<CoverageOption> defaultOptions =
         Arrays.asList(Deactivated_Code, Dead_Code, Exception_Handling, Test_Unit, Not_Covered);
   private static CoverageOptionManagerDefault instance = new CoverageOptionManagerDefault();

   private CoverageOptionManagerDefault() {
      super(defaultOptions);
   }

   public static CoverageOptionManagerDefault instance() {
      return instance;
   }

   @Override
   public void add(CoverageOption coverageOption) throws OseeArgumentException {
      throw new OseeArgumentException("Not supported for CoverageOptionManagerDefault");
   }

}
