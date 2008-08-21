/*
 * Created on Aug 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public class Tester {

   public static void main(String[] args) throws Exception {
      BranchImport importer = new BranchImport();
      importer.importBranch(new File("C:\\Documents and Settings\\b1122182\\hello.zip"), new Options());
   }
}
