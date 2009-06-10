/*
 * Created on Jun 3, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author b1565043
 *
 */
public interface StreamContent extends ITypedElement,
IStructureComparator {

public boolean isDirty();

public boolean commitChanges(IProgressMonitor pm) throws CoreException;

public void dispose();

public void init(AnyeditCompareInput input);

public StreamContent recreate();

boolean isDisposed();

String getFullName();
}
