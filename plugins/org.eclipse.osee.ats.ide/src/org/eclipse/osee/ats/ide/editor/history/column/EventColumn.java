/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.history.column;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workflow.WorkState;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class EventColumn extends XViewerValueColumn {

   private static EventColumn instance = new EventColumn();

   public static EventColumn getInstance() {
      return instance;
   }

   public EventColumn() {
      super("ats.history.Event", "Event", 290, XViewerAlign.Left, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EventColumn copy() {
      EventColumn newXCol = new EventColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof String) {
            return (String) element;
         }
         if (element instanceof Change) {
            Change change = (Change) element;
            if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentState.getName())) {
               return processCurrentStateChange(change);
            }
            if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentStateType.getName())) {
               if (change.getIsValue().equals(StateType.Completed.name())) {
                  return "Completed";
               } else if (change.getIsValue().equals(StateType.Cancelled.name())) {
                  return "Cancelled";
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      if (col.getName().equals("Event")) {
         String text = getColumnText(element, EventColumn.getInstance(), columnIndex);
         if (text.startsWith("Assigned") || text.equals("UnAssigned")) {
            return ImageManager.getImage(FrameworkImage.USERS);
         } else if (text.startsWith("Statused")) {
            return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
         } else if (text.startsWith("Transition")) {
            return ImageManager.getImage(AtsImage.TRANSITION);
         } else if (text.startsWith("Created")) {
            return ImageManager.getImage(AtsImage.ACTION);
         } else if (text.startsWith("Completed")) {
            return ImageManager.getImage(FrameworkImage.DOT_GREEN);
         } else if (text.startsWith("Cancelled")) {
            return ImageManager.getImage(FrameworkImage.X_RED);
         }
      }
      return null;
   }

   public String processCurrentStateChange(Change change) {
      try {
         WorkState was = AtsClientService.get().getWorkStateFactory().fromStoreStr(change.getWasValue());
         WorkState is = AtsClientService.get().getWorkStateFactory().fromStoreStr(change.getIsValue());
         if (change.getWasValue().equals("")) {
            return "Created in [" + is.getName() + "] state";
         } else if (!was.getName().equals(is.getName())) {
            return "Transition from [" + was.getName() + "] to [" + is.getName() + "]";
         }
         if (was.getName().equals(
            is.getName()) && (was.getPercentComplete() != is.getPercentComplete() || !getHoursSpentStr(was).equals(
               getHoursSpentStr(is)))) {
            return "Statused [" + is.getName() + "] to: " + is.getPercentComplete() + "% and " + getHoursSpent(
               is) + " hrs";
         }
         Collection<? extends IAtsUser> wasAssignees = was.getAssignees();
         Collection<? extends IAtsUser> isAssignees = is.getAssignees();
         Set<IAtsUser> assigned = new HashSet<>();
         Set<IAtsUser> unAssigned = new HashSet<>();
         for (IAtsUser isAssignee : isAssignees) {
            if (!wasAssignees.contains(isAssignee)) {
               assigned.add(isAssignee);
            }
         }
         for (IAtsUser wasAssignee : wasAssignees) {
            if (!isAssignees.contains(wasAssignee)) {
               unAssigned.add(wasAssignee);
            }
         }
         if (unAssigned.size() > 0) {
            return "UnAssigned [" + is.getName() + "] removed " + AtsObjects.toString("; ", unAssigned);
         }
         if (assigned.size() > 0) {
            return "Assigned [" + is.getName() + "] to " + AtsObjects.toString("; ", assigned);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "";
   }

   private String getHoursSpent(WorkState state) {
      return Strings.isValid(getHoursSpentStr(state)) ? getHoursSpentStr(state) : "0";
   }

   public static String getHoursSpentStr(WorkState state) {
      return AtsUtil.doubleToI18nString(state.getHoursSpent(), true);
   }

}
