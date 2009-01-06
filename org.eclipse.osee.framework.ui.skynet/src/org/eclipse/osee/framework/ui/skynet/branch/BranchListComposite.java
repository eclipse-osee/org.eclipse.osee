/*
 * Created on Oct 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.branch;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact;
import org.eclipse.osee.framework.ui.swt.ColumnSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Donald G. Dunne
 */
public class BranchListComposite implements IBranchEventListener {

   private static final String[] columnNames = {"", "Short Name", "Time Stamp", "Author", "Comment"};
   private TreeViewer branchTable;
   private TreeEditor myTreeEditor;
   private Text filterText;
   private BranchNameFilter nameFilter;
   private FavoritesSorter sorter;
   private final Collection<Branch> branches;

   public BranchListComposite(Composite parent) {
      this(null, parent);
   }

   public BranchListComposite(Collection<Branch> branches, Composite parent) {
      this.branches = branches;
      try {

         if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

         parent.setLayout(new GridLayout());
         parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         createTableViewer(parent);
         createColumns();
         createFilter(parent);

         myTreeEditor = new TreeEditor(branchTable.getTree());
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;

         forcePopulateView();
         OseeEventManager.addListener(this);
      } catch (OseeCoreException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   /**
    * @return the myTreeEditor
    */
   public TreeEditor getMyTreeEditor() {
      return myTreeEditor;
   }

   private void createColumns() {
      Tree tree = branchTable.getTree();

      tree.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setWidth(400);
      column1.setText(columnNames[0]);
      column1.addSelectionListener(new ColumnSelectionListener(0));

      TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
      column2.setWidth(100);
      column2.setText(columnNames[1]);
      column2.addSelectionListener(new ColumnSelectionListener(1));

      TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
      column3.setWidth(150);
      column3.setText(columnNames[2]);
      column3.addSelectionListener(new ColumnSelectionListener(2));

      TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
      column4.setWidth(150);
      column4.setText(columnNames[3]);
      column4.addSelectionListener(new ColumnSelectionListener(3));

      TreeColumn column5 = new TreeColumn(tree, SWT.LEFT);
      column5.setWidth(300);
      column5.setText(columnNames[4]);
      column5.addSelectionListener(new ColumnSelectionListener(4));
   }

   private void createFilter(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      Label label = new Label(composite, SWT.NONE);
      label.setText("Filter:");

      filterText = new Text(composite, SWT.BORDER);
      filterText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            nameFilter.setContains(((Text) e.getSource()).getText());
            branchTable.refresh();
         }
      });
      filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
   }

   public boolean isFiltering() {
      return nameFilter.isFiltering();
   }

   private class ColumnSelectionListener extends SelectionAdapter {
      private final int index;

      /**
       * @param index
       */
      public ColumnSelectionListener(int index) {
         super();
         this.index = index;
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         sorter.setColumnToSort(index);
         branchTable.refresh();
      }
   }

   /**
    * @return the branchTable
    */
   public TreeViewer getBranchTable() {
      return branchTable;
   }

   /**
    * @return the filterText
    */
   public Text getFilterText() {
      return filterText;
   }

   private void createTableViewer(Composite parent) {
      ITableLabelProvider labelProvider = new BranchLabelProvider(null);

      branchTable = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);
      branchTable.setContentProvider(new BranchContentProvider());
      branchTable.setLabelProvider(labelProvider);
      branchTable.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sorter = new FavoritesSorter(labelProvider);
      branchTable.setSorter(sorter);

      nameFilter = new BranchNameFilter();
      branchTable.addFilter(nameFilter);
      setFavoritesFirst(true);
   }

   class BranchArtifact implements IBranchArtifact {

      private final Branch branch;

      public BranchArtifact(Branch branch) {
         this.branch = branch;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getArtifact()
       */
      public Artifact getArtifact() {
         try {
            return branch.getAssociatedArtifact();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.IBranchArtifact#getWorkingBranch()
       */
      public Branch getWorkingBranch() throws OseeCoreException {
         return branch;
      }
   }

   public void refresh() {
      if (branchTable != null && !branchTable.getTree().isDisposed()) {
         branchTable.refresh();
      }
   }

   public void forcePopulateView() throws OseeCoreException {
      if (branchTable != null && !branchTable.getTree().isDisposed()) {
         BranchManager.refreshBranches();
         if (branches == null) {
            branchTable.setInput(BranchManager.getInstance());
         } else {
            branchTable.setInput(branches);
         }
      }
   }

   public void setFocus() {
      if (branchTable != null) branchTable.getControl().setFocus();
   }

   private class BranchNameFilter extends ViewerFilter {
      private String contains = null;
      private boolean flat = false;

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
         if (!isFiltering()) return true;

         Object backingData = ((JobbedNode) element).getBackingData();
         if (backingData instanceof Branch) {
            return descendantBranchContains((Branch) backingData);
         }
         return true;
      }

      private boolean descendantBranchContains(Branch branch) {
         if (branch.getBranchName().toLowerCase().contains(contains.toLowerCase())) {
            return true;
         }
         if (!flat) {
            // Recurse for hierarchical display
            try {
               for (Branch childBranch : branch.getChildBranches()) {
                  if (descendantBranchContains(childBranch)) {
                     return true;
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               return true;
            }
         }

         return false;
      }

      /**
       * @param contains The contains to set.
       */
      public void setContains(String contains) {
         this.contains = contains;
      }

      /**
       * @return Returns the contains.
       */
      public String getContains() {
         return contains;
      }

      public boolean isFiltering() {
         return contains != null && contains.length() > 0;
      }

      /**
       * @param flat the flat to set
       */
      public void setFlat(boolean flat) {
         this.flat = flat;
      }
   }

   private class FavoritesSorter extends ColumnSorter {
      private boolean favoritesFirst;

      /**
       * @param labelProvider
       */
      public FavoritesSorter(ITableLabelProvider labelProvider) {
         super(labelProvider);

         this.favoritesFirst = false;
      }

      @Override
      public int compare(Viewer viewer, Object o1, Object o2) {
         Object backing1 = ((JobbedNode) o1).getBackingData();
         Object backing2 = ((JobbedNode) o2).getBackingData();

         if (favoritesFirst && backing1 instanceof Branch && backing2 instanceof Branch) {
            try {
               User user = UserManager.getUser();
               boolean fav1 = user.isFavoriteBranch((Branch) backing1);
               boolean fav2 = user.isFavoriteBranch((Branch) backing2);

               if (fav1 ^ fav2) {
                  return fav1 ? -1 : 1;
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         } else if (backing1 instanceof Branch && !(backing2 instanceof Branch)) {
            return -1;
         } else if (!(backing1 instanceof Branch) && backing2 instanceof Branch) {
            return 1;
         }
         return super.compare(viewer, o1, o2);
      }

      /**
       * @return Returns the favoritesFirst.
       */
      public boolean isFavoritesFirst() {
         return favoritesFirst;
      }

      /**
       * @param favoritesFirst The favoritesFirst to set.
       */
      public void setFavoritesFirst(boolean favoritesFirst) {
         this.favoritesFirst = favoritesFirst;
      }
   }

   public void reveal(Branch branch) {
      for (Object obj : ((BranchContentProvider) branchTable.getContentProvider()).getElements(null)) {
         if (((JobbedNode) obj).getBackingData() == branch) {
            branchTable.reveal(obj);
            branchTable.setSelection(new StructuredSelection(obj), true);
            return;
         }
      }
   }

   public void disposeComposite() {
      OseeEventManager.removeListener(this);
   }

   public void setShowTransactions(boolean showTransactions) {
      if (branchTable != null && branchTable.getContentProvider() != null) {

         BranchContentProvider myBranchContentProvider = (BranchContentProvider) branchTable.getContentProvider();
         myBranchContentProvider.setShowTransactions(showTransactions);
         myBranchContentProvider.refresh();
         refresh();
      }
   }

   public void setShowMergeBranches(boolean showMergeBranches) {
      if (branchTable != null && branchTable.getContentProvider() != null) {

         BranchContentProvider myBranchContentProvider = (BranchContentProvider) branchTable.getContentProvider();
         myBranchContentProvider.setShowMergeBranches(showMergeBranches);
         myBranchContentProvider.refresh();
         refresh();
      }
   }

   public void setFavoritesFirst(boolean set) {
      if (sorter != null) {
         sorter.setFavoritesFirst(set);
         branchTable.refresh();
      }
   }

   public void setPresentation(boolean flat) {
      if (branchTable != null && branchTable.getContentProvider() != null) {
         BranchContentProvider provider = (BranchContentProvider) branchTable.getContentProvider();

         // No effect if going to the same state
         if (provider.isShowChildBranchesAtMainLevel() != flat || provider.isShowChildBranchesUnderParents() != !flat) {
            nameFilter.setFlat(flat);
            provider.setShowChildBranchesAtMainLevel(flat);
            provider.setShowChildBranchesUnderParents(!flat);

            provider.refresh();
         }
         refresh();
      }
   }

   public boolean isFavoritesFirst() {
      return sorter.isFavoritesFirst();
   }

   public void setDefaultBranch(Branch newDefaultBranch) throws OseeCoreException {
      Branch oldDefaultBranch = BranchManager.getDefaultBranch();
      BranchManager.setDefaultBranch(newDefaultBranch);
      branchTable.update(new Object[] {oldDefaultBranch, newDefaultBranch}, null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged || branchModType == BranchEventType.Renamed) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               refresh();
            }
         });
      } else if (branchModType == BranchEventType.Added || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               try {
                  forcePopulateView();
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }
}
