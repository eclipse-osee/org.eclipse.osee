/*
 * Created on Aug 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Theron Virgin
 */
public interface IVbaDiffGenerator {
   public boolean initialize(boolean visible, boolean detectFormatChanges);

   public boolean addComparison(IFile baseFile, IFile newerFile, String diffPath, boolean merge);

   public void finish(String path) throws OseeCoreException;

   public File getFile(String path) throws OseeCoreException;
}
