/*
 * Created on Jan 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.data.model.editor.input;

import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.data.model.editor.model.DataTypeSource;

/**
 * @author Roberto E. Escobar
 */
public interface IOseeDataTypeHandler {
   public boolean isValid(IPath file);

   public DataTypeSource toODMDataTypeSource(IPath file) throws OseeCoreException;
}
