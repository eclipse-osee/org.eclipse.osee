/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.branch.management;

import java.io.File;
import java.util.List;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
public interface IBranchImport {

   public void importBranch(File fileToImport, Options options, int... branchesToImport) throws Exception;

   public void importBranch(File fileToImport, Options options, List<Integer> branchesToImport) throws Exception;
}
