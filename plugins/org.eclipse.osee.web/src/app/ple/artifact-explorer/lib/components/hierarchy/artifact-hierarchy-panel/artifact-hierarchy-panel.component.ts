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
import { CdkDropList } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
	ExpandIconComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { BehaviorSubject, map } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactHierarchyOptionsComponent } from '../artifact-hierarchy-options/artifact-hierarchy-options.component';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { ArtifactSearchPanelComponent } from '../artifact-search-panel/artifact-search-panel.component';

@Component({
	selector: 'osee-artifact-hierarchy-panel',
	standalone: true,
	imports: [
		CommonModule,
		BranchPickerComponent,
		ArtifactHierarchyComponent,
		ArtifactHierarchyOptionsComponent,
		ViewSelectorComponent,
		ActionDropDownComponent,
		ArtifactSearchPanelComponent,
		MatTooltip,
		MatIcon,
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		CdkDropList,
		ExpandIconComponent,
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
