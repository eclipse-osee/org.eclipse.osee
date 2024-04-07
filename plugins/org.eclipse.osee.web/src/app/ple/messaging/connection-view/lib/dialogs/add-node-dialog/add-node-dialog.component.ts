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
import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import {
	MatStep,
	MatStepper,
	MatStepperNext,
	MatStepperPrevious,
} from '@angular/material/stepper';
import { MatTooltip } from '@angular/material/tooltip';
import { nodeData } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { BehaviorSubject, debounceTime, map, switchMap } from 'rxjs';
import { AddNodeDialog } from '../../dialogs/add-node-dialog/add-node-dialog';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';
import { CurrentGraphService } from '../../services/current-graph.service';

@Component({
	selector: 'osee-add-node-dialog',
	standalone: true,
	imports: [
		CommonModule,
		MatDialogActions,
		MatDialogClose,
		MatDialogContent,
		MatDialogTitle,
		MatStepper,
		MatStep,
		MatStepperPrevious,
		MatStepperNext,
		MatLabel,
		MatFormField,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatButton,
		MatInput,
		MatTooltip,
		MatOption,
		MatOptionLoadingComponent,
		NewNodeFormComponent,
	],
	templateUrl: './add-node-dialog.component.html',
})
export class AddNodeDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<AddNodeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: AddNodeDialog,
		private graphService: CurrentGraphService
	) {}

	paginationSize = 10;
	nodeSearch = new BehaviorSubject<string>('');
	selectedNode: nodeData | undefined = undefined;

	availableNodes = this.nodeSearch.pipe(
		debounceTime(250),
		map(
			(search) => (pageNum: number | string) =>
				this.graphService.getPaginatedNodesByName(
					search,
					pageNum,
					this.paginationSize
				)
		)
	);

	availableNodesCount = this.nodeSearch.pipe(
		debounceTime(250),
		switchMap((search) => this.graphService.getNodesByNameCount(search))
	);

	createNew() {
		this.data.node.id = '-1';
		this.selectedNode = undefined;
	}

	isDisabledOption(option: nodeData) {
		return (
			this.data.connection.nodes.filter((node) => node.id === option.id)
				.length > 0
		);
	}

	applySearchTerm(searchTerm: Event) {
		const value = (searchTerm.target as HTMLInputElement).value;
		this.nodeSearch.next(value);
	}

	selectExistingNode(node: nodeData) {
		this.selectedNode = node;
	}

	moveToReview(stepper: MatStepper) {
		if (this.selectedNode) {
			this.data.node = this.selectedNode;
		}
		this.moveToStep(3, stepper);
	}

	moveToStep(index: number, stepper: MatStepper) {
		stepper.selectedIndex = index - 1;
	}

	onNoclick() {
		this.dialogRef.close();
	}
}
