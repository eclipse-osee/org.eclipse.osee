/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.editor.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageMethodEnum;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class CoverageParameters {

   private final CoveragePackageBase coveragePackageBase;
   private Collection<CoverageMethodEnum> coverageMethods = new ArrayList<CoverageMethodEnum>();
   private boolean showAll = false;
   private String notes;
   private User assignee;

   public CoverageParameters(CoveragePackageBase coveragePackageBase) {
      this.coveragePackageBase = coveragePackageBase;
   }

   /**
    * Returns a collection of ICoverage items that matched and a collection of all top level ICoverage parents
    */
   public Pair<Set<ICoverage>, Set<ICoverage>> performSearchGetResults() throws OseeCoreException {
      Set<ICoverage> items = new HashSet<ICoverage>();
      for (ICoverage coverageItem : coveragePackageBase.getChildren(false)) {
         performSearchGetResults(items, coverageItem);
      }
      Set<ICoverage> parents = new HashSet<ICoverage>();
      for (ICoverage coverage : items) {
         parents.add(CoverageUtil.getTopLevelCoverageUnit(coverage));
      }
      return new Pair<Set<ICoverage>, Set<ICoverage>>(items, parents);
   }

   public void performSearchGetResults(Set<ICoverage> items, ICoverage item) throws OseeCoreException {
      Collection<CoverageMethodEnum> coverageMethods = getSelectedCoverageMethods();
      User assignee = getAssignee();
      if (isShowAll()) {
         items.add(item);
      } else {
         boolean add = true;
         if (assignee != null && !CoverageUtil.getCoverageItemUsers(item).contains(assignee)) {
            add = false;
         }
         if (add && coverageMethods.size() > 0 && (item instanceof CoverageItem)) {
            CoverageItem coverageItem = (CoverageItem) item;
            if (!coverageMethods.contains(coverageItem.getCoverageMethod())) {
               add = false;
            }
         }
         // don't add anything but coverage items if coverage methods specified
         if (add && coverageMethods.size() > 0 && (!(item instanceof CoverageItem))) {
            add = false;
         }
         if (Strings.isValid(getNotesStr())) {
            if (item instanceof CoverageUnit) {
               if (!Strings.isValid(((CoverageUnit) item).getNotes())) {
                  add = false;
               }
               if (((CoverageUnit) item).getNotes() == null || !((CoverageUnit) item).getNotes().contains(getNotesStr())) {
                  add = false;
               }
            } else {
               add = false;
            }
         }
         if (add) {
            items.add(item);
         }
      }
      for (ICoverage child : item.getChildren()) {
         performSearchGetResults(items, child);
      }
   }

   private void addAllParents(Set<ICoverage> items, ICoverage item) {
      if (item.getParent() != null) {
         items.add(item.getParent());
         addAllParents(items, item.getParent());
      }
   }

   public String getSelectedName(/*SearchType searchType*/) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      if (isShowAll()) {
         sb.append(" - Show All");
      }
      if (getAssignee() != null) {
         sb.append(" - Assignee: " + getAssignee());
      }
      if (getSelectedCoverageMethods().size() > 1) {
         sb.append(" - Coverage Method: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ",
               getSelectedCoverageMethods()));
      }
      return "Coverage Items " + sb.toString();
   }

   public boolean isShowAll() {
      return showAll;
   }

   public String getNotesStr() {
      return notes;
   }

   public User getAssignee() {
      return assignee;
   }

   public Collection<CoverageMethodEnum> getSelectedCoverageMethods() {
      return coverageMethods;
   }

   public Result isParameterSelectionValid() throws OseeCoreException {
      try {
         if (isShowAll()) {
            if (getSelectedCoverageMethods().size() > 0) {
               return new Result("Can't have Show All and Coverage Methods");
            }
            if (getAssignee() != null) {
               return new Result("Can't have Show All and Assignee selected");
            }
            if (Strings.isValid(getNotesStr())) {
               return new Result("Can't have Show All and Notes");
            }
         }
         if (!isShowAll()) {
            if (getSelectedCoverageMethods().size() == 0) {
               return new Result("You must select at least one Coverage Method");
            }
         }
         return Result.TrueResult;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return new Result("Exception: " + ex.getLocalizedMessage());
      }
   }

   public CoveragePackageBase getCoveragePackageBase() {
      return coveragePackageBase;
   }

   public Collection<CoverageMethodEnum> getCoverageMethods() {
      return coverageMethods;
   }

   public void setCoverageMethods(Collection<CoverageMethodEnum> coverageMethods) {
      this.coverageMethods = coverageMethods;
   }

   public String getNotes() {
      return notes;
   }

   public void setNotes(String notes) {
      this.notes = notes;
   }

   public void setShowAll(boolean showAll) {
      this.showAll = showAll;
   }

   public void setAssignee(User assignee) {
      this.assignee = assignee;
   }

}
