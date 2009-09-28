/*
 * Created on Sep 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.xcover;

import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageEditorProvider {

   public String getName();

   public Object getInput();

   public OseeImage getTitleImage();
}
