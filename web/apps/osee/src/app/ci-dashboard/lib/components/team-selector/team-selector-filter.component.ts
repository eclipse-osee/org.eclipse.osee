/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { DashboardService } from '../../services/dashboard.service';
import { CiDetailsTableService } from '../../services/ci-details-table.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	of,
	switchMap,
} from 'rxjs';
import { ScriptTeam } from '../../types/tmo';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Component({
	selector: 'osee-team-selector-filter',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOptionLoadingComponent,
		FormsModule,
		MatOption,
		MatIcon,
		MatSuffix,
	],
	template: `
		<mat-form-field
			subscriptSizing="dynamic"
			class="tw-w-full"
			(click)="$event.stopPropagation()">
			<input
				type="text"
				matInput
				[value]="filter()"
				(input)="updateFilter($event)"
				placeholder="Filter by team"
				(focusin)="openAutoComplete()"
				(focusout)="closeAutoComplete()"
				[matAutocomplete]="autocomplete" />
			@if (!autoCompleteOpened()) {
				<mat-icon matIconSuffix>arrow_drop_down</mat-icon>
			}
			@if (autoCompleteOpened() && filter() !== '') {
				<button
					mat-icon-button
					matIconSuffix
					class="tw-px-2"
					(mousedown)="clearFilter()">
					<mat-icon>close</mat-icon>
				</button>
			}
			<mat-autocomplete
				#autocomplete="matAutocomplete"
				(optionSelected)="selectTeam($event.option.value)"
				[displayWith]="displayFn">
				@if (teams() === undefined && teamsCount() === undefined) {
					<mat-option
						id="-1"
						disabled
						[value]="{ id: '-1', name: 'invalid' }">
						Loading...
					</mat-option>
				} @else {
					<osee-mat-option-loading
						[data]="teams()!"
						objectName="teams"
						[paginationSize]="pageSize"
						paginationMode="AUTO"
						[count]="teamsCount()!"
						[noneOption]="
							filter() === '' ? unassignedOption : undefined
						">
						<ng-template let-option>
							<mat-option
								[attr.data-cy]="
									'option-' +
									(option.name?.value || 'Unassigned')
								"
								[value]="option"
								[id]="option.id">
								@if (option.id === '-1') {
									Unassigned
								} @else {
									{{ option.name.value }}
								}
							</mat-option>
						</ng-template>
					</osee-mat-option-loading>
				}
			</mat-autocomplete>
		</mat-form-field>
	`,
})
export class TeamSelectorFilterComponent {
	private dashboardService = inject(DashboardService);
	private ciDetailsService = inject(CiDetailsTableService);

	pageSize = 100;
	unassignedOption: ScriptTeam = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			gammaId: '-1',
			typeId: '1152921504606847088',
			value: 'Unassigned',
		},
	};

	filter = signal('');
	private _filter$ = toObservable(this.filter).pipe(debounceTime(250));
	autoCompleteOpened = signal(false);
	private _autoCompleteOpened$ = toObservable(this.autoCompleteOpened);

	private _teams$ = this._autoCompleteOpened$.pipe(
		debounceTime(250),
		distinctUntilChanged(),
		filter((opened) => opened === true),
		switchMap((_) =>
			this._filter$.pipe(
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.dashboardService.getTeamsPaginated(
							filter,
							pageNum,
							this.pageSize,
							ATTRIBUTETYPEIDENUM.NAME
						)
					)
				)
			)
		)
	);

	teams = toSignal(this._teams$);

	private _teamsCount$ = this._autoCompleteOpened$.pipe(
		debounceTime(250),
		distinctUntilChanged(),
		filter((opened) => opened === true),
		switchMap((_) =>
			this._filter$.pipe(
				switchMap((filter) =>
					this.dashboardService.getTeamsCount(filter)
				)
			)
		)
	);

	teamsCount = toSignal(this._teamsCount$);

	selectTeam(team: ScriptTeam) {
		if (team.id === '-1') {
			// Unassigned: filter for defs with no team or "Unassigned" team
			this.filter.set('Unassigned');
			this.ciDetailsService.teamFilter.set('unassigned');
		} else {
			this.filter.set(team.name.value);
			this.ciDetailsService.teamFilter.set(team.id);
		}
	}

	openAutoComplete() {
		this.autoCompleteOpened.set(true);
	}

	closeAutoComplete() {
		this.autoCompleteOpened.set(false);
	}

	updateFilter(event: Event) {
		const filterValue = (event.target as HTMLInputElement).value;
		this.filter.set(filterValue);
	}

	clearFilter() {
		this.filter.set('');
		this.ciDetailsService.teamFilter.set(undefined);
	}

	displayFn(val: ScriptTeam) {
		return val?.name?.value || '';
	}
}
