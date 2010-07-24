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
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.Named;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public interface ICoverage extends Named, Identity {

   public String getNotes();

   public String getAssignees() throws OseeCoreException;

   public boolean isAssignable();

   public Result isEditable();

   public Collection<? extends ICoverage> getChildren();

   public Collection<? extends ICoverage> getChildren(boolean recurse);

   public KeyedImage getOseeImage();

   public boolean isCovered();

   public ICoverage getParent();

   public String getFileContents() throws OseeCoreException;

   public String getLocation();

   public String getNamespace();

   public Double getCoveragePercent();

   public String getCoveragePercentStr();

   public boolean isFolder();

   public String getOrderNumber();

}
