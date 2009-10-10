/*
 * Created on Sep 28, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public interface ICoverageEditorItem {

   public String getNotes();

   public String getAssignees() throws OseeCoreException;

   public boolean isAssignable();

   public Result isEditable();

   public String getGuid();

   public boolean isCompleted();

   public String getName();

   public String getCoverageEditorValue(XViewerColumn xCol);

   public Image getCoverageEditorImage(XViewerColumn xCol);

   public Object[] getChildren();

   public OseeImage getOseeImage();

   public boolean isCovered();

   public ICoverageEditorItem getParent();

   public Artifact getArtifact(boolean create) throws OseeCoreException;

   public String getText();

   public String getLocation();

   public String getNamespace();

}
