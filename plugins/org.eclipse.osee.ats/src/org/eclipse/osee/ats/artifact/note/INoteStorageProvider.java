/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact.note;

import org.eclipse.osee.framework.ui.plugin.util.Result;

public interface INoteStorageProvider {

   String getNoteXml();

   Result saveNoteXml(String xml);

   String getNoteTitle();

   String getNoteId();

   boolean isNoteable();
}
