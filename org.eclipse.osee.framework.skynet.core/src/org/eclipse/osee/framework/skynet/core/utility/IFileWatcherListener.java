/*
 * Created on Jun 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.Collection;

/**
 * @author b1529404
 */
public interface IFileWatcherListener {

   void filesModified(Collection<FileChangeEvent> fileChangeEvents);
}
