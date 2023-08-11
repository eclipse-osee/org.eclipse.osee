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
import {
	Component,
	Input,
	OnChanges,
	OnInit,
	SimpleChanges,
} from '@angular/core';
import { BranchCategoryService } from '../../internal/services/branch-category.service';
import { BranchSelectorComponent } from '../internal/components/branch-selector/branch-selector.component';
import { BranchTypeSelectorComponent } from '../internal/components/branch-type-selector/branch-type-selector.component';

@Component({
	selector: 'osee-branch-picker',
	templateUrl: './branch-picker.component.html',
	standalone: true,
	imports: [BranchTypeSelectorComponent, BranchSelectorComponent],
})
export class BranchPickerComponent implements OnChanges {
	@Input() category: string = '0';
	@Input() actionSearch: boolean = false;
	constructor(private branchCategoryService: BranchCategoryService) {}
	ngOnChanges(changes: SimpleChanges): void {
		this.branchCategoryService.category = this.category;
		this.branchCategoryService.shouldActionSearch = this.actionSearch;
	}
}
