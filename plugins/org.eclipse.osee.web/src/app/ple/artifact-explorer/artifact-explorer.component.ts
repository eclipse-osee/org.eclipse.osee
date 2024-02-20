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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { UiService } from '@osee/shared/services';
import { ArtifactTabGroupComponent } from './lib/components/artifact-tab-group/artifact-tab-group.component';
import { ArtifactHierarchyPanelComponent } from './lib/components/hierarchy/artifact-hierarchy-panel/artifact-hierarchy-panel.component';
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ArtifactExplorerTabService } from './lib/services/artifact-explorer-tab.service';
import { tab } from './lib/types/artifact-explorer.data';

@Component({
	selector: 'osee-artifact-explorer',
	standalone: true,
	imports: [
		CommonModule,
		ArtifactHierarchyPanelComponent,
		MatSidenavModule,
		MatButtonModule,
		MatIconModule,
		ArtifactTabGroupComponent,
		DragDropModule,
		MatMenuModule,
		MatTooltipModule,
	],
	templateUrl: './artifact-explorer.component.html',
})
export class ArtifactExplorerComponent {
	@Input() set branchType(branchType: 'working' | 'baseline' | '') {
		if (branchType != undefined) {
			this.uiService.typeValue = branchType;
		} else {
			this.uiService.typeValue = '';
		}
	}
	@Input() set branchId(branchId: string) {
		if (branchId != undefined) {
			this.uiService.idValue = branchId;
		} else {
			this.uiService.idValue = '';
		}
	}

	@Input() set viewId(viewId: string) {
		if (viewId != undefined) {
			this.uiService.viewIdValue = viewId;
		} else {
			this.uiService.viewIdValue = '-1';
		}
	}

	openTabs = this.tabService.Tabs;
	selectedTabIndex = this.tabService.selectedIndex;

	constructor(
		private uiService: UiService,
		private tabService: ArtifactExplorerTabService
	) {}

	setSelectedTab(index: number) {
		this.tabService.SelectedIndex = index;
	}
	removeTab(event: MouseEvent, index: number) {
		event.stopPropagation();
		this.tabService.removeTab(index);
	}

	fetchIcon(tab: tab) {
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

export default ArtifactExplorerComponent;
