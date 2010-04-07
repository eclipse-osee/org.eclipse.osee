/*
 * Created on Apr 2, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

public interface IComparator {

   String compare(IProgressMonitor monitor, PresentationType presentationType, Artifact baseVersion, Artifact newerVersion, boolean show) throws OseeCoreException;

   String compare(Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, boolean show) throws OseeCoreException;

   void compareArtifacts(IProgressMonitor monitor, PresentationType presentationType, Collection<Pair<Artifact, Artifact>> itemsToCompare) throws OseeCoreException;

}
