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
import { Component, computed, inject } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatTab, MatTabGroup, MatTabLabel } from '@angular/material/tabs';
import { MatTooltip } from '@angular/material/tooltip';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { ArtifactEditorDirtyService } from '../../services/artifact-editor-dirty.service';
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
		MatTabLabel,
		MatTooltip,
		ArtifactEditorComponent,
	],
	templateUrl: './artifact-tab-group.component.html',
	styles: [
		`
			:host ::ng-deep .mat-mdc-tab-header {
				border-bottom: 1px solid var(--osee-background-hover, rgba(128, 128, 128, 0.2));
				--mdc-tab-indicator-active-indicator-height: 2px;
				--mdc-secondary-navigation-tab-container-height: 36px;
			}
			:host ::ng-deep .mdc-tab {
				min-width: 0 !important;
				padding: 0 !important;
			}
			:host ::ng-deep .mdc-tab--active {
				opacity: 1;
			}
			:host ::ng-deep .mdc-tab:not(.mdc-tab--active) {
				opacity: 0.6;
			}
			:host ::ng-deep .mdc-tab:not(.mdc-tab--active):hover {
				opacity: 0.85;
			}
			:host ::ng-deep .mdc-tab__content {
				padding: 0 !important;
				gap: 0 !important;
			}
			:host ::ng-deep .mdc-tab__text-label {
				padding: 0 14px;
			}
			/* Hide close button by default, show on tab hover */
			:host ::ng-deep .mdc-tab .tab-close-btn {
				opacity: 0;
				transition: opacity 0.15s;
			}
			:host ::ng-deep .mdc-tab:hover .tab-close-btn,
			:host ::ng-deep .mdc-tab--active .tab-close-btn {
				opacity: 0.6;
			}
			:host ::ng-deep .mdc-tab .tab-close-btn:hover {
				opacity: 1;
				background: var(--osee-background-hover, rgba(128, 128, 128, 0.3));
			}
		`,
	],
})
export class ArtifactTabGroupComponent {
	private tabService = inject(ArtifactExplorerTabService);
	private dirtyService = inject(ArtifactEditorDirtyService);

	tabs = this.tabService.Tabs;

	connections = computed(() => this.tabs().map((_, i) => '' + i));

	selectedIndex = this.tabService.selectedIndex;

	removeTab(index: number) {
		if (this.dirtyService.hasDirtyEditors()) {
			const confirmed = confirm(
				'You have unsaved changes. Are you sure you want to close this tab?'
			);
			if (!confirmed) {
				return;
			}
		}
		this.tabService.removeTab(index);
	}

	onTabDropped(event: CdkDragDrop<unknown[]>) {
		this.tabService.onTabDropped(event);
	}

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
