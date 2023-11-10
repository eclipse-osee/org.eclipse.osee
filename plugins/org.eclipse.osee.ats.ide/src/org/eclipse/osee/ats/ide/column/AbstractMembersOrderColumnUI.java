/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.ats.api.column.AtsColumnTokensDefault.CoreCodeColumnTokenDefault;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsCoreCodeXColumn;
import org.eclipse.osee.ats.ide.workflow.CollectorArtifact;
import org.eclipse.osee.ats.ide.workflow.goal.MembersManager;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractMembersOrderColumnUI extends XViewerAtsCoreCodeXColumn implements IAtsXViewerPreComputedColumn {

   public static final Integer DEFAULT_WIDTH = 45;
   Map<Long, String> multiMembersValueMap = new HashMap<>();
   boolean loading = false;

   public AbstractMembersOrderColumnUI(CoreCodeColumnTokenDefault columnToken) {
      super(columnToken, AtsApiService.get());
   }

   public abstract Artifact getParentMembersArtifact(WorldXViewer worldXViewer);

   public abstract MembersManager<?> getMembersManager();

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      String result = "";
      if (!loading) {
         XViewer xViewer = (XViewer) getXViewer();
         if (obj instanceof Artifact && xViewer instanceof WorldXViewer) {
            WorldXViewer worldXViewer = (WorldXViewer) xViewer;
            CollectorArtifact parentMembersArtifact = (CollectorArtifact) getParentMembersArtifact(worldXViewer);
            if (parentMembersArtifact != null) {
               if (Strings.isValid(cachedValue)) {
                  result = cachedValue;
               }
            } else {
               String cachedObjectValue =
                  multiMembersValueMap.get(AtsApiService.get().getQueryServiceIde().getArtifact(obj).getId());
               if (Strings.isValid(cachedObjectValue)) {
                  result = cachedObjectValue;
               }
            }
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      MembersManager<CollectorArtifact> manager = (MembersManager<CollectorArtifact>) getMembersManager();
      for (Object element : objects) {
         try {
            if (element instanceof Artifact && getXViewer() instanceof WorldXViewer) {
               WorldXViewer worldXViewer = (WorldXViewer) getXViewer();
               CollectorArtifact parentMembersArtifact = (CollectorArtifact) getParentMembersArtifact(worldXViewer);
               Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(element);
               if (parentMembersArtifact != null) {
                  String value = manager.getMemberOrder(parentMembersArtifact, artifact);
                  preComputedValueMap.put(getKey(element), value);
               } else {
                  String value = manager.getMemberOrder(artifact);
                  multiMembersValueMap.put(getKey(element), value);
               }
            }
         } catch (OseeCoreException ex) {
            LogUtil.getCellExceptionString(ex);
         }
      }
   }

}
