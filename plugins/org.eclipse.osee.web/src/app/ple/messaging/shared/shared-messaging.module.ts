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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ColumnPreferencesDialogComponent } from './components/dialogs/column-preferences-dialog/column-preferences-dialog.component';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { EditEnumSetDialogComponent } from './components/dialogs/edit-enum-set-dialog/edit-enum-set-dialog.component';
import { MatTableModule } from '@angular/material/table';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { EditViewFreeTextFieldDialogComponent } from './components/dialogs/edit-view-free-text-field-dialog/edit-view-free-text-field-dialog.component';
import { MatIconModule } from '@angular/material/icon';
import { NewTypeDialogComponent } from './components/dialogs/new-type-dialog/new-type-dialog.component';
import { MatStepperModule } from '@angular/material/stepper';
import { PlatformTypeCardComponent } from './components/platform-type-card/platform-type-card.component';
import { EditTypeDialogComponent } from './components/dialogs/edit-type-dialog/edit-type-dialog.component';
import { MatCardModule } from '@angular/material/card';
import { MimHeaderComponent } from './components/mim-header/mim-header.component';
import { RouterModule } from '@angular/router';
import { EditEnumSetFieldComponent } from './components/edit-enum-set-field/edit-enum-set-field.component';
import { NewTypeFormComponent } from './components/dialogs/new-type-form/new-type-form.component';
import { NewAttributeFormFieldComponent } from './components/dialogs/new-attribute-form-field/new-attribute-form-field.component';
import { EnumSetFormComponent } from './components/dialogs/enum-set-form/enum-set-form.component';
import { EnumFormComponent } from './components/dialogs/enum-form/enum-form.component';
import { AffectedArtifactDialogComponent } from './components/dialogs/affected-artifact-dialog/affected-artifact-dialog.component';
import { NewTransportTypeDialogComponent } from './components/dialogs/new-transport-type-dialog/new-transport-type-dialog.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { UniquePlatformTypeNameDirective } from './directives/validators/unique-platform-type-name.directive';
import { UniquePlatformTypeAttributesDirective } from './directives/validators/unique-platform-type-attributes.directive';
import { MatOptionLoadingModule } from '../../../shared-components/mat-option-loading/mat-option-loading.module';

@NgModule({
	declarations: [
		ColumnPreferencesDialogComponent,
		EditEnumSetDialogComponent,
		EditViewFreeTextFieldDialogComponent,
		NewTypeDialogComponent,
		PlatformTypeCardComponent,
		EditTypeDialogComponent,
		MimHeaderComponent,
		EditEnumSetFieldComponent,
		NewTypeFormComponent,
		NewAttributeFormFieldComponent,
		EnumSetFormComponent,
		EnumFormComponent,
		AffectedArtifactDialogComponent,
		NewTransportTypeDialogComponent,
		UniquePlatformTypeNameDirective,
		UniquePlatformTypeAttributesDirective,
	],
	imports: [
		CommonModule,
		MatDialogModule,
		RouterModule,
		MatCardModule,
		MatFormFieldModule,
		MatListModule,
		MatCheckboxModule,
		MatTableModule,
		MatSelectModule,
		MatStepperModule,
		MatInputModule,
		MatSlideToggleModule,
		FormsModule,
		MatButtonModule,
		MatStepperModule,
		MatOptionLoadingModule,
		MatIconModule,
	],
	exports: [
		ColumnPreferencesDialogComponent,
		EditEnumSetDialogComponent,
		NewTypeDialogComponent,
		PlatformTypeCardComponent,
	],
})
export class SharedMessagingModule {}
