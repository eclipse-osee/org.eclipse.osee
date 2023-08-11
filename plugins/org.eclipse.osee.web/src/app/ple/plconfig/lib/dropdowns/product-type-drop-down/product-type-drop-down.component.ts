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
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { filter, switchMap, take } from 'rxjs';
import { AddProductTypeDialogComponent } from '../../dialogs/add-product-type-dialog/add-product-type-dialog.component';
import { EditProductTypeDialogComponent } from '../../dialogs/edit-product-type-dialog/edit-product-type-dialog.component';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import {
	DefaultProductType,
	ProductType,
} from '../../types/pl-config-product-types';

@Component({
	selector: 'osee-plconfig-product-type-dropdown',
	templateUrl: './product-type-drop-down.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatIconModule,
		MatMenuModule,
		MatFormFieldModule,
		NgFor,
		NgIf,
		AsyncPipe,
	],
})
export class ProductTypeDropDownComponent {
	editable = this.currentBranchService.branchApplicEditable;
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
