/*********************************************************************
 * Copyright (c) 2010 Boeing
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
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumnUI extends AbstractVersionSelector implements IXViewerPreComputedColumn, BackgroundLoadingValueProvider {

   public static TargetedVersionColumnUI instance = new TargetedVersionColumnUI();
   public AtomicBoolean loading = new AtomicBoolean(false);
   public AtomicBoolean loaded = new AtomicBoolean(false);
   protected Map<Long, String> idToValueMap = new HashMap<>();

   public TargetedVersionColumnUI() {
      super(AtsColumnTokens.TargetedVersionColumn);
   }

   public static TargetedVersionColumnUI getInstance() {
      return instance;
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public TargetedVersionColumnUI copy() {
      TargetedVersionColumnUI newXCol = new TargetedVersionColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public RelationTypeSide getRelation() {
      return AtsRelationTypes.TeamWorkflowTargetedForVersion_Version;
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof IAtsTeamWorkflow) {
         return ((IAtsTeamWorkflow) obj).getId();
      }
      return null;
   }

   @Override
   public String getValue(IAtsWorkItem teamWf, Map<Long, String> idToValueMap) {
      return super.getColumnText(teamWf, null, 0);
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      if (!loaded.get() && !loading.getAndSet(true)) {
         BackgroundLoadingColumn.startLoadingThread(getName(), objects, loading, loaded, idToValueMap,
            (WorldXViewer) getXViewer(), this);
      }
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return BackgroundLoadingColumn.getText(obj, loading, loaded, idToValueMap);
   }

}
