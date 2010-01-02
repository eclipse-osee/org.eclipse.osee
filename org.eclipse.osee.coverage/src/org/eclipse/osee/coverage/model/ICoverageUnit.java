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
package org.eclipse.osee.coverage.model;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * Single code unit (file/procedure/function) that can contain other Coverage Unit or Coverage Items
 * 
 * @author Donald G. Dunne
 */
public interface ICoverageUnit {

   public void clearCoverageUnits();

   public void clearCoverageItems();

   public void addCoverageUnit(ICoverageUnit coverageUnit);

   public List<ICoverageUnit> getCoverageUnits();

   public List<ICoverageUnit> getCoverageUnits(boolean recurse);

   public void addCoverageItem(CoverageItem coverageItem);

   public List<CoverageItem> getCoverageItems(boolean recurse);

   public CoverageItem getCoverageItem(String childUnitOrderNum, String itemOrderNumber);

   public String getName();

   public void setName(String name);

   public String getLocation();

   public void setLocation(String location);

   public String getFileContents();

   public void setFileContents(String fileContents);

   public String getGuid();

   public ICoverageUnit getParentCoverageUnit();

   public Result isEditable();

   public OseeImage getOseeImage();

   public boolean isCovered();

   public ICoverage getParent();

   public void setGuid(String guid);

   public String getNamespace();

   public void setNamespace(String namespace);

   public String getAssignees();

   public void setAssignees(String assignees);

   public boolean isAssignable();

   public String getNotes();

   public void setNotes(String notes);

   public List<CoverageItem> getCoverageItemsCovered(boolean recurse);

   public List<CoverageItem> getCoverageItemsCovered(boolean recurse, CoverageOption... CoverageOption);

   public Collection<? extends ICoverage> getChildren();

   public Collection<? extends ICoverage> getChildren(boolean recurse);

   public void removeCoverageUnit(ICoverageUnit coverageUnit);

   public List<CoverageItem> getCoverageItems();

   public void removeCoverageItem(CoverageItem coverageItem);

   public ICoverageUnit copy(boolean includeItems) throws OseeCoreException;

   public String getCoveragePercentStr();

   public int getCoveragePercent();

   public boolean isFolder();

   public void setFolder(boolean folder);

   public List<CoverageItem> getCoverageItemsCovered(CoverageOption... CoverageOption);

   public void updateAssigneesAndNotes(ICoverageUnit coverageUnit);

   public void setParent(ICoverage parent);

   public String getOrderNumber();

   public void setOrderNumber(String orderNumber);

   public void setFileContentsProvider(ICoverageUnitFileContentsProvider fileContentsProvider);

   public ICoverageUnitFileContentsProvider getFileContentsProvider();
}
