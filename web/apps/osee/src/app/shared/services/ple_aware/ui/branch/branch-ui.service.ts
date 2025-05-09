/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { Injectable, inject } from '@angular/core';
import { BranchIdService } from './branch-id.service';
import { BranchTypeService } from './branch-type.service';
import { BranchCategoryService } from './branch-category.service';

@Injectable({
	providedIn: 'root',
})
export class BranchUIService {
	private typeService = inject(BranchTypeService);
	private idService = inject(BranchIdService);
	private categoryService = inject(BranchCategoryService);

	get id() {
		return this.idService.BranchId;
	}

	/**
	 * @deprecated Will be replacing .id with functionality of .idAsObservable()
	 */
	get idAsObservable() {
		return this.idService.BranchIdAsObservable;
	}

	get type() {
		return this.typeService.branchType;
	}

	set idValue(id: string | number) {
		this.idService.BranchIdValue = id;
	}

	set typeValue(branchType: 'working' | 'baseline' | '') {
		this.typeService.BranchType = branchType;
	}

	/**
	 * Category
	 */

	get category() {
		return this.categoryService.branchCategory;
	}

	set categoryValue(branchCategory: string) {
		this.categoryService.BranchCategory = branchCategory;
	}
}
