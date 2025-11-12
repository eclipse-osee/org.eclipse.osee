/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import {
	Component,
	computed,
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
	MatSelect,
	MatSelectChange,
} from '@angular/material/select';
import { LatestActionDropDownComponent } from '../../latest-action-drop-down/latest-action-drop-down.component';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { CreateActionService } from '@osee/configuration-management/services';
import { CreateAction } from '@osee/configuration-management/types';
import { user } from '@osee/shared/types/auth';
import {
	WorkType,
	PRIORITIES,
	actionableItem,
} from '@osee/shared/types/configuration-management';
import {
	BehaviorSubject,
	tap,
	filter,
	switchMap,
	combineLatest,
	of,
	shareReplay,
	map,
} from 'rxjs';
import { ActionUserService } from '../create-action-dialog/internal/action-user.service';
import { HasValidIdDirective } from '@osee/shared/validators';

@Component({
	selector: 'osee-create-action-form',
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatSuffix,
		MatIcon,
		MatOption,
		MatSelect,
		MatCheckbox,
		LatestActionDropDownComponent,
		HasValidIdDirective,
	],
	templateUrl: './create-action-form.component.html',
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class CreateActionFormComponent {
	data = input.required<CreateAction>();

	createActionService = inject(CreateActionService);
	userService = inject(ActionUserService);

	actionableItemId = new BehaviorSubject<string>('');
	users = this.userService.usersSorted;

	actionableItemsFilter = signal('');
	actionableItems = toSignal(
		this.createActionService.actionableItems.pipe(
			tap((items) => {
				if (items.length === 1) {
					this._selectActionableItem(items[0]);
				}
			})
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

	data$ = toObservable(this.data);

	workTypesFilter = signal('');
	workType = signal<WorkType>({
		name: '',
		humanReadableName: '',
		description: '',
		createBranchDefault: false,
	});
	workTypes = toSignal(
		this.createActionService.workTypes.pipe(map((types) => types)),
		{ initialValue: [] }
	);
	private _applyDefaultWorkType = effect(() => {
		const types = this.workTypes();
		const data = this.data();
		if (!data || !types || types.length === 0) return;
		const defaultType = types.find((t) => t.name === data.defaultWorkType);
		if (defaultType) {
			this.workType.set(defaultType);
			this.createActionService.workTypeValue = defaultType.name;
			data.createBranchDefault = defaultType.createBranchDefault;
		}
	});
	filteredWorkTypes = computed(
		() =>
			this.workTypes()?.filter((wt) =>
				wt.name
					.toLowerCase()
					.includes(this.workTypesFilter().toLowerCase())
			) || []
	);

	points = this.createActionService.getPoints();
	selectedAssignees: user[] = [];

	targetedVersions = this.actionableItemId.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) =>
			combineLatest([
				this.createActionService.currentBranch,
				this.createActionService.getVersions(id),
			]).pipe(
				tap(([branch, versions]) => {
					versions.forEach((v) => {
						if (v.name === branch.name) {
							this.data().targetedVersion = v;
							return;
						}
					});
				}),
				switchMap(([_, versions]) => of(versions))
			)
		)
	);

	changeTypes = this.actionableItemId.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) => this.createActionService.getChangeTypes(id))
	);

	additionalFields = this.actionableItemId.pipe(
		filter((id) => id !== ''),
		switchMap((id) => this.createActionService.getCreateActionFields(id))
	);

	teamDef = this.actionableItemId.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) => this.createActionService.getTeamDef(id)),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	featureGroups = this.teamDef.pipe(
		filter((t) => t && t.length > 0),
		switchMap((teams) =>
			this.createActionService.getFeatureGroups(teams[0].id)
		)
	);
	sprints = this.teamDef.pipe(
		filter((t) => t && t.length > 0),
		switchMap((teams) => this.createActionService.getSprints(teams[0].id))
	);

	private _priorityKeys = Object.keys(PRIORITIES);
	private _priorityValues = Object.values(PRIORITIES);
	priorities = this._priorityKeys.map((item, row) => ({
		name: item.split(/(?=[A-Z])/).join(' '),
		value: this._priorityValues[row],
	}));

	actionableItemText = signal('');
	selectActionableItem(selection: MatAutocompleteSelectedEvent) {
		const ai = selection.option.value as actionableItem;
		this._selectActionableItem(ai);
		this.actionableItemText.set(ai.name);
	}
	private _selectActionableItem(ai: actionableItem) {
		this.data().actionableItem = ai;
		this.actionableItemId.next(ai.id);
		const _workType = this.workTypes()?.find(
			(type) => type.name === ai.workType
		);
		if (_workType && _workType.name !== this.workType().name) {
			this.workType.set(_workType);
			this.data().createBranchDefault = _workType.createBranchDefault;
		}
	}
	selectWorkType(selection: MatAutocompleteSelectedEvent) {
		this._selectWorkType(selection.option.value);
	}
	private _selectWorkType(workType: WorkType) {
		this.workType.set(workType);
		this.createActionService.workTypeValue = this.workType().name;
		this.data().createBranchDefault = this.workType().createBranchDefault;
		this._selectActionableItem(new actionableItem());
	}
	selectAssignees(selection: MatSelectChange) {
		const selections: user[] = selection.value;
		this.data().assignees = selections.map((s) => s.id).join(',');
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
		this.actionableItemText.set('');
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
	displayFn(val: WorkType | actionableItem | string): string {
		if (typeof val === 'string') return val;
		return val?.name ?? '';
	}
}
