/*
 * Created on Feb 8, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

import java.io.InputStream;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageImportRecordProvider {

   public InputStream getImportRecordZipInputStream() throws OseeCoreException;
}
