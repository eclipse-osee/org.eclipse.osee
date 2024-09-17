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
import { AsyncPipe } from '@angular/common';
import { Component, computed, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import { filter, switchMap, take } from 'rxjs';
import { AddProductTypeDialogComponent } from '../../dialogs/add-product-type-dialog/add-product-type-dialog.component';
import { EditProductTypeDialogComponent } from '../../dialogs/edit-product-type-dialog/edit-product-type-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import {
	DefaultProductType,
	ProductType,
} from '../../types/pl-config-product-types';
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CurrentBranchInfoService, branchImpl } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-product-type-dropdown',
	templateUrl: './product-type-drop-down.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatMenuItem,
		MatMenuTrigger,
		MatMenuContent,
		MatMenu,
		MatIcon,
		AsyncPipe,
	],
})
export class ProductTypeDropDownComponent {
	//TODO add real prefs
	private _branchInfoService = inject(CurrentBranchInfoService);
	private _branch = toSignal(
		this._branchInfoService.currentBranch.pipe(takeUntilDestroyed()),
		{
			initialValue: new branchImpl(),
		}
	);
	protected editable = computed(() => this._branch().branchType === '0');
	productTypes = this.currentBranchService.productTypes;
	constructor(
		private currentBranchService: PlConfigCurrentBranchService,
		public dialog: MatDialog
	) {}

	addProductType() {
		this.dialog
			.open(AddProductTypeDialogComponent, {
				data: new DefaultProductType(),
				width: '80vw',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((result) => result !== undefined),
				switchMap((result) =>
					this.currentBranchService.createProductType(result)
				)
			)
			.subscribe();
	}

	editProductType(type: ProductType) {
		this.dialog
			.open(EditProductTypeDialogComponent, {
				data: type,
				width: '80vw',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((result) => result !== undefined),
				switchMap((result) =>
					this.currentBranchService.updateProductType(result)
				)
			)
			.subscribe();
	}

	deleteProductType(id: string) {
		this.currentBranchService.deleteProductType(id).subscribe();
	}

	toggleMenu(menuTrigger: MatMenuTrigger) {
		menuTrigger.toggleMenu();
	}
}
