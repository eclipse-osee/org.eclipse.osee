/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, effect, inject, input } from '@angular/core';
import { BranchCategoryService } from '../../internal/services/branch-category.service';
import { BranchSelectorComponent } from '../internal/components/branch-selector/branch-selector.component';
import { BranchTypeSelectorComponent } from '../internal/components/branch-type-selector/branch-type-selector.component';
import { workType } from '@osee/shared/types/configuration-management';
import { WorktypeService } from '@osee/shared/services';

@Component({
	selector: 'osee-branch-picker',
	imports: [BranchTypeSelectorComponent, BranchSelectorComponent],
	template: `<div class="tw-flex tw-flex-col">
		<osee-branch-type-selector />
		<osee-branch-selector />
	</div>`,
})
export class BranchPickerComponent {
	private branchCategoryService = inject(BranchCategoryService);
	private workTypeService = inject(WorktypeService);

	category = input<`${number}`>('-1');
	excludeCategory = input<`${number}`>('-1');
	workType = input<workType>('None');

	private _categoryEffect = effect(
		() => (this.branchCategoryService.category = this.category())
	);
	private _excludeCategoryEffect = effect(
		() =>
			(this.branchCategoryService.excludeCategory =
				this.excludeCategory())
	);
	private _workTypeEffect = effect(
		() => (this.workTypeService.workType = this.workType())
	);
}
