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
import { Component, Inject, computed, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { user } from '@osee/shared/types/auth';
import {
	PRIORITIES,
	WorkType,
	actionableItem,
} from '@osee/shared/types/configuration-management';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { filter, shareReplay, switchMap, tap } from 'rxjs/operators';
import { LatestActionDropDownComponent } from '../../latest-action-drop-down/latest-action-drop-down.component';
import { ActionUserService } from '../../action-state-button/internal/services/action-user.service';
import { CreateAction } from '@osee/configuration-management/create-action/types';
import { CreateActionService } from '@osee/configuration-management/create-action/services';
/**
 * Dialog for creating a new action with the correct workType and category.
 */
@Component({
	selector: 'osee-create-action-dialog',
	templateUrl: './create-action-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatIconButton,
		MatSuffix,
		MatIcon,
		MatOption,
		MatSelect,
		MatCheckbox,
		MatButton,
		MatDialogActions,
		MatDialogClose,
		LatestActionDropDownComponent,
	],
})
export class CreateActionDialogComponent {
	actionableItemId = new BehaviorSubject<string>('');
	users = this.userService.usersSorted;
	actionableItemsFilter = signal('');
	actionableItems = toSignal(
		this.createActionService.actionableItems.pipe(
			tap((items) => {
				if (items.length === 1) {
					this._selectActionableItem(items[0]);
				}
			}),
			takeUntilDestroyed()
		)
	);
	filteredActionableItems = computed(
		() =>
			this.actionableItems()?.filter((wt) =>
				wt.name
					.toLowerCase()
					.includes(this.actionableItemsFilter().toLowerCase())
			) || []
	);
	workTypesFilter = signal('');
	workTypes = toSignal(
		this.createActionService.workTypes.pipe(
			tap((types) => {
				types.forEach((t) => {
					if (t.name === this.data.defaultWorkType) {
						this.workType = t;
						this.data.createBranchDefault = t.createBranchDefault;
						return;
					}
				});
			})
		)
	);
	filteredWorkTypes = computed(
		() =>
			this.workTypes()?.filter((wt) =>
				wt.name
					.toLowerCase()
					.includes(this.workTypesFilter().toLowerCase())
			) || []
	);
	points = this.createActionService.getPoints();
	workType: WorkType = {
		name: '',
		humanReadableName: '',
		description: '',
		createBranchDefault: false,
	};
	selectedAssignees: user[] = [];
	targetedVersions = this.actionableItemId.pipe(
		filter((id) => id !== ''),
		switchMap((id) =>
			combineLatest([
				this.createActionService.currentBranch,
				this.createActionService.getVersions(id),
			]).pipe(
				tap(([branch, versions]) => {
					versions.forEach((v) => {
						if (v.name === branch.name) {
							this.data.targetedVersion = v;
							return;
						}
					});
				}),
				switchMap(([_, versions]) => of(versions))
			)
		)
	);
	changeTypes = this.actionableItemId.pipe(
		filter((id) => id !== ''),
		switchMap((id) => this.createActionService.getChangeTypes(id))
	);
	additionalFields = this.actionableItemId.pipe(
		filter((id) => id !== ''),
		switchMap((id) => this.createActionService.getCreateActionFields(id))
	);
	teamDef = this.actionableItemId.pipe(
		filter((id) => id !== ''),
		switchMap((id) => this.createActionService.getTeamDef(id)),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	featureGroups = this.teamDef.pipe(
		filter((t) => t !== undefined && t !== null && t.length > 0),
		switchMap((teams) =>
			this.createActionService.getFeatureGroups(teams[0].id)
		)
	);
	sprints = this.teamDef.pipe(
		filter((t) => t !== undefined && t !== null && t.length > 0),
		switchMap((teams) => this.createActionService.getSprints(teams[0].id))
	);
	private _priorityKeys = Object.keys(PRIORITIES);
	private _priorityValues = Object.values(PRIORITIES);
	priorities = this._priorityKeys.map((item, row) => {
		return {
			name: item.split(/(?=[A-Z])/).join(' '),
			value: this._priorityValues[row],
		};
	});

	constructor(
		public dialogRef: MatDialogRef<CreateActionDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: CreateAction,
		public createActionService: CreateActionService,
		public userService: ActionUserService
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}
	selectActionableItem(selection: MatAutocompleteSelectedEvent) {
		this._selectActionableItem(selection.option.value);
	}

	private _selectActionableItem(ai: actionableItem) {
		this.data.actionableItem = ai;
		this.actionableItemId.next(ai.id);
	}

	selectWorkType(selection: MatAutocompleteSelectedEvent) {
		this._selectWorkType(selection.option.value);
	}

	private _selectWorkType(workType: WorkType) {
		this.workType = workType;
		this.createActionService.workTypeValue = this.workType.name;
		this.data.createBranchDefault = this.workType.createBranchDefault;
		this._selectActionableItem(new actionableItem());
	}

	selectAssignees(selection: MatSelectChange) {
		const selections: user[] = selection.value;
		this.data.assignees = selections.map((s) => s.id).join(',');
	}

	compareUsers(user1: user, user2: user) {
		return user1.id === user2.id;
	}

	clearWorkType(event: Event) {
		event.stopPropagation();
		this.workTypesFilter.set('');
		this._selectWorkType({
			name: '',
			humanReadableName: '',
			description: '',
			createBranchDefault: false,
		});
	}

	clearActionableItem(event: Event) {
		event.stopPropagation();
		this.actionableItemsFilter.set('');
		this._selectActionableItem(new actionableItem());
	}

	updateWorkTypeFilter(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		this.workTypesFilter.set(value);
	}

	updateActionableItemsFilter(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		this.actionableItemsFilter.set(value);
	}

	displayFn(val: WorkType | actionableItem) {
		return val?.name;
	}
}
