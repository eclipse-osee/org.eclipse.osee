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
import { tab } from '../types/artifact-explorer';
import { ArtifactIconService } from './artifact-icon.service';
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import { teamWorkflowToken } from '@osee/shared/types/configuration-management';

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

	generateTabId() {
		return (performance.now() * Math.random()).toString();
	}

	addTab(tab: tab) {
		this.tabs.update((rows) => [...rows, tab]);
		this.SelectedIndex = this.tabs().length - 1;
	}

	addChangeReportTab(tabTitle: string) {
		let newTab: tab = {
			tabId: this.generateTabId(),
			tabType: 'ChangeReport',
			tabTitle,
			branchId: this.branchId(),
			viewId: this.viewId(),
		};
		this.addTab(newTab);
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
				existingTab.branchId === this.uiService.id.value &&
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
			viewId: viewId,
		});
	}

	addTeamWorkflowTab(teamWorkflow: teamWorkflowToken) {
		// don't open a tab for an action if it's already open
		const currentIndex = this.tabs().findIndex(
			(existingTab) =>
				existingTab.tabType === 'TeamWorkflow' &&
				existingTab.teamWorkflowId === teamWorkflow.id
		);
		if (currentIndex !== -1) {
			this.SelectedIndex = currentIndex;
			return;
		}

		this.addTab({
			tabId: this.generateTabId(),
			tabType: 'TeamWorkflow',
			tabTitle: teamWorkflow.atsId + ' - ' + teamWorkflow.name,
			teamWorkflowId: teamWorkflow.id,
			branchId: '570',
			viewId: '-1',
		});
	}

	removeTab(index: number) {
		this.tabs.update((rows) => rows.filter((_, i) => index !== i));
	}

	getTabIcon(tab: tab) {
		switch (tab.tabType) {
			case 'Artifact':
				return tab.artifact.icon.icon;
			case 'ChangeReport':
				return 'differences';
			case 'TeamWorkflow':
				return 'assignment';
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
		if (tab.tabType === 'TeamWorkflow') {
			return 'material-icons-outlined';
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
