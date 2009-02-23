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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchResult;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
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
public class ArtifactSearchPage extends AbstractArtifactSearchViewPage implements IAdaptable, IRebuildMenuListener, IFrameworkTransactionEventListener, IArtifactsPurgedEventListener {
   private static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.ArtifactSearchView";

   protected static final Match[] EMPTY_MATCH_ARRAY = new Match[0];
   public static class DecoratorIgnoringViewerSorter extends ViewerComparator {
      private final ILabelProvider fLabelProvider;

      public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
         fLabelProvider = labelProvider;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
       */
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

      @SuppressWarnings("unchecked")
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
         if (name1 == null) name1 = "";//$NON-NLS-1$
         if (name2 == null) name2 = "";//$NON-NLS-1$
         return getComparator().compare(name1, name2);
      }
   }

   private static final String KEY_LIMIT = "org.eclipse.search.resultpage.limit"; //$NON-NLS-1$
   private static final int DEFAULT_ELEMENT_LIMIT = 1000;

   private IArtifactSearchContentProvider fContentProvider;
   private ArtifactDecorator artifactDecorator;
   private ISelectionProvider selectionProvider;

   public ArtifactSearchPage() {
      setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));
   }

   public void setElementLimit(Integer elementLimit) {
      super.setElementLimit(elementLimit);
      int limit = elementLimit.intValue();
      getSettings().put(KEY_LIMIT, limit);
   }

   public StructuredViewer getViewer() {
      return super.getViewer();
   }

   private void addDragAdapters(StructuredViewer viewer) {
      new SearchDragAndDrop(viewer.getControl(), VIEW_ID);
   }

   private ArtifactDecorator getArtifactDecorator() {
      if (artifactDecorator == null) {
         artifactDecorator = new ArtifactDecorator(SkynetGuiPlugin.ARTIFACT_SEARCH_RESULTS_ATTRIBUTES_PREF);
         artifactDecorator.addActions(getSite().getActionBars().getMenuManager());
      }
      return artifactDecorator;
   }

   private ISelectionProvider getSearchSelectionProvider() {
      if (selectionProvider == null) {
         selectionProvider = new SearchSelectionProvider();
      }
      return selectionProvider;
   }

   protected void configureTableViewer(TableViewer viewer) {
      viewer.setUseHashlookup(true);
      ArtifactDecorator decorator = getArtifactDecorator();
      decorator.setViewer(viewer);
      ArtifactSearchLabelProvider innerLabelProvider = new ArtifactSearchLabelProvider(this, decorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(innerLabelProvider));
      viewer.setContentProvider(new ArtifactTableContentProvider(this));
      viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
      fContentProvider = (IArtifactSearchContentProvider) viewer.getContentProvider();
      addDragAdapters(viewer);
   }

   protected void configureTreeViewer(TreeViewer viewer) {
      viewer.setUseHashlookup(true);
      ArtifactDecorator decorator = getArtifactDecorator();
      decorator.setViewer(viewer);
      ArtifactSearchLabelProvider innerLabelProvider = new ArtifactSearchLabelProvider(this, decorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(innerLabelProvider));
      viewer.setContentProvider(new ArtifactTreeContentProvider(this, viewer));
      viewer.setComparator(new DecoratorIgnoringViewerSorter(innerLabelProvider));
      fContentProvider = (IArtifactSearchContentProvider) viewer.getContentProvider();
      addDragAdapters(viewer);
   }

   protected void fillContextMenu(IMenuManager mgr) {
      mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      getSite().setSelectionProvider(getSearchSelectionProvider());

      mgr.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new Action("Open Search Preferences") {
         public void run() {
            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
            PreferencesUtil.createPreferenceDialogOn(shell, "org.eclipse.search.preferences.SearchPreferencePage",
                  null, null).open();
         }
      });
   }

   public void setViewPart(ISearchResultViewPart part) {
      super.setViewPart(part);
   }

   public void init(IPageSite site) {
      super.init(site);
      setID(VIEW_ID);
      OseeContributionItem.addTo(getSite(), false);
      getSite().getActionBars().updateActionBars();
      OseeEventManager.addListener(this);
   }

   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   protected void elementsChanged(Object[] objects) {
      if (fContentProvider != null) {
         fContentProvider.elementsChanged(objects);
      }
   }

   protected void clear() {
      if (fContentProvider != null) {
         fContentProvider.clear();
      }
   }

   public void restoreState(IMemento memento) {
      super.restoreState(memento);

      int elementLimit = DEFAULT_ELEMENT_LIMIT;
      try {
         elementLimit = getSettings().getInt(KEY_LIMIT);
      } catch (NumberFormatException e) {
      }
      if (memento != null) {
         Integer value = memento.getInteger(KEY_LIMIT);
         if (value != null) elementLimit = value.intValue();
      }
      setElementLimit(new Integer(elementLimit));
   }

   public void saveState(IMemento memento) {
      super.saveState(memento);
      memento.putInteger(KEY_LIMIT, getElementLimit().intValue());
   }

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      return null;
   }

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
      if (result == null) return EMPTY_MATCH_ARRAY;
      Match[] matches = result.getMatches(element);
      if (result.getActiveMatchFilters() == null) return matches;

      int count = 0;
      for (int i = 0; i < matches.length; i++) {
         if (matches[i].isFiltered())
            matches[i] = null;
         else
            count++;
      }
      if (count == matches.length) return matches;

      Match[] filteredMatches = new Match[count];
      for (int i = 0, k = 0; i < matches.length; i++) {
         if (matches[i] != null) filteredMatches[k++] = matches[i];
      }
      return filteredMatches;
   }

   @SuppressWarnings("unchecked")
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

   @SuppressWarnings("unchecked")
   protected void evaluateInternalChangedElements(Match[] matches, Set changedElements) {
      for (int i = 0; i < matches.length; i++) {
         changedElements.add(matches[i].getElement());
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#handleOpen(org.eclipse.jface.viewers.OpenEvent)
    */
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

      Set<Artifact> artifacts = new HashSet<Artifact>();
      for (Object object : objects) {
         Artifact toAdd = null;
         if (object instanceof AttributeLineElement) {
            toAdd = (Artifact) ((IAdaptable) object).getAdapter(Artifact.class);
            artifacts.add(toAdd);
         } else if (object instanceof IAdaptable) {
            toAdd = (Artifact) ((IAdaptable) object).getAdapter(Artifact.class);
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener#rebuildMenu()
    */
   @Override
   public void rebuildMenu() {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(final Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (getViewer() != null) {
               AbstractArtifactSearchResult results = getInput();
               if (results != null) {
                  for (Artifact artifact : transData.cacheDeletedArtifacts) {
                     for (Match match : results.getMatches(artifact)) {
                        results.removeMatch(match);
                     }
                  }
                  getViewer().refresh();
               }
            }
         }
      });

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               if (getViewer() != null) {
                  AbstractArtifactSearchResult results = getInput();
                  if (results != null) {
                     for (Artifact artifact : loadedArtifacts.getLoadedArtifacts()) {
                        for (Match match : results.getMatches(artifact)) {
                           results.removeMatch(match);
                        }
                     }
                     getViewer().refresh();
                  }
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });

   }

   private class SearchSelectionProvider implements ISelectionProvider {
      public void addSelectionChangedListener(ISelectionChangedListener listener) {

      }

      public ISelection getSelection() {
         return new ArtifactSelection(getSelectedArtifacts());
      }

      public void removeSelectionChangedListener(ISelectionChangedListener listener) {

      }

      public void setSelection(ISelection selection) {

      }
   }

   private final class ArtifactSelection implements IStructuredSelection {
      private final Collection<Artifact> collection;

      private ArtifactSelection(Collection<Artifact> collection) {
         this.collection = collection;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ISelection#isEmpty()
       */
      @Override
      public boolean isEmpty() {
         return collection.isEmpty();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
       */
      @Override
      public Object getFirstElement() {
         return collection.size() == 0 ? null : iterator().next();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
       */
      @SuppressWarnings("unchecked")
      @Override
      public Iterator iterator() {
         return collection.iterator();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredSelection#size()
       */
      @Override
      public int size() {
         // TODO Auto-generated method stub
         return collection.size();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
       */
      @Override
      public Object[] toArray() {
         // TODO Auto-generated method stub
         return collection.toArray();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
       */
      @SuppressWarnings("unchecked")
      @Override
      public List toList() {
         return new ArrayList<Artifact>(collection);
      }
   }
}