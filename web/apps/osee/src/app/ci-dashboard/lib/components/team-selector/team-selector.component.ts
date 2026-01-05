/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	effect,
	inject,
	input,
	linkedSignal,
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
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { DashboardService } from '../../services/dashboard.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	of,
	switchMap,
} from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { DefReference, ScriptTeam } from '../../types/tmo';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { CurrentTransactionService } from '@osee/transactions/services';
import { RELATIONTYPEIDENUM } from '@osee/shared/types/constants';
import { addRelation } from '@osee/transactions/operators';

@Component({
	selector: 'osee-team-selector',
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
			class="tw-w-full">
			<input
				type="text"
				matInput
				[value]="filter()"
				(input)="updateFilter($event)"
				placeholder="Select a Team"
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
						[noneOption]="undefined">
						<ng-template let-option>
							@if (option.name !== '') {
								<mat-option
									[attr.data-cy]="
										'option-' + option.name.value
									"
									[value]="option"
									[id]="option.id">
									@if (option.name === '') {
										None
									} @else {
										{{ option.name.value }}
									}
								</mat-option>
							}
						</ng-template>
					</osee-mat-option-loading>
				}
			</mat-autocomplete>
		</mat-form-field>
	`,
})
export class TeamSelectorComponent {
	script = input.required<DefReference>();

	private dashboardService = inject(DashboardService);
	private txService = inject(CurrentTransactionService);

	pageSize = 100;
	noneOption: ScriptTeam = {
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			gammaId: '-1',
			typeId: '1152921504606847088',
			value: '',
		},
	};

	filter = linkedSignal(() => this.script().team.name.value);
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

	private _scriptEffect = effect(() => {
		this.filter.set(this.script().team.name.value);
	});

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
		this.filter.set(team.name.value);

		if (team.id === this.script().team.id) {
			return;
		}

		if (this.script().team.id !== '-1') {
			this.txService
				.deleteRelation(
					`Changing team relation for script ${this.script().id}`,
					{
						typeId: RELATIONTYPEIDENUM.SCRIPTDEFTOTEAM,
						aArtId: this.script().id,
						bArtId: this.script().team.id,
					}
				)
				.pipe(
					addRelation({
						typeId: RELATIONTYPEIDENUM.SCRIPTDEFTOTEAM,
						aArtId: this.script().id,
						bArtId: team.id,
					}),
					this.txService.performMutation()
				)
				.subscribe();
		} else {
			this.txService
				.addRelation(
					`Changing team relation for script ${this.script().id}`,
					{
						typeId: RELATIONTYPEIDENUM.SCRIPTDEFTOTEAM,
						aArtId: this.script().id,
						bArtId: team.id,
					}
				)
				.pipe(this.txService.performMutation())
				.subscribe();
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
	}

	displayFn(val: ScriptTeam) {
		return val?.name?.value || '';
	}
}
