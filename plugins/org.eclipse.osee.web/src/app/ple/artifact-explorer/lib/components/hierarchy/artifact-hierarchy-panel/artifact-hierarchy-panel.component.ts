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
import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BehaviorSubject, map } from 'rxjs';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { MatExpansionModule } from '@angular/material/expansion';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ArtifactHierarchyOptionsComponent } from '../artifact-hierarchy-options/artifact-hierarchy-options.component';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIconModule } from '@angular/material/icon';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ArtifactSearchPanelComponent } from '../artifact-search-panel/artifact-search-panel.component';

@Component({
	selector: 'osee-artifact-hierarchy-panel',
	standalone: true,
	imports: [
		CommonModule,
		BranchPickerComponent,
		MatExpansionModule,
		ArtifactHierarchyComponent,
		DragDropModule,
		ArtifactHierarchyOptionsComponent,
		ViewSelectorComponent,
		MatIconModule,
		MatTooltipModule,
		ActionDropDownComponent,
		ArtifactSearchPanelComponent,
	],
	templateUrl: './artifact-hierarchy-panel.component.html',
})
export class ArtifactHierarchyPanelComponent {
	private uiService = inject(UiService);
	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });
	protected paths = this.artHierPathService.getPaths();

	branchName = toSignal(
		this.currentBranchService.currentBranch.pipe(
			map((branch) => branch.name)
		)
	);

	branchIdValid = computed(
		() =>
			this.branchId() !== '' &&
			this.branchId() !== '-1' &&
			this.branchId() !== '0'
	);

	constructor(
		private artHierPathService: ArtifactHierarchyPathService,
		private tabService: ArtifactExplorerTabService,
		private currentBranchService: CurrentBranchInfoService
	) {}

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(true);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}

	openChangeReport() {
		this.tabService.addTab(
			'ChangeReport',
			'Change Report - ' + this.branchName()
		);
	}
}
