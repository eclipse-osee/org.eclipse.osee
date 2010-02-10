/*
 * Created on Oct 15, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.vcast;

import org.eclipse.osee.coverage.model.ICoverageImportRecordProvider;

/**
 * @author Donald G. Dunne
 */
public interface IVectorCastCoverageImportProvider extends ICoverageImportRecordProvider {

   public String getVCastDirectory();

   public String getFileNamespace(String filename);

   /**
    * true if importer should automatically set known exception handling cases
    */
   public boolean isResolveExceptionHandling();

}
