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
import { Component, Inject } from '@angular/core';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import {
	applicability,
	compApplicRelationshipStructure,
} from '../../types/pl-config-compound-applicabilities';
import { PLAddCompoundApplicabilityData } from '../../types/pl-config-compound-applicabilities';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';

@Component({
	selector: 'osee-add-compound-applicability-dialog',
	templateUrl: './add-compound-applicability-dialog.component.html',
	styleUrls: ['./add-compound-applicability-dialog.component.sass'],
	standalone: true,
	imports: [
		NgFor,
		NgIf,
		AsyncPipe,
		FormsModule,
		MatDialogModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MatButtonModule,
	],
})
export class AddCompoundApplicabilityDialogComponent {
	features = this.currentBranchService.branchApplicFeatures;
	relationships = compApplicRelationshipStructure;
	inclusionOptions: string[] = ['Included', 'Excluded'];

	constructor(
		private currentBranchService: PlConfigCurrentBranchService,
		public dialogRef: MatDialogRef<AddCompoundApplicabilityDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: PLAddCompoundApplicabilityData
	) {}

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
		var newApplic: applicability = { featureName: '', featureValue: '' };
		this.data.compoundApplicability.applicabilities.push(newApplic);
		this.data.compoundApplicability.relationships.push('');
	}

	isCompoundApplic(name: string) {
		return name.includes(' | ') || name.includes(' & ');
	}

	valueTracker<T>(index: number, item: T) {
		return index;
	}
}
