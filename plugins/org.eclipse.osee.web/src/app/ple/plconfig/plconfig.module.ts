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
//Base angular imports
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

//Base component imports
import { PlconfigRoutingModule } from './plconfig-routing.module';
import { PlconfigComponent } from './plconfig.component';

//Angular material imports & forms
import {
	MatRadioModule,
	MAT_RADIO_DEFAULT_OPTIONS,
} from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
//import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatListModule } from '@angular/material/list';
import { FormsModule } from '@angular/forms';

//Sub-component imports
import { ApplicabilityTableComponent } from './lib/table/applicability-table/applicability-table.component';
import { ConfigurationDropdownComponent } from './lib/dropdowns/configuration-dropdown/configuration-dropdown.component';
import { EditConfigurationDialogComponent } from './lib/dialogs/edit-config-dialog/edit-config-dialog.component';
import { AddConfigurationDialogComponent } from './lib/dialogs/add-configuration-dialog/add-configuration-dialog.component';
import { FeatureDropdownComponent } from './lib/dropdowns/feature-dropdown/feature-dropdown.component';
import { AddFeatureDialogComponent } from './lib/dialogs/add-feature-dialog/add-feature-dialog.component';
import { EditFeatureDialogComponent } from './lib/dialogs/edit-feature-dialog/edit-feature-dialog.component';
import { CopyConfigurationDialogComponent } from './lib/dialogs/copy-configuration-dialog/copy-configuration-dialog.component';
import { ConfigurationGroupDropdownComponent } from './lib/dropdowns/configuration-group-dropdown/configuration-group-dropdown.component';
import { AddConfigurationGroupDialogComponent } from './lib/dialogs/add-configuration-group-dialog/add-configuration-group-dialog.component';
import { ConfigGroupDialogComponent } from './lib/dialogs/config-group-dialog/config-group-dialog.component';

import { PleSharedMaterialModule } from '../ple-shared-material/ple-shared-material.module';
import { FeatureMenuComponent } from './lib/menus/feature-menu/feature-menu.component';
import { ArrayDiffMenuComponent } from './lib/menus/array-diff-menu/array-diff-menu.component';
import { ConfigMenuComponent } from './lib/menus/config-menu/config-menu.component';
import { ConfigGroupMenuComponent } from './lib/menus/config-group-menu/config-group-menu.component';
import { ValueMenuComponent } from './lib/menus/value-menu/value-menu.component';
import { ProductTypeDropDownComponent } from './lib/dropdowns/product-type-drop-down/product-type-drop-down.component';
import { AddProductTypeDialogComponent } from './lib/dialogs/add-product-type-dialog/add-product-type-dialog.component';
import { EditProductTypeDialogComponent } from './lib/dialogs/edit-product-type-dialog/edit-product-type-dialog.component';
import { BranchPickerComponent } from 'src/app/shared/components/branch-picker/branch-picker/branch-picker.component';
import { ActionDropDownComponent } from 'src/app/shared/components/action-state-button/action-drop-down/action-drop-down.component';

@NgModule({
	declarations: [
		PlconfigComponent,
		ApplicabilityTableComponent,
		ConfigurationDropdownComponent,
		EditConfigurationDialogComponent,
		AddConfigurationDialogComponent,
		FeatureDropdownComponent,
		AddFeatureDialogComponent,
		EditFeatureDialogComponent,
		CopyConfigurationDialogComponent,
		ConfigurationGroupDropdownComponent,
		AddConfigurationGroupDialogComponent,
		ConfigGroupDialogComponent,
		FeatureMenuComponent,
		ArrayDiffMenuComponent,
		ConfigMenuComponent,
		ConfigGroupMenuComponent,
		ValueMenuComponent,
		ProductTypeDropDownComponent,
		AddProductTypeDialogComponent,
		EditProductTypeDialogComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		MatRadioModule,
		MatSelectModule,
		MatTableModule,
		MatSortModule,
		MatPaginatorModule,
		MatInputModule,
		MatMenuModule,
		BranchPickerComponent,
		PleSharedMaterialModule,
		MatDialogModule,
		MatSlideToggleModule,
		MatTooltipModule,
		MatIconModule,
		MatProgressSpinnerModule,
		MatListModule,
		ActionDropDownComponent,
		PlconfigRoutingModule,
	],
	providers: [
		{
			provide: MAT_RADIO_DEFAULT_OPTIONS,
			useValue: { color: 'primary' },
		},
	],
})
export class PlconfigModule {}
