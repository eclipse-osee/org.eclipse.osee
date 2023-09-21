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
import { Component, computed, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BehaviorSubject, filter, tap } from 'rxjs';
import {
	BranchPickerComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { MatExpansionModule } from '@angular/material/expansion';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ArtifactHierarchyOptionsComponent } from '../artifact-hierarchy-options/artifact-hierarchy-options.component';
import { UiService } from '@osee/shared/services';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { toSignal } from '@angular/core/rxjs-interop';

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
	],
	templateUrl: './artifact-hierarchy-panel.component.html',
})
export class ArtifactHierarchyPanelComponent {
	private uiService = inject(UiService);
	protected branchType = this.uiService.type;
	protected branchId = this.uiService.id;

	_branchId = toSignal(this.branchId, { initialValue: '' });
	changedBranchId = computed(
		() =>
			this._branchId() !== '' &&
			this._branchId() !== '-1' &&
			this._branchId() !== '0'
	);

	constructor() {}

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(true);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}
}
