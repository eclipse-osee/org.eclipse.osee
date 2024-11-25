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
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { SeparatedFeatureSelectorComponent } from '../../dropdowns/separated-feature-selector/separated-feature-selector.component';
import {
	PLAddCompoundApplicabilityData,
	applicability,
	compApplicRelationshipStructure,
} from '../../types/pl-config-compound-applicabilities';

@Component({
	selector: 'osee-add-compound-applicability-dialog',
	templateUrl: './add-compound-applicability-dialog.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatLabel,
		MatFormField,
		MatSelect,
		MatOption,
		MatButton,
		MatDialogActions,
		MatDialogClose,
		SeparatedFeatureSelectorComponent,
	],
})
export class AddCompoundApplicabilityDialogComponent {
	dialogRef =
		inject<MatDialogRef<AddCompoundApplicabilityDialogComponent>>(
			MatDialogRef
		);
	data = inject<PLAddCompoundApplicabilityData>(MAT_DIALOG_DATA);

	relationships = compApplicRelationshipStructure;

	onNoClick(): void {
		this.dialogRef.close();
	}

	constructName(): string {
		this.data.compoundApplicability.name =
			this.data.compoundApplicability.applicabilities
				.map((value, index) => {
					const currRelationship =
						this.data.compoundApplicability.relationships[index] ==
						undefined
							? ''
							: this.data.compoundApplicability.relationships[
									index
								];
					return (
						' ' +
						value.featureName +
						' = ' +
						value.featureValue +
						' ' +
						currRelationship
					);
				})
				.join('');

		return this.data.compoundApplicability.name;
	}

	// user clicks 'add applicability' -> make new applic and push new values into the arrays
	addApplicabilitySection(): void {
		const newApplic: applicability = { featureName: '', featureValue: '' };
		this.data.compoundApplicability.applicabilities.push(newApplic);
		this.data.compoundApplicability.relationships.push('');
	}

	valueTracker<T>(index: number, _item: T) {
		return index;
	}
}
