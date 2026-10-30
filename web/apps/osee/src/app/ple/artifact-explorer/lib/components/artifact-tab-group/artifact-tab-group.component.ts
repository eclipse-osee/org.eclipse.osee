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
import { CdkDrag, CdkDragDrop, CdkDropList } from '@angular/cdk/drag-drop';
import { Component, computed, inject, signal } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTab, MatTabGroup, MatTabLabel } from '@angular/material/tabs';
import { MatTooltip } from '@angular/material/tooltip';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { EditorDirtyService } from '@osee/shared/conflict-resolution';
import { tab } from '../../types/artifact-explorer';
import { ArtifactEditorComponent } from '../editor/artifact-editor/artifact-editor.component';

@Component({
	selector: 'osee-artifact-tab-group',
	imports: [
		MatTabGroup,
		CdkDropList,
		MatTab,
		CdkDrag,
		MatIcon,
		MatProgressSpinner,
		MatTabLabel,
		MatTooltip,
		ArtifactEditorComponent,
	],
	templateUrl: './artifact-tab-group.component.html',
	styles: [
		`
			:host {
				--mdc-secondary-navigation-tab-container-height: 36px;
				--mdc-tab-indicator-active-indicator-height: 2px;
				--mat-tab-header-divider-color: var(
					--osee-background-hover,
					rgba(128, 128, 128, 0.2)
				);
				--mat-tab-header-divider-height: 1px;
				--mat-tab-header-inactive-label-text-color: inherit;
				--mat-tab-header-active-label-text-color: inherit;
			}
		`,
	],
})
export class ArtifactTabGroupComponent {
	private tabService = inject(ArtifactExplorerTabService);
	private dirtyService = inject(EditorDirtyService);

	tabs = this.tabService.Tabs;

	connections = computed(() => this.tabs().map((_, i) => '' + i));

	selectedIndex = this.tabService.selectedIndex;

	/**
	 * How long to wait for an in-progress auto-save to land before falling back to a
	 * confirm prompt. Clicking the close button blurs the focused editor, which
	 * triggers its async blur-save; this window lets that save complete so the tab
	 * closes silently instead of nagging about changes that are already persisting.
	 *
	 * The normal path closes the instant the save lands (well under this budget), so
	 * this is purely the give-up deadline. A stuck save is almost always a dropped
	 * connection, so keep it short enough to surface the "check your network" prompt
	 * promptly, while still comfortably clearing a healthy-but-slow save (~100-400ms).
	 */
	private static readonly AUTOSAVE_SETTLE_MS = 1500;

	/**
	 * Tabs (by stable tabId) whose close is in progress while we wait for an
	 * in-flight auto-save to settle. The close button for these shows a spinner and
	 * is disabled, so a slow save can't be clicked repeatedly into stacked prompts.
	 */
	private readonly closingTabs = signal<ReadonlySet<string>>(new Set());

	/** Whether this tab's close is awaiting its auto-save (shows a spinner, X disabled). */
	protected isClosing(tabId: string): boolean {
		return this.closingTabs().has(tabId);
	}

	private setClosing(tabId: string, closing: boolean) {
		this.closingTabs.update((set) => {
			if (set.has(tabId) === closing) {
				return set;
			}
			const next = new Set(set);
			if (closing) {
				next.add(tabId);
			} else {
				next.delete(tabId);
			}
			return next;
		});
	}

