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
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { DestroyRef, Injectable, inject, signal } from '@angular/core';
import {
	BranchChangeEventService,
	CurrentBranchInfoService,
	UiService,
} from '@osee/shared/services';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { tab } from '../types/artifact-explorer';
import { ArtifactIconService } from './artifact-icon.service';
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import { map } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerTabService {
	private branchChangeEvent = inject(BranchChangeEventService);
	private artifactIconService = inject(ArtifactIconService);
	private currentBranchService = inject(CurrentBranchInfoService);
	private destroyRef = inject(DestroyRef);

	private tabs = signal<tab[]>([]);
	private _selectedIndex = signal<number>(0);

	private uiService = inject(UiService);
	branchId = toSignal(this.uiService.id, { initialValue: '' });
	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch?.name)
		),
		{ initialValue: '' }
	);
	viewId = toSignal(this.uiService.viewId, { initialValue: '' });

	constructor() {
		this.branchChangeEvent.branchChanges$
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe((event) => {
				// Rebaseline (update-from-parent): the old branch is retired and its artifacts
				// live on the new working branch. Re-point open tabs to the new branch so they
				// reload against the live branch instead of a deleted one.
				if (event.changeType === 'rebaselined' && event.newBranchId) {
					this.repointTabsToBranch(event.branchId, event.newBranchId);
					return;
				}
				// Committed/deleted/purged: the branch is no longer a live working branch to edit
				// (gone, or its changes are now on the parent). Close any tabs opened on it.
				if (
					event.changeType === 'committed' ||
					event.changeType === 'deleted' ||
					event.changeType === 'purged'
				) {
					this.removeTabsByBranchId(event.branchId);
				}
			});
	}

	private removeTabsByBranchId(branchId: string) {
		this.tabs.update((rows) => rows.filter((t) => t.branchId !== branchId));
		if (this._selectedIndex() >= this.tabs().length) {
			this._selectedIndex.set(Math.max(0, this.tabs().length - 1));
		}
	}

	private repointTabsToBranch(oldBranchId: string, newBranchId: string) {
		this.tabs.update((rows) =>
			rows.map((t) =>
				t.branchId === oldBranchId ? { ...t, branchId: newBranchId } : t
			)
		);
	}

	generateTabId() {
		return (performance.now() * Math.random()).toString();
	}

	addTab(tab: tab) {
		this.tabs.update((rows) => [...rows, tab]);
		this.SelectedIndex = this.tabs().length - 1;
	}

	addArtifactTab(artifact: artifactWithRelations) {
		this.addArtifactTabOnBranch(artifact, this.branchId(), this.viewId());
	}

	addArtifactTabOnBranch(
		artifact: artifactWithRelations,
		branchId: string,
		viewId: string
	) {
		// don't open a tab for the same artifact on the same branch
		const currentIndex = this.tabs().findIndex(
			(existingTab) =>
				existingTab.tabType === 'Artifact' &&
				existingTab.branchId === branchId &&
				existingTab.artifact?.id === artifact.id
		);
		if (currentIndex !== -1) {
			this.SelectedIndex = currentIndex;
			return;
		}

		this.addTab({
			tabId: this.generateTabId(),
			tabType: 'Artifact',
			tabTitle: artifact.name,
			artifact: artifact,
			branchId: branchId,
			branchName: this.branchName(),
			viewId: viewId,
		});
	}

	removeTab(index: number) {
		this.tabs.update((rows) => rows.filter((_, i) => index !== i));
		if (this._selectedIndex() >= this.tabs().length) {
			this._selectedIndex.set(Math.max(0, this.tabs().length - 1));
		}
	}

	removeTabByArtifactId(artifactId: string) {
		this.tabs.update((rows) =>
			rows.filter(
				(t) =>
					!(t.tabType === 'Artifact' && t.artifact.id === artifactId)
			)
		);
	}

	updateTabTitle(artifactId: string, newTitle: string) {
		this.tabs.update((rows) =>
			rows.map((t) =>
				t.tabType === 'Artifact' && t.artifact.id === artifactId
					? {
							...t,
							tabTitle: newTitle,
							artifact: { ...t.artifact, name: newTitle },
						}
					: t
			)
		);
	}

	getTabIcon(tab: tab) {
		switch (tab.tabType) {
			case 'Artifact':
				return tab.artifact.icon.icon;
			default:
				return '';
		}
	}

	get Tabs() {
		return this.tabs;
	}

	get selectedIndex() {
		return this._selectedIndex;
	}

	set SelectedIndex(index: number) {
		this._selectedIndex.set(index);
	}

	getTabIconClass(tab: tab) {
		if (tab.tabType === 'Artifact') {
			return this.artifactIconService.getIconClass(tab.artifact.icon);
		}
		return '';
	}

	getTabIconVariantClass(tab: tab) {
		if (tab.tabType === 'Artifact') {
			return this.artifactIconService.getIconVariantClass(
				tab.artifact.icon
			);
		}
		return '';
	}

	onTabDropped(event: CdkDragDrop<unknown[]>) {
		this.tabs.update((tabs) => {
			const copy = [...tabs];
			moveItemInArray(copy, event.previousIndex, event.currentIndex);
			return copy;
		});
		if (this.selectedIndex() === event.previousIndex) {
			this.selectedIndex.set(event.currentIndex);
		}
	}
}
