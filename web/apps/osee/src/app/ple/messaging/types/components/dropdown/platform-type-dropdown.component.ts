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
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	inject,
	input,
	model,
	output,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import { MatIconAnchor } from '@angular/material/button';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { RouterLink } from '@angular/router';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { TypesUIService } from '@osee/messaging/shared/services';
import { PlatformType } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	provideOptionalControlContainerNgForm,
	provideOptionalControlContainerNgModelGroup,
	writableSlice,
} from '@osee/shared/utils';
import {
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	map,
	switchMap,
} from 'rxjs';
let nextUniqueId = 0;
@Component({
	selector: 'osee-platform-type-dropdown',
	standalone: true,
	imports: [
		MatAutocomplete,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatIconAnchor,
		MatSuffix,
		MatAutocompleteTrigger,
		MatOption,
		MatTooltip,
		MatOptionLoadingComponent,
		FormsModule,
		AsyncPipe,
		RouterLink,
	],
	template: `<mat-form-field
		subscriptSizing="dynamic"
		class="tw-w-full [&>.mdc-text-field--filled]:tw-bg-inherit">
		<mat-label>Platform Type</mat-label>
		<input
			matInput
			type="text"
			[name]="'platform_type_dropdown_' + _componentId()"
			[(ngModel)]="filter"
			(focusin)="autoCompleteOpened()"
			[disabled]="disabled()"
			[matAutocomplete]="auto"
			[required]="required()" />
		<div matSuffix>
			<ng-content select="[preLocation]"></ng-content>
			@if (!hideSearchButton()) {
				<a
					mat-icon-button
					[routerLink]="typePageLocation()"
					queryParamsHandling="merge"
					[target]="target()"
					matTooltip="Open Platform Type Search Page">
					<mat-icon>search</mat-icon>
				</a>
			}
			@if (id() !== '-1' && id() !== '0') {
				<a
					mat-icon-button
					[routerLink]="location()"
					queryParamsHandling="merge"
					(contextmenu)="contextmenu.emit($event)"
					[target]="target()"
					[matTooltip]="'Open Details for Platform Type:' + name()"
					><mat-icon>arrow_forward</mat-icon></a
				>
			}
			<ng-content select="[postLocation]"></ng-content>
		</div>
		<ng-content select="[selectHint]"></ng-content>
		<ng-content select="[hint]"></ng-content>
		<mat-autocomplete
			autoActiveFirstOption
			autoSelectActiveOption
			#auto="matAutocomplete"
			(opened)="autoCompleteOpened()"
			(optionSelected)="this.platformType.set($event.option.value)">
			@for (extraType of extraPlatformTypes(); track extraType) {
				<mat-option
					[value]="extraType"
					[id]="extraType.id"
					>{{ extraType.name.value }}</mat-option
				>
			}
			@if (filteredTypes | async; as func) {
				<osee-mat-option-loading
					[data]="func"
					[count]="(filteredTypesCount | async) || -1"
					objectName="Existing  Platform Types"
					[paginationSize]="_paginationSize"
					paginationMode="AUTO">
					<ng-template let-option>
						<mat-option
							[value]="option"
							[id]="option.id">
							{{ option.name.value }}
						</mat-option>
					</ng-template>
				</osee-mat-option-loading>
			}
		</mat-autocomplete>
	</mat-form-field>`,
	viewProviders: [
		provideOptionalControlContainerNgForm(),
		provideOptionalControlContainerNgModelGroup(),
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PlatformTypeDropdownComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	allowOpenInSameTab = input(false);
	protected target = computed(() =>
		this.allowOpenInSameTab() ? '' : '_blank'
	);

	extraPlatformTypes = input<PlatformType[]>([]);
	disabled = input(false);
	required = input(false);
	hideSearchButton = input(false);
	contextmenu = output<MouseEvent>();
	platformType = model<PlatformType>(new PlatformTypeSentinel('None'));
	protected id = computed(() => this.platformType().id);
	private nameAttr = writableSlice(this.platformType, 'name');

	protected name = writableSlice(this.nameAttr, 'value');

	protected filter = signal(this.name());
	private _filter = toObservable(this.filter);
	private _updateFilter = effect(
		() => {
			this.filter.set(this.name());
		},
		{ allowSignalWrites: true }
	);
	private _autoCompleteOpened = new ReplaySubject<void>();
	protected _paginationSize = 10;
	private _typesUiService = inject(TypesUIService);
	private _typeDetailLocation = this._typesUiService.detailLocation;

	protected typePageLocation = toSignal(this._typesUiService.searchLocation, {
		initialValue: '',
	});
	private _location = toSignal(this._typeDetailLocation, {
		initialValue: '',
	});
	protected location = computed(() => {
		return this._location() + this.platformType().id;
	});
	autoCompleteOpened() {
		this._autoCompleteOpened.next();
	}
	filteredTypes = this._autoCompleteOpened.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._filter.pipe(
				debounceTime(500),
				map(
					(typeAhead) => (pageNum: string | number) =>
						this._typesUiService.getPaginatedFilteredTypes(
							typeAhead.toLowerCase(),
							this._paginationSize,
							pageNum.toString()
						)
				)
			)
		)
	);

	filteredTypesCount = this._autoCompleteOpened.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._filter.pipe(
				debounceTime(500),
				switchMap((search) =>
					this._typesUiService.getFilteredTypesCount(search)
				)
			)
		)
	);
}
