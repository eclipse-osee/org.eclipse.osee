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
package org.eclipse.osee.framework.ui.skynet.search.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IBranchProvider;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.IPageSite;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactSearchPage extends AbstractArtifactSearchViewPage implements Adaptable, IRebuildMenuListener, IArtifactEventListener, IBranchProvider {
   private static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.ArtifactSearchView";

   protected static final Match[] EMPTY_MATCH_ARRAY = new Match[0];
   public static class DecoratorIgnoringViewerSorter extends ViewerComparator {
      private final ILabelProvider fLabelProvider;

      public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
         fLabelProvider = labelProvider;
      }

      @Override
      public int category(Object element) {
         if (element instanceof Artifact) {
            return 4;
         } else if (element instanceof AttributeLineElement) {
            return 3;
         } else if (element instanceof AttributeMatch) {
            return 2;
         }
         return 1;
      }

      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
         int cat1 = category(e1);
         int cat2 = category(e2);

         if (cat1 != cat2) {
            return cat1 - cat2;
         }

         if (e1 instanceof AttributeLineElement && e2 instanceof AttributeLineElement) {
            AttributeLineElement m1 = (AttributeLineElement) e1;
            AttributeLineElement m2 = (AttributeLineElement) e2;
            return m1.getOffset() - m2.getOffset();
         }

         String name1 = fLabelProvider.getText(e1);
         String name2 = fLabelProvider.getText(e2);
         if (name1 == null) {
            name1 = "";//$NON-NLS-1$
         }
         if (name2 == null) {
            name2 = "";//$NON-NLS-1$
         }
         return getComparator().compare(name1, name2);
      }
   }

   private static final String KEY_LIMIT = "org.eclipse.search.resultpage.limit"; //$NON-NLS-1$
   private static final int DEFAULT_ELEMENT_LIMIT = 1000;

   private IArtifactSearchContentProvider fContentProvider;
   private ISelectionProvider selectionProvider;
   private final ArtifactDecorator artifactDecorator =
      new ArtifactDecorator(Activator.ARTIFACT_SEARCH_RESULTS_ATTRIBUTES_PREF);

   public ArtifactSearchPage() {
      setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));
   }

   @Override
   public void setElementLimit(Integer elementLimit) {
      super.setElementLimit(elementLimit);
      int limit = elementLimit.intValue();
      getSettings().put(KEY_LIMIT, limit);
   }

   @Override
   public StructuredViewer getViewer() {
      return super.getViewer();
   }

   private void addDragAdapters(StructuredViewer viewer) {
      new SearchDragAndDrop(viewer.getControl(), VIEW_ID);
   }

   @Override
   public void createControl(Composite parent) {
      super.createControl(parent);
      artifactDecorator.addActions(getSite().getActionBars().getMenuManager(), this);
   }

   private ISelectionProvider getSearchSelectionProvider() {
      if (selectionProvider == null) {
         selectionProvider = new SearchSelectionProvider();
      }
      return selectionProvider;
   }

   @Override
   protected void configureTableViewer(TableViewer viewer) {
      viewer.setUseHashlookup(true);
      artifactDecorator.setViewer(viewer);

      ArtifactSearchLabelProvider innerLabelProvider = new ArtifactSearchLabelProvider(this, artifactDecorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(innerLabelProvider));
      viewer.setContentProvider(new ArtifactTableContentProvider(this));
      viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
      fContentProvider = (IArtifactSearchContentProvider) viewer.getContentProvider();
      addDragAdapters(viewer);
   }

   @Override
   protected void configureTreeViewer(TreeViewer viewer) {
      viewer.setUseHashlookup(true);
      artifactDecorator.setViewer(viewer);

      ArtifactSearchLabelProvider innerLabelProvider = new ArtifactSearchLabelProvider(this, artifactDecorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(innerLabelProvider));
      viewer.setContentProvider(new ArtifactTreeContentProvider(this, viewer));
      viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
      fContentProvider = (IArtifactSearchContentProvider) viewer.getContentProvider();
      addDragAdapters(viewer);
   }

   @Override
   protected void fillContextMenu(IMenuManager mgr) {
      mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      getSite().setSelectionProvider(getSearchSelectionProvider());

      mgr.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new Action("Open Search Preferences") {
         @Override
         public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.search.preferences.SearchPreferencePage", null,
               null).open();
         }
      });
   }

   @Override
   public void setViewPart(ISearchResultViewPart part) {
      super.setViewPart(part);
   }

   @Override
   public void init(IPageSite site) {
      super.init(site);
      setID(VIEW_ID);
      OseeStatusContributionItemFactory.addTo(getSite(), false);
      getSite().getActionBars().updateActionBars();
      OseeEventManager.addListener(this);
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   protected void elementsChanged(Object[] objects) {
      if (fContentProvider != null) {
         fContentProvider.elementsChanged(objects);
      }
   }

   @Override
   protected void clear() {
      if (fContentProvider != null) {
         fContentProvider.clear();
      }
   }

   @Override
   public void restoreState(IMemento memento) {
      super.restoreState(memento);

      int elementLimit = DEFAULT_ELEMENT_LIMIT;
      try {
         elementLimit = getSettings().getInt(KEY_LIMIT);
      } catch (NumberFormatException e) {
         // do nothing
      }
      if (memento != null) {
         Integer value = memento.getInteger(KEY_LIMIT);
         if (value != null) {
            elementLimit = value.intValue();
         }
      }
      setElementLimit(new Integer(elementLimit));
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento.putInteger(KEY_LIMIT, getElementLimit().intValue());
   }

   @Override
   public String getLabel() {
      String label = super.getLabel();
      StructuredViewer viewer = getViewer();
      if (viewer instanceof TableViewer) {
         TableViewer tv = (TableViewer) viewer;

         AbstractArtifactSearchResult result = getInput();
         if (result != null) {
            int itemCount = ((IStructuredContentProvider) tv.getContentProvider()).getElements(getInput()).length;
            if (showLineMatches()) {
               int matchCount = getInput().getMatchCount();
               if (itemCount < matchCount) {
                  return String.format("%s (showing %s of %s matches)", label, itemCount, matchCount);
               }
            } else {
               int fileCount = getInput().getElements().length;
               if (itemCount < fileCount) {
                  return String.format("%s (showing %s of %s files)", label, itemCount, fileCount);
               }
            }
         }
      }
      return label;
   }

   @Override
   public int getDisplayedMatchCount(Object element) {
      if (showLineMatches()) {
         if (element instanceof AttributeLineElement) {
            AttributeLineElement lineEntry = (AttributeLineElement) element;
            return lineEntry.getNumberOfMatches(getInput());
         }
         return 0;
      }
      return super.getDisplayedMatchCount(element);
   }

   @Override
   public Match[] getDisplayedMatches(Object element) {
      if (showLineMatches()) {
         if (element instanceof AttributeLineElement) {
            AttributeLineElement lineEntry = (AttributeLineElement) element;
            return lineEntry.getMatches(getInput());
         }
         return new Match[0];
      }
      return getInternalDisplayedMatches(element);
   }

   public Match[] getInternalDisplayedMatches(Object element) {
      AbstractArtifactSearchResult result = getInput();
      if (result == null) {
         return EMPTY_MATCH_ARRAY;
      }
      Match[] matches = result.getMatches(element);
      if (result.getActiveMatchFilters() == null) {
         return matches;
      }

      int count = 0;
      for (int i = 0; i < matches.length; i++) {
         if (matches[i].isFiltered()) {
            matches[i] = null;
         } else {
            count++;
         }
      }
      if (count == matches.length) {
         return matches;
      }

      Match[] filteredMatches = new Match[count];
      for (int i = 0, k = 0; i < matches.length; i++) {
         if (matches[i] != null) {
            filteredMatches[k++] = matches[i];
         }
      }
      return filteredMatches;
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   protected void evaluateChangedElements(Match[] matches, Set changedElements) {
      if (showLineMatches()) {
         for (int i = 0; i < matches.length; i++) {
            if (matches[i] instanceof AttributeMatch) {
               changedElements.add(((AttributeMatch) matches[i]).getLineElement());
            } else {
               changedElements.add(matches[i].getElement());
            }
         }
      } else {
         evaluateInternalChangedElements(matches, changedElements);
      }
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   protected void evaluateInternalChangedElements(Match[] matches, Set changedElements) {
      for (int i = 0; i < matches.length; i++) {
         changedElements.add(matches[i].getElement());
      }
   }

   @Override
   protected void handleOpen(OpenEvent event) {
      ArtifactDoubleClick.openArtifact(getSearchSelectionProvider().getSelection());
   }

   private boolean showLineMatches() {
      AbstractArtifactSearchResult input = getInput();
      return getLayout() == FLAG_LAYOUT_TREE && input != null && input.hasAttributeMatches();
   }

   private Collection<Artifact> getSelectedArtifacts() {
      IStructuredSelection selection = (IStructuredSelection) getViewer().getSelection();

      Object[] objects = selection.toArray();
      if (objects.length == 0) {
         return Collections.emptyList();
      }

      AbstractArtifactSearchResult resultInput = getInput();
      if (resultInput == null) {
         return Collections.emptyList();
      }

      Set<Artifact> artifacts = new LinkedHashSet<>();
      for (Object object : objects) {
         Artifact toAdd = null;
         if (object instanceof AttributeLineElement) {
            toAdd = ((IAdaptable) object).getAdapter(Artifact.class);
            artifacts.add(toAdd);
         } else if (object instanceof IAdaptable) {
            toAdd = ((IAdaptable) object).getAdapter(Artifact.class);
         } else if (object instanceof Match) {
            toAdd = (Artifact) ((Match) object).getElement();
         }
         if (toAdd != null) {
            artifacts.add(toAdd);
         }
      }
      return artifacts;
   }
   private class SearchDragAndDrop extends SkynetDragAndDrop {

      public SearchDragAndDrop(Control control, String viewId) {
         super(control, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         Collection<Artifact> artifacts = getSelectedArtifacts();
         return artifacts.toArray(new Artifact[artifacts.size()]);
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }
   }

   @Override
   public void rebuildMenu() {
      // Do Nothing
   }

   private class SearchSelectionProvider implements ISelectionProvider {
      @Override
      public void addSelectionChangedListener(ISelectionChangedListener listener) {
         // do nothing
      }

      @Override
      public ISelection getSelection() {
         return new ArtifactSelection(getSelectedArtifacts());
      }

      @Override
      public void removeSelectionChangedListener(ISelectionChangedListener listener) {
         // do nothing
      }

      @Override
      public void setSelection(ISelection selection) {
         // do nothing
      }
   }

   @Override
   public IOseeBranch getBranch() {
      if (getInput() != null && getInput().getArtifactResults() != null && !getInput().getArtifactResults().isEmpty()) {
         return getInput().getArtifactResults().get(0).getBranchToken();
      }
      return null;
   }

   private final class ArtifactSelection implements IStructuredSelection {
      private final Collection<Artifact> collection;

      private ArtifactSelection(Collection<Artifact> collection) {
         this.collection = collection;
      }

      @Override
      public boolean isEmpty() {
         return collection.isEmpty();
      }

      @Override
      public Object getFirstElement() {
         return collection.isEmpty() ? null : iterator().next();
      }

      @SuppressWarnings("rawtypes")
      @Override
      public Iterator iterator() {
         return collection.iterator();
      }

      @Override
      public int size() {
         return collection.size();
      }

      @Override
      public Object[] toArray() {
         return collection.toArray();
      }

      @SuppressWarnings("rawtypes")
      @Override
      public List toList() {
         return new ArrayList<Artifact>(collection);
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      final Collection<EventBasicGuidArtifact> deletedPurgedArts =
         artifactEvent.get(EventModType.Deleted, EventModType.Purged);
      if (deletedPurgedArts.isEmpty()) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (getViewer() != null) {
               AbstractArtifactSearchResult results = getInput();
               if (results != null) {
                  for (EventBasicGuidArtifact guidArt : deletedPurgedArts) {
                     for (Match match : results.getMatches(guidArt)) {
                        results.removeMatch(match);
                     }
                  }
                  getViewer().refresh();
               }
            }
         }
      });

   }
}