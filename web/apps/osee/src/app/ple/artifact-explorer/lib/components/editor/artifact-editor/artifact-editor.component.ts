/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	inject,
	input,
	signal,
	viewChild,
} from '@angular/core';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { switchMap } from 'rxjs';
import { HttpResourceRef } from '@angular/common/http';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactInfoPanelComponent } from '../artifact-info-panel/artifact-info-panel.component';
import { AttributesEditorPanelComponent } from '../attributes-editor-panel/attributes-editor-panel.component';
import { RelationsEditorPanelComponent } from '../relations-editor-panel/relations-editor-panel.component';
import { ArtifactHistoryPanelComponent } from '../artifact-history-panel/artifact-history-panel.component';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import {
	ArtifactChangeNotificationService,
	UserPresenceService,
} from '@osee/shared/services';
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import { HelpTopicRegistryService } from '@osee/shared/components';
import { HelpButtonComponent } from '@osee/shared/components';
import { HelpAnchorDirective } from '@osee/shared/components';
import { AttributeToolbarComponent } from '@osee/shared/components';
import { PresenceAvatarsComponent } from '@osee/shared/components';
import {
	ConflictResolutionBannerComponent,
	EditorDirtyService,
} from '@osee/shared/conflict-resolution';

export type EditorSection = 'attributes' | 'relations' | 'history' | 'info';

@Component({
	selector: 'osee-artifact-editor',
	imports: [
		MatIcon,
		MatIconButton,
		MatTooltip,
		RelationsEditorPanelComponent,
		ArtifactInfoPanelComponent,
		AttributesEditorPanelComponent,
		ArtifactHistoryPanelComponent,
		HelpButtonComponent,
		HelpAnchorDirective,
		AttributeToolbarComponent,
		PresenceAvatarsComponent,
		ConflictResolutionBannerComponent,
	],
	templateUrl: './artifact-editor.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ArtifactEditorComponent {
	private readonly helpRegistry = inject(HelpTopicRegistryService);
	private readonly changeNotification = inject(
		ArtifactChangeNotificationService
	);
	private readonly artExpHttpService = inject(ArtifactExplorerHttpService);
	private readonly presenceService = inject(UserPresenceService);
	private readonly destroyRef = inject(DestroyRef);
	private readonly tabService = inject(ArtifactExplorerTabService);
	private readonly snackBar = inject(MatSnackBar);
	private readonly dirtyService = inject(EditorDirtyService);

	tab = input.required<artifactTab>();

	/** Which editor section is currently visible. */
	protected activeSection = signal<EditorSection>('attributes');

	/** Whether delete mode is active (shows × on deletable attributes). */
	protected deleteMode = signal(false);

	/**
	 * Incremented each time a remote change is detected for this artifact.
	 * Passed to child panels as an input so they can trigger targeted reloads.
	 */
	protected remoteChangeCount = signal(0);

	/**
	 * Incremented on any change (local or remote). Used by children like
	 * the history panel that should reload on all changes, not just remote.
	 */
	protected anyChangeCount = signal(0);

	// Derived signals for the resource
	private branchId = computed(() => this.tab().branchId);
	private artifactId = computed(() => this.tab().artifact.id);
	private viewId = computed(() => this.tab().viewId);

	/** Presence context key for this artifact on this branch. */
	private presenceContext = computed(
		() => `${this.branchId()}/${this.artifactId()}`
	);

	/** Presence handle — signal of other users viewing this same artifact. */
	protected presence = this.presenceService.watchContext(
		this.presenceContext,
		this.destroyRef
	);

	/**
	 * Shared artifact resource (includes relations) — owned by the parent,
	 * consumed by attributes panel and relations panel. One fetch serves both.
	 */
	protected artifactResource: HttpResourceRef<
		artifactWithRelations | undefined
	> = this.artExpHttpService.getArtifactWithRelationsResource(
		this.branchId,
		this.artifactId,
		this.viewId
	);

	/** Reference to the attributes panel for triggering add dialog and conflict resolution. */
	protected readonly attrPanel =
		viewChild<AttributesEditorPanelComponent>('attrPanel');

	/**
	 * SSE + local save subscription: listens for changes to THIS artifact only. Filtering upstream
	 * via `forArtifact(branchId, artifactId)` means an app-wide invalidation runs this callback only
	 * for the matching editor, not once per open tab. Driven through `toObservable` so the required
	 * `tab` input is read lazily (after it's set), not at construction time; `switchMap` re-points
	 * the filter if the tab's branch/artifact ever change.
	 */
	private readonly _artifactKey = computed(() => ({
		branchId: this.branchId(),
		artifactId: this.artifactId(),
	}));

	private readonly _changeSubscription = toObservable(this._artifactKey)
		.pipe(
			switchMap((key) =>
				this.changeNotification.forArtifact(
					key.branchId,
					key.artifactId
				)
			),
			takeUntilDestroyed()
		)
		.subscribe((inv) => {
			const t = this.tab();
			// If this artifact was deleted, close the tab instead of reloading
			if (inv.changeTypes.includes('artifact_deleted')) {
				this.tabService.removeTabByArtifactId(t.artifact.id);
				if (!inv.isLocal) {
					const name = t.artifact.name || t.artifact.id;
					this.snackBar.open(
						`"${name}" was deleted by another user.`,
						'Dismiss',
						{ duration: 5000 }
					);
				}
				return;
			}
			this.anyChangeCount.update((c) => c + 1);

			if (!inv.isLocal) {
				this.remoteChangeCount.update((c) => c + 1);
				// If the user has unsaved edits on this artifact, do NOT reload
				// the shared resource — that would overwrite their in-progress
				// edits and clear the conflict "red ring". The attributes panel
				// detects the conflict via remoteChangeCount and fetches the
				// latest server state on demand for the resolution dialog.
				if (this.dirtyService.hasDirtyEditorsForEntity(t.artifact.id)) {
					return;
				}
			}
			this.artifactResource.reload();
		});

	/**
	 * On SSE resync (reconnect), refresh this artifact — change events may have been missed while
	 * disconnected. Skips reload when the user has unsaved edits so in-progress work and the
	 * conflict indicator are preserved; the conflict path already handles stale detection.
	 */
	private readonly _resyncSubscription = this.changeNotification.resync$
		.pipe(takeUntilDestroyed())
		.subscribe(() => {
			const t = this.tab();
			if (this.dirtyService.hasDirtyEditorsForEntity(t.artifact.id)) {
				return;
			}
			this.artifactResource.reload();
		});

	private readonly _registerHelp = this.helpRegistry.register({
		id: 'attribute-editor',
		label: 'Attribute Editor',
		markdownPath: 'assets/help/attribute-editor/overview.md',
		sections: [
			{ id: 'editing', label: 'Editing', anchorId: 'attr-panel' },
			{
				id: 'toolbar-actions',
				label: 'Toolbar Actions',
				anchorId: 'attr-toolbar',
			},
			{
				id: 'adding-attributes',
				label: 'Adding Attributes',
				anchorId: 'attr-add-btn',
			},
			{
				id: 'deleting-attributes',
				label: 'Deleting Attributes',
				anchorId: 'attr-delete-btn',
			},
			{
				id: 'grouped-attributes',
				label: 'Grouped Attributes',
				anchorId: '',
			},
			{
				id: 'working-with-others-in-real-time',
				label: 'Working with Others',
				anchorId: '',
			},
		],
	});

	protected openAddAttributeDialog() {
		this.attrPanel()?.openAddAttributeDialog();
	}

	protected toggleDeleteMode() {
		this.deleteMode.update((v) => !v);
	}
}
