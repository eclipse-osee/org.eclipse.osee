import { featureConstraintData } from './../../types/pl-config-feature-constraints';
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
import { MatButton } from '@angular/material/button';
import {
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { PlConfigUIStateService } from '@osee/plconfig';
import { XResultData } from '@osee/shared/types';
import { take } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { applicWithConstraints } from '../../types/pl-config-feature-constraints';

@Component({
	selector: 'osee-view-feature-constraints-dialog',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatDialogActions,
		MatButton,
	],
	templateUrl: './view-feature-constraints-dialog.component.html',
	styles: [],
})
export class ViewFeatureConstraintsDialogComponent {
	private uiStateService = inject(PlConfigUIStateService);
	currentBranchService = inject(PlConfigCurrentBranchService);
	dialogRef =
		inject<MatDialogRef<ViewFeatureConstraintsDialogComponent>>(
			MatDialogRef
		);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		this.currentBranchService.applicsWithFeatureConstraints.subscribe(
			(applics) => {
				this.applicsToDisplay = applics;
			}
		);
	}

	applicsWithFeatureConstraint$ =
		this.currentBranchService.applicsWithFeatureConstraints;
	applicsToDisplay: applicWithConstraints[] = [];
	displayedColumns: string[] = [
		'childApplic',
		'isOnlyApplicableIf',
		'parentApplic',
		'delete',
	];
	data: featureConstraintData = {
		featureConstraint: {
			applicability1: { id: '-1', name: '' },
			applicability2: { id: '-1', name: '' },
		},
	};

	onCancelClick(): void {
		this.dialogRef.close();
	}

	deleteFeatureConstraint(
		childApplicId: `${number}`,
		childApplicName: string,
		parentApplicId: `${number}`,
		parentApplicName: string
	) {
		if (
			childApplicId != '-1' &&
			childApplicName != '' &&
			parentApplicId != '-1' &&
			parentApplicName != ''
		) {
			this.data.featureConstraint.applicability1.id = childApplicId;
			this.data.featureConstraint.applicability1.name = childApplicName;
			this.data.featureConstraint.applicability2.id = parentApplicId;
			this.data.featureConstraint.applicability2.name = parentApplicName;
			this.currentBranchService
				.deleteFeatureConstraint(this.data)
				.pipe(take(1))
				.subscribe((response: XResultData) => {
					if (response.success) {
						this.uiStateService.updateReqConfig = true;
					}
				});
		}
	}
}
