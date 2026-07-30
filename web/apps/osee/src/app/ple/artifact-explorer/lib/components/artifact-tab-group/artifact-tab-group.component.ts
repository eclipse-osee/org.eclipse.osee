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
	private dirtyService = inject(ArtifactEditorDirtyService);

	tabs = this.tabService.Tabs;

	connections = computed(() => this.tabs().map((_, i) => '' + i));

	selectedIndex = this.tabService.selectedIndex;

	removeTab(index: number) {
		if (this.dirtyService.hasDirtyEditors()) {
			const confirmed = confirm(
				'You have unsaved changes.\n\n' +
					'To save your work, click Cancel, then click outside the text field you were editing. ' +
					'Changes are saved automatically when an editor loses focus.\n\n' +
					'Click OK to discard changes and close this tab.'
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
