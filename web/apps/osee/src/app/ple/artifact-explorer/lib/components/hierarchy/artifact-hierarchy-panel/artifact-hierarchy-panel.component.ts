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
import { AsyncPipe } from '@angular/common';
import { Component, computed, inject, input, output } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	BranchPickerComponent,
	CurrentViewSelectorComponent,
} from '@osee/shared/components';
import { CurrentBranchInfoService, UiService } from '@osee/shared/services';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { ArtifactSearchComponent } from '../artifact-search-panel/artifact-search/artifact-search.component';
import { BranchManagementPanelComponent } from '../branch-management-panel/branch-management-panel.component';

export type HierarchySection = 'hierarchy' | 'search' | 'branch';

@Component({
	selector: 'osee-artifact-hierarchy-panel',
	imports: [
		AsyncPipe,
		BranchPickerComponent,
		ArtifactHierarchyComponent,
		CurrentViewSelectorComponent,
		ArtifactSearchComponent,
		CdkDropList,
		BranchManagementPanelComponent,
	],
	templateUrl: './artifact-hierarchy-panel.component.html',
	styles: [
		`
			:host {
				--mdc-filled-text-field-container-color: transparent;
				--mat-form-field-container-color: transparent;
				--mat-option-selected-state-label-text-color: var(
					--osee-primary-default
				);
				--mat-minimal-pseudo-checkbox-selected-checkmark-color: var(
					--osee-primary-default
				);
			}
			.hierarchy-scroll::-webkit-scrollbar {
				width: 12px;
				height: 12px;
			}
			.hierarchy-scroll::-webkit-scrollbar-thumb {
				background: var(--osee-scrollbar-thumb, rgba(128, 128, 128, 0.4));
				border-radius: 6px;
			}
			.hierarchy-scroll::-webkit-scrollbar-track {
				background: transparent;
			}
			.hierarchy-scroll {
				scrollbar-width: auto;
			}
		`,
	],
})
export class ArtifactHierarchyPanelComponent {
	private artHierPathService = inject(ArtifactHierarchyPathService);
	private uiService = inject(UiService);

	/** Active section driven by the parent activity bar. */
	activeSection = input.required<HierarchySection>();

	/** Emits when the search "Show in Hierarchy" action is used. */
	navigateToHierarchy = output<void>();

	protected branchType = toSignal(this.uiService.type, { initialValue: '' });
	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	protected paths = this.artHierPathService.getPaths();

	branchIdValid = computed(
		() =>
			this.branchId() !== '' &&
			this.branchId() !== '-1' &&
			this.branchId() !== '0'
	);

	private currBranchInfoService = inject(CurrentBranchInfoService);
	branchHasPleCategory = this.currBranchInfoService.branchHasPleCategory;
}
