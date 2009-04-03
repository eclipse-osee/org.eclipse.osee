/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.utility;

import java.nio.CharBuffer;
import org.eclipse.core.filesystem.IFileStore;

/**
 * @author Roberto E. Escobar
 */
public interface IResourceLocator {

   public boolean isValidFileStore(IFileStore fileStore);

   public boolean hasValidContent(CharBuffer fileBuffer);

   public String getIdentifier(IFileStore fileStore, CharBuffer fileBuffer) throws Exception;
}
