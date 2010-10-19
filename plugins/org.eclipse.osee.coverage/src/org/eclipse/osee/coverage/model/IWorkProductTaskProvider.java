/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import java.util.List;

public interface IWorkProductTaskProvider {

   public List<WorkProductAction> getWorkProductRelatedActions();

   public void removeWorkProductAction(WorkProductAction action);

   public void addWorkProductAction(Collection<WorkProductAction> actions);

   public List<WorkProductTask> getWorkProductTasks();

   public WorkProductTask getWorkProductTask(String guid);

   public void setCoveragePackage(CoveragePackage coveragePackage);

   public CoveragePackage getCoveragePackage();

   public void reload();
}
