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
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTab, MatTabGroup, MatTabLabel } from '@angular/material/tabs';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { tab } from '../../types/artifact-explorer';
import { ChangeReportTableComponent } from '../change-report-table/change-report-table.component';
import { ArtifactEditorComponent } from '../editor/artifact-editor/artifact-editor.component';
import { ArtifactInfoPanelComponent } from '../editor/artifact-info-panel/artifact-info-panel.component';
import { RelationsEditorPanelComponent } from '../editor/relations-editor-panel/relations-editor-panel.component';
import { TeamWorkflowTabComponent } from '../editor/team-workflow-tab/team-workflow-tab.component';

@Component({
	selector: 'osee-artifact-tab-group',
	imports: [
		MatTabGroup,
		CdkDropList,
		MatTab,
		CdkDrag,
		MatIcon,
		MatIconButton,
		MatTabLabel,
		ArtifactInfoPanelComponent,
		RelationsEditorPanelComponent,
		ArtifactEditorComponent,
		TeamWorkflowTabComponent,
		ChangeReportTableComponent,
	],
	templateUrl: './artifact-tab-group.component.html',
})
export class ArtifactTabGroupComponent {
	private tabService = inject(ArtifactExplorerTabService);

	tabs = this.tabService.Tabs;

	connections = computed(() => this.tabs().map((_, i) => '' + i));

	selectedIndex = this.tabService.selectedIndex;

	removeTab(index: number) {
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
