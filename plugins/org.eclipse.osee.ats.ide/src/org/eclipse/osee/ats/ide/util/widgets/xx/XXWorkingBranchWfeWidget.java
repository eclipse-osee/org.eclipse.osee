/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.branch.BranchStatus;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.StateDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AbstractAtsBranchService;
import org.eclipse.osee.ats.ide.column.TargetedVersionColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.access.AccessControlData;
import org.eclipse.osee.framework.core.access.AccessTopicEventPayload;
import org.eclipse.osee.framework.core.client.AccessTopicEvent;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UpdateBranchData;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.event.EventUtil;
import org.eclipse.osee.framework.core.model.MergeBranch;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.update.ConflictResolverOperation;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictManagerExternal;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.util.MergeInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.util.RebaselineInProgressHandler;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxBranchDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * Composite widget containing branch name and status, all the operations available for the branch and the Commit
 * Manager.
 *
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXWorkingBranchWfeWidget extends XXAbstractBaseXWidget<String> implements IArtifactEventListener, IArtifactTopicEventListener, IBranchEventListener, EventHandler {

   public static final WidgetId ID = WidgetIdAts.XXWorkingBranchWfeWidget;

   // ------------------------------------------------------------------
   // State
   // ------------------------------------------------------------------

   private TeamWorkFlowArtifact teamWf;

   // Branch state cached from events
   private boolean workingBranchCreationInProgress;
   private boolean workingBranchCommitInProgress;
   private boolean workingBranchCommitWithMergeInProgress;
   private boolean workingBranchInWork;
   private boolean committedBranchExists;
   private boolean disableAll;
   private BranchToken workingBranch = BranchToken.SENTINEL;

   // Targeted version state
   private IAtsVersion targetedVersion;
   private boolean targetedVersionLoaded;
   private BranchToken parentBranch = BranchToken.SENTINEL;

   // Operation-in-progress flags for button text feedback
   private boolean deletionInProgress;
   private boolean updateInProgress;
   private boolean lockInProgress;
   private boolean favoritesInProgress;
   private boolean deleteMergeBranchesInProgress;
   private boolean selectVersionInProgress;

   // Controls
   private Label targetedVersionLabel;
   private Label targetedVersionValue;
   private Label parentBranchLabel;
   private Label parentBranchValue;
   private Label branchLabel;
   private Label branchValue;
   private Composite buttonRow;
   private final Map<BranchButton, Button> buttons = new EnumMap<>(BranchButton.class);
   private final Set<BranchButton> hiddenButtons = new HashSet<>();

   private boolean lockEventHandlerRegistered;

   public XXWorkingBranchWfeWidget() {
      super(ID, "Working Branch");
      OseeEventManager.addListener(this);
      set(XOption.VAL_NOT_APPLICABLE);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      comp = new Composite(parent, SWT.NONE);
      GridLayout topLayout = new GridLayout(1, false);
      topLayout.marginHeight = 6;
      topLayout.marginWidth = 0;
      comp.setLayout(topLayout);
      GridData topGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
      topGd.horizontalSpan = horizontalSpan;
      comp.setLayoutData(topGd);
      if (getToolkit() != null) {
         getToolkit().adapt(comp);
      }

      // Targeted version row
      Composite targetedVersionComp = createLabelValueRow(comp);
      targetedVersionLabel = new Label(targetedVersionComp, SWT.NONE);
      targetedVersionLabel.setText("Targeted Version: ");
      targetedVersionLabel.setFont(FontManager.getDefaultLabelFont());
      targetedVersionLabel.setToolTipText("Version or Build to make changes against");
      targetedVersionValue = new Label(targetedVersionComp, SWT.NONE);
      targetedVersionValue.setToolTipText("Version or Build to make changes against");

      // Parent branch row
      Composite parentBranchComp = createLabelValueRow(comp);
      parentBranchLabel = new Label(parentBranchComp, SWT.NONE);
      parentBranchLabel.setText("Parent Branch: ");
      parentBranchLabel.setFont(FontManager.getDefaultLabelFont());
      parentBranchLabel.setToolTipText("Baseline Branch configured for selected Targeted Version");
      parentBranchValue = new Label(parentBranchComp, SWT.NONE);
      parentBranchValue.setToolTipText("Baseline Branch configured for selected Targeted Version");

      // Working branch row
      Composite branchComp = createLabelValueRow(comp);
      branchLabel = new Label(branchComp, SWT.NONE);
      branchLabel.setText("Working Branch: ");
      branchLabel.setFont(FontManager.getDefaultLabelFont());
      branchLabel.setToolTipText(
         "Location for changes specific to this workflow and eventually merged back into Parent Branch");
      branchValue = new Label(branchComp, SWT.NONE);
      branchValue.setToolTipText(
         "Location for changes specific to this workflow and eventually merged back into Parent Branch");

      // Horizontal button row using RowLayout so visible buttons shift together
      buttonRow = new Composite(comp, SWT.NONE);
      RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
      rowLayout.marginTop = 0;
      rowLayout.marginBottom = 0;
      rowLayout.marginLeft = 0;
      rowLayout.spacing = 2;
      rowLayout.wrap = false;
      rowLayout.pack = true;
      buttonRow.setLayout(rowLayout);
      buttonRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      if (getToolkit() != null) {
         getToolkit().adapt(buttonRow);
      }

      // Create buttons in the specified order
      for (BranchButton bb : BranchButton.values()) {
         createButton(bb);
      }

      // Register the OSGi event handler for lock access changes
      registerLockEventHandler();

      refreshAll();
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   public String getDisplayValue() {
      return getStatus().getDisplayName();
   }

   @Override
   protected boolean handleSelection() {
      return false;
   }

   @Override
   protected TransactionToken handleSelectedPersist() {
      return TransactionToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // Show / hide buttons
   // ------------------------------------------------------------------

   /** Hide a button. It will not be created or will be disposed if controls already exist. */
   public void setButtonVisible(BranchButton btn, boolean visible) {
      if (visible) {
         hiddenButtons.remove(btn);
      } else {
         hiddenButtons.add(btn);
      }
      // If controls already exist, update visibility
      Button swtBtn = buttons.get(btn);
      if (swtBtn != null && !swtBtn.isDisposed()) {
         swtBtn.setVisible(visible);
         GridData gd = (GridData) swtBtn.getLayoutData();
         gd.exclude = !visible;
         buttonRow.layout(true, true);
         comp.layout(true, true);
      }
   }

   public boolean isButtonVisible(BranchButton btn) {
      return !hiddenButtons.contains(btn);
   }

   // ------------------------------------------------------------------
   // Refresh
   // ------------------------------------------------------------------

   @Override
   public void refresh() {
      if (!Widgets.isAccessible(comp)) {
         return;
      }
      validate();
   }

   private void refreshAll() {
      if (teamWf == null) {
         return;
      }
      Displays.ensureInDisplayThread(() -> {
         if (!Widgets.isAccessible(comp)) {
            return;
         }
         refreshLabel();
         refreshButtonEnablement();
      });
   }

   @Override
   public Control getControl() {
      return comp;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   //////////////////////////////////////////
   // Event Listeners
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // Event listeners
   // ------------------------------------------------------------------

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      asyncRefresh();
   }

   @Override
   public void handleArtifactTopicEvent(ArtifactTopicEvent artifactTopicEvent, Sender sender) {
      asyncRefresh();
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      new Thread(() -> {
         try {
            updateBranchState();
            asyncRefresh();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }).start();
   }

   /** OSGi EventHandler for access/lock changes */
   @Override
   public void handleEvent(org.osgi.service.event.Event event) {
      if (teamWf != null) {
         BranchId branch = teamWf.getWorkingBranch();
         if (branch.isValid()) {
            AccessTopicEventPayload accessEvent = EventUtil.getTopicJson(event, AccessTopicEventPayload.class);
            if (branch.equals(accessEvent.getBranch())) {
               asyncRefresh();
            }
         }
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return null;
   }

   @Override
   public List<? extends ITopicEventFilter> getTopicEventFilters() {
      return null;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private Composite createLabelValueRow(Composite parent) {
      Composite rowComp = new Composite(parent, SWT.NONE);
      rowComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
      RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
      rowLayout.marginTop = 0;
      rowLayout.marginBottom = 0;
      rowLayout.marginLeft = 0;
      rowLayout.spacing = 0;
      rowLayout.wrap = false;
      rowLayout.pack = true;
      rowComp.setLayout(rowLayout);
      if (getToolkit() != null) {
         getToolkit().adapt(rowComp);
      }
      return rowComp;
   }

   /**
    * Enumeration of all branch buttons in their display order. Each constant knows its tooltip, image, and the widget
    * name to check against the state definition for allowed-in-state logic.
    */
   public enum BranchButton {
      SelectVersion,
      Create,
      ArtifactExplorer,
      ChangeReport,
      WordChangeReport,
      ContextChangeReport,
      Delete,
      Favorites,
      Lock,
      Update,
      DeleteMergeBranches;
   }

   private Button newButton(Composite parent) {
      if (getToolkit() != null) {
         return getToolkit().createButton(parent, null, SWT.PUSH);
      }
      return new Button(parent, SWT.PUSH);
   }

   private void createButton(BranchButton bb) {
      Button btn = newButton(buttonRow);
      buttons.put(bb, btn);

      switch (bb) {
         case SelectVersion:
            btn.setToolTipText("Select Targeted Version");
            btn.setImage(ImageManager.getImage(AtsImage.VERSION));
            btn.addListener(SWT.Selection, e -> handleSelectVersion(btn));
            break;
         case Create:
            btn.setToolTipText("Create Working Branch");
            btn.setImage(ImageManager.getImage(CoreImage.BRANCH));
            btn.addListener(SWT.Selection, e -> handleCreate(btn));
            break;
         case ArtifactExplorer:
            btn.setToolTipText("Show Artifact Explorer");
            btn.setImage(ImageManager.getImage(CoreImage.ARTIFACT_EXPLORER));
            btn.addListener(SWT.Selection, e -> handleArtifactExplorer());
            break;
         case ChangeReport:
            btn.setToolTipText("Show Change Report");
            btn.setImage(ImageManager.getImage(CoreImage.BRANCH_CHANGE));
            btn.addListener(SWT.Selection, e -> handleChangeReport());
            break;
         case WordChangeReport:
            btn.setToolTipText("Generate Full Word Change Report");
            btn.setImage(ImageManager.getProgramImage("docx"));
            btn.addListener(SWT.Selection, e -> handleWordChangeReport());
            break;
         case ContextChangeReport:
            btn.setToolTipText("Generate Context Change Report");
            btn.setImage(ImageManager.getImage(AtsImage.CONTEXT_CHANGE_REPORT));
            btn.addListener(SWT.Selection, e -> handleContextChangeReport());
            break;
         case Delete:
            btn.setToolTipText("Delete Working Branch");
            btn.setImage(ImageManager.getImage(CoreImage.TRASH));
            btn.addListener(SWT.Selection, e -> handleDelete(btn));
            break;
         case Favorites:
            btn.setToolTipText("Toggle Working Branch as Favorite");
            btn.setImage(ImageManager.getImage(AtsImage.FAVORITE));
            btn.addListener(SWT.Selection, e -> handleFavorites());
            break;
         case Lock:
            btn.setToolTipText("Toggle Working Branch Access Control");
            // Image is set in refreshEnablement based on lock state
            btn.addListener(SWT.Selection, e -> handleLock(btn));
            break;
         case Update:
            btn.setToolTipText("Update Working Branch From Targeted Version or Team Configured Branch");
            btn.setImage(ImageManager.getImage(CoreImage.BRANCH_SYNCH));
            btn.addListener(SWT.Selection, e -> handleUpdate());
            break;
         case DeleteMergeBranches:
            btn.setToolTipText("Delete Merge Branch(es)");
            btn.setImage(ImageManager.getImage(CoreImage.DELETE_MERGE_BRANCHES));
            btn.addListener(SWT.Selection, e -> handleDeleteMergeBranches());
            break;
      }

      // Hide if configured as hidden
      if (hiddenButtons.contains(bb)) {
         btn.setVisible(false);
      }
   }

   private void registerLockEventHandler() {
      if (!lockEventHandlerRegistered) {
         try {
            BundleContext context = AtsApiService.get().getEventService().getBundleContext(Activator.PLUGIN_ID);
            context.registerService(EventHandler.class.getName(), this,
               AtsUtil.hashTable(EventConstants.EVENT_TOPIC, AccessTopicEvent.ACCESS_BRANCH_MODIFIED.getTopic()));
            lockEventHandlerRegistered = true;
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Could not register lock event handler", ex);
         }
      }
   }

   private void refreshLabel() {
      // Refresh targeted version value
      if (Widgets.isAccessible(targetedVersionValue)) {
         String versionStr;
         if (targetedVersion != null) {
            versionStr = targetedVersion.getName();
         } else {
            versionStr = "Not Selected";
         }
         targetedVersionValue.setText(versionStr);
         targetedVersionValue.getParent().layout(true, true);
      }

      // Refresh parent branch value
      if (Widgets.isAccessible(parentBranchValue)) {
         String parentBranchStr;
         if (parentBranch != null && parentBranch.isValid()) {
            parentBranchStr = parentBranch.getName();
         } else {
            parentBranchStr = "Not Configured";
         }
         parentBranchValue.setText(parentBranchStr);
         parentBranchValue.getParent().layout(true, true);
      }

      // Refresh working branch value
      if (!Widgets.isAccessible(branchValue)) {
         return;
      }
      BranchToken wb = workingBranch;
      String valueStr;
      if (wb != null && wb.isValid()) {
         valueStr = String.format("%s - Status: %s", wb.getShortName(), getStatus().getDisplayName());
      } else if (committedBranchExists) {
         valueStr = "Working Branch Committed";
      } else {
         valueStr = "Not Yet Created";
      }
      branchValue.setText(valueStr);
      branchValue.getParent().layout(true, true);
      if (getManagedForm() != null && !getManagedForm().getForm().isDisposed()) {
         getManagedForm().reflow(true);
      }
   }

   private void refreshButtonEnablement() {
      for (Map.Entry<BranchButton, Button> entry : buttons.entrySet()) {
         Button btn = entry.getValue();
         if (btn == null || btn.isDisposed()) {
            continue;
         }
         BranchButton bb = entry.getKey();
         boolean enabled = computeEnabled(bb);
         btn.setEnabled(enabled);

         // Reset button text for buttons that show progress text only when operation is complete
         if (bb == BranchButton.SelectVersion && !selectVersionInProgress) {
            btn.setText("");
         }
         if (bb == BranchButton.Create && !workingBranchCreationInProgress) {
            btn.setText("");
         }
         if (bb == BranchButton.Delete && !deletionInProgress) {
            btn.setText("");
         }
         if (bb == BranchButton.Update && !updateInProgress) {
            btn.setText("");
         }
         if (bb == BranchButton.Lock && !lockInProgress) {
            btn.setText("");
            refreshLockImage(btn);
         }
         if (bb == BranchButton.Favorites && !favoritesInProgress) {
            btn.setText("");
         }
         if (bb == BranchButton.DeleteMergeBranches && !deleteMergeBranchesInProgress) {
            btn.setText("");
         }
      }
      if (Widgets.isAccessible(buttonRow)) {
         buttonRow.layout(true, true);
      }
   }

   private boolean computeEnabled(BranchButton bb) {
      if (!isWidgetAllowedInCurrentState(bb)) {
         return false;
      }
      // SelectVersion is enabled unless in progress or branch already exists
      if (bb == BranchButton.SelectVersion) {
         return !selectVersionInProgress && !workingBranchInWork && !committedBranchExists;
      }
      // All other buttons require a targeted version to be selected
      if (targetedVersion == null) {
         return false;
      }
      switch (bb) {
         case Create:
            return !disableAll && !workingBranchCreationInProgress && !workingBranchCommitInProgress && !workingBranchInWork && !committedBranchExists;
         case ArtifactExplorer:
            return !disableAll && workingBranchInWork;
         case ChangeReport:
         case WordChangeReport:
         case ContextChangeReport:
            return !disableAll && (workingBranchInWork || committedBranchExists);
         case Delete:
            return !disableAll && !deletionInProgress && workingBranchInWork;
         case Favorites:
            return !disableAll && !favoritesInProgress && workingBranchInWork;
         case Lock:
            return !disableAll && !lockInProgress && workingBranchInWork;
         case Update:
            return !disableAll && !updateInProgress && workingBranchInWork;
         case DeleteMergeBranches:
            return !deleteMergeBranchesInProgress && destinationBranchNotCommitted();
         default:
            return false;
      }
   }

   private boolean isWidgetAllowedInCurrentState(BranchButton bb) {
      if (teamWf == null) {
         return false;
      }
      // These buttons are always allowed regardless of state definition
      switch (bb) {
         case SelectVersion:
         case Create:
         case Delete:
         case Lock:
         case Update:
         case ArtifactExplorer:
         case ChangeReport:
         case WordChangeReport:
         case ContextChangeReport:
         case Favorites:
            return true;
         default:
            break;
      }
      // Others check against widget names in the state definition
      String widgetName = getWidgetNameForStateCheck(bb);
      if (widgetName == null) {
         return true;
      }
      StateDefinition stateDef = teamWf.getStateDefinition();
      return teamWf.getAtsApi().getWorkDefinitionService().hasWidgetNamed(stateDef, widgetName);
   }

   /**
    * Returns the legacy widget class name used for state-definition "allowed" checks, or null if no state check is
    * needed.
    */
   private static String getWidgetNameForStateCheck(BranchButton bb) {
      switch (bb) {
         case DeleteMergeBranches:
            return "XWorkingBranchButtonDeleteMergeBranches";
         default:
            return null;
      }
   }

   private void refreshLockImage(Button button) {
      if (button == null || button.isDisposed()) {
         return;
      }
      boolean noBranch = false;
      boolean someAccessControlSet = false;
      BranchId branch = BranchId.SENTINEL;
      try {
         branch = teamWf.getWorkingBranch();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (branch.isInvalid()) {
         noBranch = true;
      } else {
         someAccessControlSet =
            !ServiceUtil.getOseeClient().getAccessControlService().getAccessControlList(branch).isEmpty();
      }
      button.setImage(
         ImageManager.getImage(noBranch || someAccessControlSet ? CoreImage.LOCK_LOCKED : CoreImage.LOCK_UNLOCKED));
      button.redraw();
   }

   // ------------------------------------------------------------------
   // Branch state
   // ------------------------------------------------------------------

   private void updateBranchState() {
      if (teamWf != null) {
         // Load targeted version if not already loaded
         if (!targetedVersionLoaded) {
            targetedVersion = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
            targetedVersionLoaded = true;
         }

         // Load parent branch from targeted version (with inheritance from team def)
         if (targetedVersion != null) {
            BranchId baselineBranchId =
               AtsApiService.get().getVersionService().getBaselineBranchIdInherited(targetedVersion);
            if (baselineBranchId != null && baselineBranchId.isValid()) {
               parentBranch = AtsApiService.get().getBranchService().getBranch(baselineBranchId);
            } else {
               parentBranch = BranchToken.SENTINEL;
            }
         } else {
            parentBranch = BranchToken.SENTINEL;
         }

         workingBranch = AtsApiService.get().getBranchService().getWorkingBranch(teamWf, true);
         workingBranchCreationInProgress =
            AtsApiService.get().getBranchService().isWorkingBranchCreationInProgress(teamWf);
         workingBranchInWork = AtsApiService.get().getBranchService().isWorkingBranchInWork(teamWf);

         if (workingBranch.isInvalid()) {
            workingBranchCommitInProgress = false;
         } else {
            BranchState state = BranchManager.getState(workingBranch);
            workingBranchCreationInProgress |= state.isCreationInProgress();
            workingBranchCommitInProgress = AtsApiService.get().getBranchService().isWorkingBranchCommitInProgress(
               teamWf) || state.isCommitInProgress();
            workingBranchCommitWithMergeInProgress =
               BranchManager.hasMergeBranches(workingBranch) && !state.isRebaselineInProgress();
         }
         committedBranchExists = AtsApiService.get().getBranchService().isCommittedBranchExists(teamWf);
         disableAll = workingBranchCommitInProgress;
      }
   }

   private BranchStatus getStatus() {
      if (teamWf != null) {
         if (workingBranchCreationInProgress) {
            return BranchStatus.Changes_NotPermitted__CreationInProgress;
         } else if (workingBranchCommitInProgress) {
            return BranchStatus.Changes_NotPermitted__CommitInProgress;
         } else if (committedBranchExists) {
            return BranchStatus.Changes_NotPermitted__BranchCommitted;
         } else if (workingBranchInWork) {
            return BranchStatus.Changes_InProgress;
         }
      }
      return BranchStatus.Not_Started;
   }

   private boolean destinationBranchNotCommitted() {
      try {
         if (workingBranchCommitWithMergeInProgress) {
            List<MergeBranch> mergeBranches = BranchManager.getMergeBranches(workingBranch);
            Collection<BranchToken> committedBranches =
               AtsApiService.get().getBranchService().getBranchesCommittedTo(teamWf);
            for (MergeBranch mb : mergeBranches) {
               if (!committedBranches.contains(mb.getDestinationBranch())) {
                  return true;
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

   // ------------------------------------------------------------------
   // Button actions
   // ------------------------------------------------------------------

   private void handleSelectVersion(Button button) {
      selectVersionInProgress = true;
      button.setText("Selecting...");
      button.setEnabled(false);
      buttonRow.layout(true, true);

      new Thread("Select Targeted Version") {
         @Override
         public void run() {
            try {
               Displays.ensureInDisplayThread(() -> {
                  boolean changed = TargetedVersionColumnUI.getInstance().promptChangeVersion(teamWf);
                  if (changed) {
                     // Reload the targeted version
                     targetedVersionLoaded = false;
                     updateBranchState();
                  }
                  selectVersionInProgress = false;
                  asyncRefresh();
               });
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               selectVersionInProgress = false;
               asyncRefresh();
            }
         }
      }.start();
   }

   private void handleCreate(Button button) {
      Result result = AtsApiService.get().getBranchService().createWorkingBranchValidate(teamWf);
      boolean appropriate = selectTargetedVersionOrConfigureParentBranchIfAppropriate(result);
      if (appropriate) {
         return;
      }
      if (result.isFalse()) {
         AWorkbench.popup(result);
         return;
      }
      try {
         String workingBranchName = AtsApiService.get().getBranchService().getBranchName(teamWf);
         final BranchId parentBranch = AtsApiService.get().getBranchService().getConfiguredBranchForWorkflow(teamWf);
         if (parentBranch.isValid()) {
            String pBranchName = AtsApiService.get().getBranchService().getBranchName(parentBranch);
            if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create Working Branch",
               "Creating working branch:\n\n\"" + workingBranchName + //
                  "\"\n\nfrom parent branch: \n\n\"" + //
                  pBranchName + "\"\n\nIs that correct?\n\n" + //
                  "---\nNOTE: Working branches are necessary when OSEE Artifact changes " + //
                  "are made during implementation.")) {
               return;
            }
            button.setText("Creating Branch...");
            button.setEnabled(false);
            buttonRow.layout(true, true);
            AtsApiService.get().getBranchServiceIde().createWorkingBranch_Create(teamWf, false);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         asyncRefresh();
      }
   }

   private boolean selectTargetedVersionOrConfigureParentBranchIfAppropriate(Result result) {
      if (result.getText().equals(AbstractAtsBranchService.PARENT_BRANCH_CAN_NOT_BE_DETERMINED)) {
         IAtsVersion version = AtsApiService.get().getVersionService().getTargetedVersion(teamWf);
         if (version == null) {
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
               AbstractAtsBranchService.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
               new String[] {"Select Targeted Version", "Cancel"}, 0);
            if (dialog.open() == Window.OK) {
               TargetedVersionColumnUI.getInstance().promptChangeVersion(teamWf);
            }
         } else {
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Create Working Branch", null,
               AbstractAtsBranchService.PARENT_BRANCH_CAN_NOT_BE_DETERMINED, MessageDialog.ERROR,
               new String[] {"Ok", "Cancel"}, 0);
            dialog.open();
         }
         return true;
      }
      return false;
   }

   private void handleArtifactExplorer() {
      try {
         ArtifactExplorer.exploreBranch(teamWf.getWorkingBranch());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private void handleChangeReport() {
      AtsApiService.get().getBranchServiceIde().showChangeReport(teamWf);
   }

   private void handleWordChangeReport() {
      AtsApiService.get().getBranchServiceIde().generateWordChangeReport(teamWf);
   }

   private void handleContextChangeReport() {
      AtsApiService.get().getBranchServiceIde().generateContextChangeReport(teamWf);
   }

   private void handleDelete(Button button) {
      deletionInProgress = true;
      button.setText("Deleting...");
      button.setEnabled(false);
      buttonRow.layout(true, true);

      Job deleteJob = new Job("Delete Working Branch") {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               Displays.ensureInDisplayThread(() -> {
                  AtsApiService.get().getBranchServiceIde().deleteWorkingBranch(teamWf, true, false);
               });
            } finally {
               deletionInProgress = false;
               updateBranchState();
               asyncRefresh();
            }
            return Status.OK_STATUS;
         }
      };
      deleteJob.setUser(true);
      deleteJob.schedule();
   }

   private void handleFavorites() {
      try {
         Artifact user = ArtifactQuery.getArtifactFromId(OseeApiService.user(), CoreBranches.COMMON);
         user.reloadAttributesAndRelations();
         if (OseeApiService.userSvc().isSystemUser(user)) {
            AWorkbench.popup("Can't set preference as System User = " + user);
            return;
         }
         BranchId branch = teamWf.getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isFavorite = OseeApiService.branchSvc().isFavoriteBranch(branch);
         String message = String.format("Working branch is currently [%s]\n\nToggle favorite?",
            isFavorite ? "Favorite" : "NOT Favorite");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch as Favorite", message)) {
            Button btn = buttons.get(BranchButton.Favorites);
            favoritesInProgress = true;
            if (btn != null && !btn.isDisposed()) {
               btn.setText("Updating...");
               btn.setEnabled(false);
               buttonRow.layout(true, true);
            }

            new Thread("Toggle Favorite Branch") {
               @Override
               public void run() {
                  try {
                     OseeApiService.branchSvc().toggleFavoriteBranch(branch);
                     OseeEventManager.kickBranchEvent(XXWorkingBranchWfeWidget.this,
                        new BranchEvent(BranchEventType.FavoritesUpdated, branch));
                  } finally {
                     favoritesInProgress = false;
                     asyncRefresh();
                  }
               }
            }.start();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleLock(Button button) {
      try {
         BranchId branch = teamWf.getWorkingBranch();
         if (branch.isInvalid()) {
            AWorkbench.popup("Working branch doesn't exist");
            return;
         }
         boolean isLocked = false;
         boolean manuallyLocked = false;
         Collection<AccessControlData> datas =
            AtsApiService.get().getAccessControlService().getAccessControlList(branch);
         if (datas.size() > 1) {
            manuallyLocked = true;
         } else if (datas.isEmpty()) {
            isLocked = false;
         } else {
            AccessControlData data = datas.iterator().next();
            if (data.getSubject().equals(CoreUserGroups.Everyone) && data.getBranchPermission().equals(
               PermissionEnum.READ)) {
               isLocked = true;
            } else {
               manuallyLocked = true;
            }
         }
         if (manuallyLocked) {
            AWorkbench.popup(
               "Manual access control applied to branch.  Can't override.\n\nUse Access Control option of Branch Manager");
            return;
         }
         String message = String.format("Working branch is currently [%s]\n\n%s the Branch?",
            isLocked ? "Locked" : "NOT Locked", isLocked ? "UnLock" : "Lock");
         if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), "Toggle Branch Lock", message)) {
            lockInProgress = true;
            button.setText(isLocked ? "Unlocking..." : "Locking...");
            button.setEnabled(false);
            buttonRow.layout(true, true);

            boolean wasLocked = isLocked;
            AccessControlData dataToRemove = datas.isEmpty() ? null : datas.iterator().next();

            new Thread("Toggle Branch Lock") {
               @Override
               public void run() {
                  try {
                     if (wasLocked) {
                        AtsApiService.get().getAccessControlService().removeAccessControlDataIf(true, dataToRemove);
                     } else {
                        IUserGroup everyoneGroup =
                           AtsApiService.get().userService().getUserGroup(CoreUserGroups.Everyone);
                        Conditions.assertTrue(everyoneGroup.getArtifact() instanceof Artifact, "Must be Artifact");
                        AtsApiService.get().getAccessControlService().setPermission(everyoneGroup.getArtifact(), branch,
                           PermissionEnum.READ);
                     }
                     Displays.ensureInDisplayThread(() -> {
                        AWorkbench.popup(String.format("Branch set to [%s]", !wasLocked ? "Locked" : "NOT Locked"));
                     });
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  } finally {
                     lockInProgress = false;
                     asyncRefresh();
                  }
               }
            }.start();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleUpdate() {
      try {
         BranchToken branchToUpdate = workingBranch;
         if (branchToUpdate == null || branchToUpdate.isInvalid()) {
            return;
         }
         Artifact associatedArtifact = BranchManager.getAssociatedArtifact(branchToUpdate);
         IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(associatedArtifact);
         if (workItem == null || !workItem.isTeamWorkflow()) {
            AWorkbench.popup("Working Branch must have associated Team Workflow");
            return;
         }
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
         BranchId targetedBranch = AtsApiService.get().getBranchService().getConfiguredBranchForWorkflow(teamWf);
         if (BranchManager.isUpdatable(branchToUpdate)) {
            if (BranchManager.getState(branchToUpdate).isRebaselineInProgress()) {
               RebaselineInProgressHandler.handleRebaselineInProgress(branchToUpdate);
            } else {
               boolean isUserSure = MessageDialog.openQuestion(
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Update Branch",
                  String.format(
                     "Are you sure you want to update: \n\n[%s]\n\nBranch from Targeted Version or Team Configured branch:\n\n[%s]?",
                     branchToUpdate.getName(), BranchManager.getBranchToken(targetedBranch).getName()));
               if (isUserSure) {
                  Button btn = buttons.get(BranchButton.Update);
                  updateInProgress = true;
                  if (btn != null && !btn.isDisposed()) {
                     btn.setText("Updating...");
                     btn.setEnabled(false);
                     buttonRow.layout(true, true);
                  }

                  Job updateBranch = new Job("Update Branch") {
                     @Override
                     protected IStatus run(IProgressMonitor monitor) {
                        try {
                           UpdateBranchData branchData =
                              BranchManager.updateBranch(branchToUpdate, new UserConflictResolver());
                           if (branchData.getResults().isErrors()) {
                              XResultDataUI.report(branchData.getResults(), "Update Branch Failed");
                           } else if (branchData.isNeedsMerge()) {
                              XResultDataUI.report(branchData.getResults(), "Branch needs to be merged.");
                           }
                        } finally {
                           updateInProgress = false;
                           updateBranchState();
                           asyncRefresh();
                        }
                        return Status.OK_STATUS;
                     }
                  };
                  updateBranch.setUser(true);
                  updateBranch.setPriority(Job.LONG);
                  updateBranch.schedule();
               }
            }
         } else {
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Can't Update Branch",
               String.format("Couldn't update [%s] because it currently has merge branches from commits.  " //
                  + "To perform an update please delete all the merge branches for this branch.",
                  branchToUpdate.getName()));
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void handleDeleteMergeBranches() {
      try {
         BranchId wrkBranch = teamWf.getWorkingBranch();
         if (workingBranchCommitWithMergeInProgress) {
            List<BranchId> selectedBranches = new ArrayList<>();
            Collection<BranchToken> branchesAlreadyCommitted =
               AtsApiService.get().getBranchService().getBranchesCommittedTo(teamWf);
            List<MergeBranch> mergeBranches = BranchManager.getMergeBranches(wrkBranch);

            Set<BranchId> destinationMinusAlreadyCommitted = new HashSet<>();
            for (MergeBranch branch : mergeBranches) {
               if (!branchesAlreadyCommitted.contains(branch.getDestinationBranch())) {
                  destinationMinusAlreadyCommitted.add(branch.getDestinationBranch());
               }
            }

            if (destinationMinusAlreadyCommitted.size() > 1) {
               FilteredCheckboxBranchDialog dialog = new FilteredCheckboxBranchDialog("Select Destination Branch(es)",
                  "Select the Destination branch(es) for which you want to Delete the Merge Branch",
                  destinationMinusAlreadyCommitted);
               if (dialog.open() == Window.OK) {
                  for (BranchId branchToken : dialog.getChecked()) {
                     selectedBranches.add(branchToken);
                  }
               }
            } else if (destinationMinusAlreadyCommitted.size() == 1) {
               MergeBranch mergeBranch = BranchManager.getFirstMergeBranch(wrkBranch);
               selectedBranches.add(mergeBranch.getDestinationBranch());
            }

            if (!selectedBranches.isEmpty()) {
               Button btn = buttons.get(BranchButton.DeleteMergeBranches);
               deleteMergeBranchesInProgress = true;
               if (btn != null && !btn.isDisposed()) {
                  btn.setText("Deleting...");
                  btn.setEnabled(false);
                  buttonRow.layout(true, true);
               }

               new Thread("Delete Merge Branches") {
                  @Override
                  public void run() {
                     try {
                        MergeInProgressHandler.deleteMultipleMergeBranches(wrkBranch, selectedBranches, false);
                     } finally {
                        deleteMergeBranchesInProgress = false;
                        updateBranchState();
                        asyncRefresh();
                     }
                  }
               }.start();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   // ------------------------------------------------------------------
   // ConflictResolver for branch update
   // ------------------------------------------------------------------

   private static final class UserConflictResolver extends ConflictResolverOperation {

      public UserConflictResolver() {
         super("Launch Merge Manager", Activator.PLUGIN_ID);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         ConflictManagerExternal conflictManager = getConflictManager();
         Job job = createMergeViewJob(conflictManager.getSourceBranch(), conflictManager.getDestinationBranch());
         Jobs.startJob(job);
      }

      private Job createMergeViewJob(final BranchId sourceBranch, final BranchId destinationBranch) {
         return new UIJob("Launch Merge Manager") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
               try {
                  IWorkbenchPage page = AWorkbench.getActivePage();
                  IViewPart viewPart = page.showView(MergeView.VIEW_ID,
                     String.valueOf(sourceBranch.getId() * 100000 + destinationBranch.getId()),
                     IWorkbenchPage.VIEW_ACTIVATE);
                  if (viewPart instanceof MergeView) {
                     MergeView mergeView = (MergeView) viewPart;
                     mergeView.explore(sourceBranch, destinationBranch, null, null, true);
                  }
               } catch (PartInitException ex) {
                  return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Error launching merge view", ex);
               }
               return Status.OK_STATUS;
            }
         };
      }
   }

   private void asyncRefresh() {
      Displays.ensureInDisplayThread(() -> refreshAll());
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   // ------------------------------------------------------------------
   // Artifact / base class binding
   // ------------------------------------------------------------------

   @Override
   public void setArtifact(Artifact artifact) {
      super.setArtifact(artifact);
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.teamWf = (TeamWorkFlowArtifact) artifact;
         // Reset version loaded flag for new artifact
         targetedVersionLoaded = false;
         targetedVersion = null;
         parentBranch = BranchToken.SENTINEL;
         updateBranchState();
         refreshAll();
      }
   }

}
