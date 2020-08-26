/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.world.search;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class MySubscribedSearchItem extends UserSearchItem {

   public MySubscribedSearchItem(String name, AtsUser user) {
      super(name, user, AtsImage.SUBSCRIBED);
   }

   public MySubscribedSearchItem(MySubscribedSearchItem mySubscribedSearchItem) {
      super(mySubscribedSearchItem, AtsImage.SUBSCRIBED);
   }

   @Override
   protected Collection<Artifact> searchIt(AtsUser user) {
      return Collections.castAll(AtsApiService.get().getRelationResolver().getRelated((IAtsObject) user,
         AtsRelationTypes.SubscribedUser_Artifact));
   }

   @Override
   public WorldUISearchItem copy() {
      return new MySubscribedSearchItem(this);
   }

}