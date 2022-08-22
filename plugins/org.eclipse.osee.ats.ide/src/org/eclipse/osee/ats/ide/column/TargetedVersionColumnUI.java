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
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.column.AtsColumnTokens;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionColumnUI extends AbstractVersionSelector implements BackgroundLoadingValueProvider, IXViewerPreComputedColumn {

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
   public String getValue(IAtsWorkItem workItem, Map<Long, String> idToValueMap) {
      String value = super.getColumnText(workItem, null, 0);
      if (Strings.isValid(value)) {
         idToValueMap.put(workItem.getId(), value);
      }
      return value;
   }

   public String getValue(Object obj) {
      String value = super.getColumnText(obj, null, 0);
      if (Strings.isValid(value)) {
         if (obj instanceof NamedId) {
            idToValueMap.put(((NamedId) obj).getId(), value);
         }
      }
      return value;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      this.preComputedValueMap = preComputedValueMap;
      this.loaded.set(false);
      this.loading.set(false);
      for (Object obj : objects) {
         this.preComputedValueMap.put(getKey(obj), "loading...");
      }
      BackgroundLoadingPreComputedColumn.startLoadingThread(getName(), objects, loading, loaded,
         (WorldXViewer) getXViewer(), preComputedValueMap, this);
   }

   @Override
   public String getColumnText(Object obj, XViewerColumn column, int columnIndex) {
      String value = BackgroundLoadingPreComputedColumn.getColumnText(obj, loading, loaded, preComputedValueMap, this);
      return value;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      String value = BackgroundLoadingPreComputedColumn.getColumnText(obj, loading, loaded, preComputedValueMap, this);
      return value;
   }

   @Override
   public Long getKey(Object obj) {
      if (obj instanceof Id) {
         return ((Id) obj).getId();
      }
      return Id.SENTINEL;
   }

}
