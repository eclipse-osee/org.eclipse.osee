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
import { Injectable, inject, signal } from '@angular/core';
import { BranchCommitEventService, UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	tab,
	artifact,
	TabType,
	artifactSentinel,
} from '../types/artifact-explorer.data';
import { ArtifactIconService } from './artifact-icon.service';
import { twColorClasses } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ArtifactExplorerTabService {
	private tabs = signal<tab[]>([]);
	private _selectedIndex = signal<number>(0);

	private uiService = inject(UiService);
	branchId = toSignal(this.uiService.id, { initialValue: '' });
	viewId = toSignal(this.uiService.viewId, { initialValue: '' });

	constructor(
		private eventService: BranchCommitEventService,
		private artifactIconService: ArtifactIconService
	) {
		this.eventService.events.subscribe((id) => {
			this.tabs.update((current) =>
				current.filter((tab) => tab.branchId !== id)
			);
		});
	}

	addTab(tabType: TabType, tabTitle: string) {
		this.tabs.update((rows) => [
			...rows,
			{
				tabType,
				tabTitle,
				artifact: artifactSentinel,
				branchId: this.branchId(),
				viewId: this.viewId(),
			},
		]);
		this.SelectedIndex = this.tabs().length - 1;
	}

	addArtifactTab(artifact: artifact) {
		this.addArtifactTabOnBranch(artifact, this.branchId(), this.viewId());
	}

	addArtifactTabOnBranch(
		artifact: artifact,
		branchId: string,
		viewId: string
	) {
		// don't open a tab for the same artifact on the same branch
		const currentIndex = this.tabs().findIndex(
			(existingTab) =>
				existingTab.branchId === this.uiService.id.value &&
				existingTab.artifact?.id === artifact.id
		);
		if (currentIndex === -1) {
			this.tabs.update((rows) => [
				...rows,
				{
					tabType: 'Artifact',
					tabTitle: artifact.name,
					artifact: artifact,
					branchId: branchId,
					viewId: viewId,
				},
			]);
			this.SelectedIndex = this.tabs().length - 1;
		} else {
			this.SelectedIndex = currentIndex;
		}
	}

	removeTab(index: number) {
		this.tabs.update((rows) => rows.filter((_, i) => index !== i));
	}

	getTabIcon(tab: tab) {
		if (tab.tabType === 'Artifact' && tab.artifact) {
			return tab.artifact.icon.icon;
		} else if (tab.tabType === 'ChangeReport') {
			return 'differences';
		}
		return '';
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
		moveItemInArray(this.tabs(), event.previousIndex, event.currentIndex);
		if (this.selectedIndex() === event.previousIndex) {
			this.selectedIndex.set(event.currentIndex);
		}
	}
}