	/**
	 * Closes a tab, keeping the common case seamless. The close itself blurs the
	 * active editor and triggers its auto-save, so the only states that need the
	 * user's attention are a true conflict (can't auto-save) or a save that doesn't
	 * land in time. Everything else closes with no prompt. Resolved by tabId so a
	 * reorder during the brief settle wait can't close the wrong tab.
	 */
	async closeTab(index: number) {
		const closing = this.tabs()[index];
		if (!closing) {
			return;
		}
		// Close by the stable tabId, not index: the index can shift while we await an
		// auto-save, and the same artifact can be open on two branches (so closing by
		// artifact id would wrongly close both). tabId targets exactly this tab.
		const tabId = closing.tabId;

		// Ignore a repeat click while this tab is already settling its save — the
		// button is disabled in that window, this guards the programmatic path too.
		if (this.isClosing(tabId)) {
			return;
		}

		const artifactId =
			closing.tabType === 'Artifact' ? closing.artifact?.id : undefined;

		// A non-artifact tab has nothing to save — close immediately.
		if (!artifactId) {
			this.tabService.removeTabByTabId(tabId);
			return;
		}

		switch (this.dirtyService.leaveStateFor(artifactId)) {
			case 'clean':
				this.tabService.removeTabByTabId(tabId);
				return;

			case 'conflicted': {
				// The one case that must warn: a held conflict can't auto-save, so
				// closing genuinely discards the edit.
				const discard = confirm(
					'This artifact has unsaved changes that conflict with an update from another user. ' +
						'They cannot be saved until you resolve the conflict.\n\n' +
						'OK: close and discard your changes.\n' +
						'Cancel: keep the tab open and resolve the conflict.'
				);
				if (discard) {
					// Explicit discard: clear the held conflict's dirty keys before the
					// editor destroys (its ngOnDestroy skips markClean while conflicted),
					// so they don't leak and leave the entity stuck looking "saving".
					this.dirtyService.clearEntity(artifactId);
					this.tabService.removeTabByTabId(tabId);
				}
				return;
			}

			case 'saving': {
				// The blur from clicking close already kicked off the save; wait for it
				// to land, then close silently. The X shows a spinner and is disabled
				// during this window so the wait can't be clicked into stacked prompts.
				// Only prompt if the save stalls or fails.
				this.setClosing(tabId, true);
				try {
					const result = await this.dirtyService.awaitSettled(
						artifactId,
						ArtifactTabGroupComponent.AUTOSAVE_SETTLE_MS
					);
					if (result === 'saved') {
						// Save landed; its success handler already cleared the dirty keys.
						this.tabService.removeTabByTabId(tabId);
					} else if (
						confirm(
							'This artifact has changes that have not finished saving.\n\n' +
								'OK: close and discard the unsaved changes.\n' +
								'Cancel: keep the tab open. Check that your network badge (top right) is green, then try closing again.'
						)
					) {
						// Explicit discard of a never-landed save: clear the orphaned keys
						// so the entity doesn't keep looking "saving" after the tab is gone.
						this.dirtyService.clearEntity(artifactId);
						this.tabService.removeTabByTabId(tabId);
					}
				} finally {
					this.setClosing(tabId, false);
				}
				return;
			}
		}
	}

	onTabDropped(event: CdkDragDrop<unknown[]>) {
		this.tabService.onTabDropped(event);
	}

	/**
	 * Restricts the tab strip to reordering its own tabs. Because the artifact
	 * explorer wraps everything in a single `cdkDropListGroup`, an artifact dragged
	 * from the hierarchy would otherwise be "accepted" by this list (the tab strip
	 * overlays the whole editor panel), hijacking the drag before it can reach the
	 * markdown editor. Only allow drags that originate from this same list.
	 */
	protected onlyReorderTabs = (drag: CdkDrag, drop: CdkDropList): boolean =>
		drag.dropContainer === drop;

	getAllDropListConnections(index: number) {
		return computed(() =>
			this.connections().filter((row) => row === '' + index)
		);
	}

	trackByIndex(index: number): number {
		return index;
	}

	selectIndex(index: number) {
		this.tabService.SelectedIndex = index;
	}

	getTabIcon(tab: tab) {
		return this.tabService.getTabIcon(tab);
	}

	getTabIconClasses(tab: tab) {
		return (
			this.tabService.getTabIconClass(tab) +
			' ' +
			this.tabService.getTabIconVariantClass(tab)
		);
	}
}
