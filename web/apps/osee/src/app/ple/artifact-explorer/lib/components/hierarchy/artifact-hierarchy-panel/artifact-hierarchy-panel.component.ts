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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import {
	ActionDropDownComponent,
	BranchPickerComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { BehaviorSubject, map } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactHierarchyOptionsComponent } from '../artifact-hierarchy-options/artifact-hierarchy-options.component';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { ArtifactSearchPanelComponent } from '../artifact-search-panel/artifact-search-panel.component';
import { ArtifactExplorerExpansionPanelComponent } from '../../shared/artifact-explorer-expansion-panel/artifact-explorer-expansion-panel.component';

@Component({
	selector: 'osee-artifact-hierarchy-panel',
	standalone: true,
	imports: [
		NgClass,
		AsyncPipe,
		BranchPickerComponent,
		ArtifactHierarchyComponent,
		ArtifactHierarchyOptionsComponent,
		ViewSelectorComponent,
		ActionDropDownComponent,
		ArtifactSearchPanelComponent,
		ArtifactExplorerExpansionPanelComponent,
		MatTooltip,
		MatIcon,
		CdkDropList,
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

	openChangeReport() {
		this.tabService.addTab(
			'ChangeReport',
			'Change Report - ' + this.branchName()
		);
	}
}
