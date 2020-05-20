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
package org.eclipse.osee.ats.core.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class AtsObject extends NamedIdBase implements IAtsObject {

   private String desc;
   private ArtifactToken object;
   private List<WorkType> workTypes = new ArrayList<>();
   private List<String> tags = new ArrayList<>();

   public AtsObject(String name, long id) {
      super(id, name);
   }

   @Override
   public String getDescription() {
      return desc;
   }

   public void setDescription(String desc) {
      this.desc = desc;
   }

   @Override
   public ArtifactToken getStoreObject() {
      return object;
   }

   @Override
   public void setStoreObject(ArtifactToken artifact) {
      this.object = artifact;
   }

   @Override
   public ArtifactTypeToken getArtifactType() {
      return object.getArtifactType();
   }

   @Override
   public AtsApi getAtsApi() {
      return AtsApiService.get();
   }

   @Override
   public List<WorkType> getWorkTypes() {
      return workTypes;
   }

   public void setWorkTypes(List<WorkType> workTypes) {
      this.workTypes = workTypes;
   }

   @Override
   public boolean isWorkType(WorkType workType) {
      return getWorkTypes().contains(workType);
   }

   @Override
   public Collection<String> getTags() {
      return tags;
   }

   public void setTags(List<String> tags) {
      this.tags = tags;
   }

   @Override
   public boolean hasTag(String tag) {
      return getTags().contains(tag);
   }

}