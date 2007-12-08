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

import org.eclipse.osee.framework.jdk.core.util.PersistenceObject;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

public interface IRelationLinkDescriptor extends PersistenceObject, Comparable<IRelationLinkDescriptor> {

   public IRelationLink makeNewLink();

   /**
    * @return Returns the aToBPhrasing.
    */
   public String getAToBPhrasing();

   /**
    * @return Returns the bToAPhrasing.
    */
   public String getBToAPhrasing();

   /**
    * @return Returns the name.
    */
   public String getName();

   public String getSideName(boolean sideA);

   /**
    * @return Returns the sideAName.
    */
   public String getSideAName();

   /**
    * @return Returns the sideBName.
    */
   public String getSideBName();

   /**
    * @return Returns the shortName;
    */
   public String getShortName();

   public boolean isSideAName(String sideName);

   public LinkDescriptorPersistenceMemo getPersistenceMemo();

   public void setLinkSideRestriction(int artTypeId, LinkSideRestriction linkSideRestriction);

   public boolean canLinkType(int id);

   public int getRestrictionSizeFor(int id, boolean sideA);

   public void ensureSideWillSupportArtifact(boolean sideA, Artifact artifact, int artifactCount);

   public TransactionId getTransactionId();
}
