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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.osee.framework.ui.plugin.util.SelectionCountChangeListener;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.progress.UIJob;

public abstract class AbstractArtifactSearchViewPage extends Page implements ISearchResultPage {

   private class UpdateUIJob extends UIJob {

      public UpdateUIJob() {
         super("Error in running search criteria");
         setSystem(true);
      }

      public IStatus runInUIThread(IProgressMonitor monitor) {
         Control control = getControl();
         if (control == null || control.isDisposed()) {
            // disposed the control while the UI was posted.
            return Status.OK_STATUS;
         }
         runBatchedUpdates();
         if (hasMoreUpdates() || isQueryRunning()) {
            schedule(500);
         } else {
            aIsUIUpdateScheduled = false;
            turnOnDecoration();
         }
         aViewPart.updateLabel();
         return Status.OK_STATUS;
      }

      /*
       * Undocumented for testing only. Used to find UpdateUIJobs.
       */
      public boolean belongsTo(Object family) {
         return family == AbstractArtifactSearchViewPage.this;
      }

   }

   private class SelectionProviderAdapter implements ISelectionProvider, ISelectionChangedListener {
      private ArrayList<ISelectionChangedListener> aListeners = new ArrayList<ISelectionChangedListener>(5);

      public void addSelectionChangedListener(ISelectionChangedListener listener) {
         aListeners.add(listener);
      }

      public ISelection getSelection() {
         return aViewer.getSelection();
      }

      public void removeSelectionChangedListener(ISelectionChangedListener listener) {
         aListeners.remove(listener);
      }

      public void setSelection(ISelection selection) {
         aViewer.setSelection(selection);
      }

      public void selectionChanged(SelectionChangedEvent event) {
         // forward to my listeners
         SelectionChangedEvent wrappedEvent = new SelectionChangedEvent(this, event.getSelection());
         for (Iterator<?> listeners = aListeners.iterator(); listeners.hasNext();) {
            ISelectionChangedListener listener = (ISelectionChangedListener) listeners.next();
            listener.selectionChanged(wrappedEvent);
         }
      }
   }

   private transient boolean aIsUIUpdateScheduled = false;

   private ISearchResult aInput;

   private IQueryListener aQueryListener;
   private PageBook aPagebook;

   private Control aBusyLabel;
   private boolean aIsBusyShown;

   private Composite aViewerContainer;
   private SelectionProviderAdapter aViewerAdapter;

   private ISearchResultViewPart aViewPart;
   private StructuredViewer aViewer;

   @SuppressWarnings("unchecked")
   private Set aBatchedUpdates;

   private String aId;

   @Override
   public void createControl(Composite parent) {
      aQueryListener = createQueryListener();

      aPagebook = new PageBook(parent, SWT.NULL);
      aPagebook.setLayoutData(new GridData(GridData.FILL_BOTH));

      aBusyLabel = createBusyControl();

      aViewerContainer = new Composite(aPagebook, SWT.NULL);
      aViewerContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
      aViewerContainer.setSize(100, 100);
      aViewerContainer.setLayout(new FillLayout());

      aViewerAdapter = new SelectionProviderAdapter();
      getSite().setSelectionProvider(aViewerAdapter);

      createViewer(aViewerContainer);
      showBusyLabel(aIsBusyShown);
      NewSearchUI.addQueryListener(aQueryListener);
   }

   private IQueryListener createQueryListener() {
      return new IQueryListener() {
         public void queryAdded(ISearchQuery query) {
            // ignore
         }

         public void queryRemoved(ISearchQuery query) {
            // ignore
         }

         public void queryStarting(final ISearchQuery query) {
            final Runnable runnable1 = new Runnable() {
               public void run() {
                  updateBusyLabel();
                  AbstractArtifactSearchResult result = getInput();

                  if (result == null || !result.getQuery().equals(query)) {
                     return;
                  }
                  turnOffDecoration();
                  scheduleUIUpdate();
               }
            };
            asyncExec(runnable1);
         }

         public void queryFinished(final ISearchQuery query) {
            final Runnable runnable2 = new Runnable() {
               public void run() {
                  updateBusyLabel();
                  AbstractArtifactSearchResult result = getInput();

                  if (result == null || !result.getQuery().equals(query)) {
                     return;
                  }
               }
            };
            asyncExec(runnable2);
         }
      };
   }

   private Control createBusyControl() {
      Table busyLabel = new Table(aPagebook, SWT.NONE);
      TableItem item = new TableItem(busyLabel, SWT.NONE);
      item.setText("Busy");
      busyLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      return busyLabel;
   }

