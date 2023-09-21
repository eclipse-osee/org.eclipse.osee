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
import { Component, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTabsModule } from '@angular/material/tabs';
import { ArtifactExplorerTabService } from '../../services/artifact-explorer-tab.service';
import { DragDropModule, CdkDragDrop } from '@angular/cdk/drag-drop';
import { ArtifactEditorComponent } from '../editor/artifact-editor/artifact-editor.component';
import { ArtifactInfoPanelComponent } from '../editor/artifact-info-panel/artifact-info-panel.component';
import { RelationsEditorPanelComponent } from '../editor/relations-editor-panel/relations-editor-panel.component';

@Component({
	selector: 'osee-artifact-tab-group',
	standalone: true,
	imports: [
		CommonModule,
		MatTabsModule,
		MatIconModule,
		MatButtonModule,
		DragDropModule,
		ArtifactInfoPanelComponent,
		RelationsEditorPanelComponent,
		ArtifactEditorComponent,
	],
	templateUrl: './artifact-tab-group.component.html',
})
export class ArtifactTabGroupComponent {
	tabs = this.tabService.Tabs;

	connections = computed(() => this.tabs().map((_, i) => '' + i));

	constructor(private tabService: ArtifactExplorerTabService) {}

	removeTab(index: number) {
		this.tabService.removeTab(index);
	}

	onTabDropped(event: CdkDragDrop<any[]>) {
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
}
