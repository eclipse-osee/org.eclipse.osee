/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsWorkData;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkItemServiceImpl implements IAtsWorkItemService {

   private static AtsWorkItemServiceImpl instance;

   public AtsWorkItemServiceImpl() {
      AtsWorkItemServiceImpl.instance = this;
   }

   public static AtsWorkItemServiceImpl instance() {
      if (instance == null) {
         instance = new AtsWorkItemServiceImpl();
      }
      return instance;
   }

   @Override
   public IAtsWorkData getWorkData(IAtsWorkItem workItem) throws OseeCoreException {
      return AtsWorkItemServiceStore.getService().getWorkData(workItem);
   }

   @Override
   public IArtifactType getArtifactType(IAtsWorkItem workItem) throws OseeCoreException {
      return AtsWorkItemServiceStore.getService().getArtifactType(workItem);
   }

   @Override
   public Collection<Object> getAttributeValues(IAtsObject workItem, IAttributeType attributeType) throws OseeCoreException {
      return AtsWorkItemServiceStore.getService().getAttributeValues(workItem, attributeType);
   }

   @Override
   public boolean isOfType(IAtsWorkItem item, IArtifactType matchType) throws OseeCoreException {
      return AtsWorkItemServiceStore.getService().isOfType(item, matchType);
   }

   @Override
   public IAtsWorkItem getParentTeamWorkflow(IAtsWorkItem workItem) throws OseeCoreException {
      return AtsWorkItemServiceStore.getService().getParentTeamWorkflow(workItem);
   }

}