   private void createViewer(Composite parent) {
      TableViewer viewer = new TableViewer(parent, SWT.MULTI);
      aViewer = viewer;
      configureTableViewer(viewer);

      viewer.addSelectionChangedListener(new SelectionCountChangeListener(aViewPart.getViewSite()));
   }

   private void showBusyLabel(boolean shouldShowBusy) {
      if (shouldShowBusy)
         aPagebook.showPage(aBusyLabel);
      else
         aPagebook.showPage(aViewerContainer);
   }

   private void updateBusyLabel() {
      AbstractArtifactSearchResult result = getInput();
      boolean shouldShowBusy =
            result != null && NewSearchUI.isQueryRunning(result.getQuery()) && result.getMatchCount() == 0;
      if (shouldShowBusy == aIsBusyShown) return;
      aIsBusyShown = shouldShowBusy;
      showBusyLabel(aIsBusyShown);
   }

   /**
    * Returns the currently shown result.
    * 
    * @return the previously set result or <code>null</code>
    * @see AbstractTextSearchViewPage#setInput(ISearchResult, Object)
    */
   public AbstractArtifactSearchResult getInput() {
      return (AbstractArtifactSearchResult) aInput;
   }

   private void turnOffDecoration() {
      IBaseLabelProvider lp = aViewer.getLabelProvider();
      if (lp instanceof DecoratingLabelProvider) {
         ((DecoratingLabelProvider) lp).setLabelDecorator(null);
      }
   }

   private void turnOnDecoration() {
      IBaseLabelProvider lp = aViewer.getLabelProvider();
      if (lp instanceof DecoratingLabelProvider) {
         ((DecoratingLabelProvider) lp).setLabelDecorator(PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

      }
   }

   private synchronized void scheduleUIUpdate() {
      if (!aIsUIUpdateScheduled) {
         aIsUIUpdateScheduled = true;
         new UpdateUIJob().schedule();
      }
   }

   private synchronized void runBatchedUpdates() {
      if (aBatchedUpdates != null) {
         elementsChanged(aBatchedUpdates.toArray());
         aBatchedUpdates.clear();
         updateBusyLabel();
      }
   }

   private synchronized boolean hasMoreUpdates() {
      if (aBatchedUpdates != null) return aBatchedUpdates.size() > 0;
      return false;
   }

   private boolean isQueryRunning() {
      AbstractArtifactSearchResult result = getInput();
      if (result != null) {
         return NewSearchUI.isQueryRunning(result.getQuery());
      }
      return false;
   }

   private void asyncExec(final Runnable runnable) {
      final Control control = getControl();
      if (control != null && !control.isDisposed()) {
         Display currentDisplay = Display.getCurrent();
         if (currentDisplay == null || !currentDisplay.equals(control.getDisplay()))
            // meaning we're not executing on the display thread of the
            // control
            control.getDisplay().asyncExec(new Runnable() {
               public void run() {
                  if (control != null && !control.isDisposed()) runnable.run();
               }
            });
         else
            runnable.run();
      }
   }

   @Override
   public Control getControl() {
      return aPagebook;
   }

   @Override
   public void setFocus() {
      Control control = aViewer.getControl();
      if (control != null && !control.isDisposed()) control.setFocus();
   }

   public Object getUIState() {
      return aViewer.getSelection();
   }

   public void setInput(ISearchResult search, Object viewState) {
      disconnectViewer();

      aInput = search;
      if (search != null) {
         aViewer.setInput(search);
         if (viewState instanceof ISelection) {
            aViewer.setSelection((ISelection) viewState, true);
         }
      }

      updateBusyLabel();
      turnOffDecoration();
      scheduleUIUpdate();
   }

   private ISearchResult disconnectViewer() {
      ISearchResult result = (ISearchResult) aViewer.getInput();
      aViewer.setInput(null);
      return result;
   }

   public void setViewPart(ISearchResultViewPart part) {
      aViewPart = part;
   }

   public void restoreState(IMemento memento) {
   }

   public void saveState(IMemento memento) {
   }

   public void setID(String id) {
      aId = id;
   }

   public String getID() {
      return aId;
   }

   public String getLabel() {
      AbstractArtifactSearchResult result = getInput();
      if (result == null) return ""; //$NON-NLS-1$
      return result.getLabel();
   }

   public StructuredViewer getViewer() {
      return aViewer;
   }

   /**
    * @param viewer the viewer to be configured
    */
   protected abstract void configureTableViewer(TableViewer viewer);

   /**
    * This method is called whenever the set of matches for the given elements changes. This method is guaranteed to be
    * called in the UI thread. Note that this notification is asynchronous. i.e. further changes may have occurred by
    * the time this method is called. They will be described in a future call.
    * 
    * @param objects array of objects that has to be refreshed
    */
   protected abstract void elementsChanged(Object[] objects);
}
