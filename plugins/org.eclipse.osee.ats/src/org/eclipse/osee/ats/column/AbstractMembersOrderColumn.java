/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IAltLeftClickProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.workflow.CollectorArtifact;
import org.eclipse.osee.ats.workflow.goal.MembersManager;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractMembersOrderColumn extends XViewerAtsColumn implements IAtsXViewerPreComputedColumn, IAltLeftClickProvider {

   public static final Integer DEFAULT_WIDTH = 45;
   Map<Long, String> multiMembersValueMap = new HashMap<>();
   boolean loading = false;

   public AbstractMembersOrderColumn(String id, String name, int width, XViewerAlign align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String description) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description);
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
               String cachedObjectValue = multiMembersValueMap.get(((Artifact) obj).getId());
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
               if (parentMembersArtifact != null) {
                  String value = manager.getMemberOrder(parentMembersArtifact, (Artifact) element);
                  preComputedValueMap.put(getKey(element), value);
               } else {
                  String value = manager.getMemberOrder((Artifact) element);
                  multiMembersValueMap.put(getKey(element), value);
               }
            }
         } catch (OseeCoreException ex) {
            LogUtil.getCellExceptionString(ex);
         }
      }
   }

}
