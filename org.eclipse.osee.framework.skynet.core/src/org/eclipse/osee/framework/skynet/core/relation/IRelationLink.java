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
package org.eclipse.osee.framework.skynet.core.relation;

import java.sql.SQLException;
import org.eclipse.osee.framework.jdk.core.util.PersistenceObject;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public interface IRelationLink extends PersistenceObject {

   public static final IRelationLink[] EMPTY_ARRAY = new IRelationLink[0];

   public IRelationLinkDescriptor getLinkDescriptor();

   public int getAOrder();

   public void setAOrder(int order);

   public void swapAOrder(IRelationLink link);

   public int getBOrder();

   public void setBOrder(int order);

   public void swapBOrder(IRelationLink link);

   public Artifact getArtifactA();

   public Artifact getArtifactB();

   public void setArtifactA(Artifact artA);

   public void setArtifactB(Artifact artB);

   public void setArtifactA(Artifact artA, boolean remoteEvent);

   public void setArtifactB(Artifact artB, boolean remoteEvent);

   public String getRationale();

   public String getSideNameFor(Artifact artifact);

   public String getSidePhrasingFor(Artifact artifact);

   public String getSideNameForOtherArtifact(Artifact artifact);

   public String getSidePhrasingForOtherArtifact(Artifact artifact);

   public void setRationale(String rationale, boolean notify);

   public LinkPersistenceMemo getPersistenceMemo();

   public void delete() throws SQLException;

   public boolean isDeleted();

   public void persist() throws SQLException;

   public void persist(boolean recursive) throws SQLException;

   public boolean isExplorable();

   public boolean isDirty();

   public void setNotDirty();

   public void setDirty();

   public void setDirty(boolean isDirty);

   public String getASideName();

   public String getBSideName();

   public boolean isVersionControlled();

   public Branch getBranch();
}
