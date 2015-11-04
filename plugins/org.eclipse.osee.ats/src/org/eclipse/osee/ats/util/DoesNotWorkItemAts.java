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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Test AtsQuery", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      Artifact teamArt =
         ArtifactQuery.getArtifactFromAttribute(AtsAttributeTypes.AtsId, "ATS16", AtsUtilCore.getAtsBranch());

      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(AtsUtilCore.getAtsBranch());
      queryBuilder.and(AtsAttributeTypes.AtsId, Collections.singleton("ATS16"));
      Artifact teamArt2 = queryBuilder.getResults().getOneOrNull();

      Collection<IAtsWorkItem> items =
         AtsClientService.get().getQueryService().createQuery(WorkItemType.WorkItem).andAttr(AtsAttributeTypes.AtsId,
            Collections.singleton("ATS16")).getItems();

      System.out.println("team " + teamArt);
      System.out.println("team2 " + teamArt2);
      System.out.println("items " + items);

   }
}
