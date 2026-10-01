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
import { AsyncPipe, NgTemplateOutlet, TitleCasePipe } from '@angular/common';
import { Component, input, model, inject } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatLabel } from '@angular/material/form-field';
import { applic } from '@osee/applicability/types';
import { attribute } from '@osee/attributes/types';
import { UniquePlatformTypeAttributesDirective } from '@osee/messaging/shared/directives';
import { TypesService } from '@osee/messaging/shared/services';
import type {
	DisplayablePlatformTypeProps,
	PlatformType,
	PlatformTypeAttr,
	enumerationSet,
	logicalType,
	logicalTypeFieldInfo,
} from '@osee/messaging/shared/types';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import {
	provideOptionalControlContainerNgForm,
	writableSlice,
} from '@osee/shared/utils';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	shareReplay,
	switchMap,
	tap,
} from 'rxjs';
import { NewPlatformTypeFieldComponent } from '../new-platform-type-field/new-platform-type-field.component';
import { UnitDropdownComponent } from '@osee/messaging/units/dropdown';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
/**
 * Form that handles the selection of platform type attributes for a new platform type based on it's logical type.
 */
@Component({
	selector: 'osee-new-platform-type-form',
	templateUrl: './new-platform-type-form.component.html',
	styles: [],
	viewProviders: [provideOptionalControlContainerNgForm()],
	imports: [
		AsyncPipe,
		FormsModule,
		MatLabel,
		TitleCasePipe,
		UniquePlatformTypeAttributesDirective,
		NgTemplateOutlet,
		UnitDropdownComponent,
		NewPlatformTypeFieldComponent,
	],
})
export class NewPlatformTypeFormComponent {
	private typesService = inject(TypesService);

	/**
	 * Logical type to load needed attributes from
	 */
	logicalType = input<logicalType>({
		id: '-1',
		name: '',
		idString: '',
		idIntValue: 0,
	});
	private _logicalType = toObservable(this.logicalType);
	// Single source for the logical-type form detail. Keyed on the type id (not object identity, which
	// changes as platformType rebuilds) so re-emissions of the same type do not refetch, and shared so
	// the template's async pipe drives exactly one HTTP call. Previously two separate pipelines
	// (_formInfo + __formInfo) each fetched this, and object-identity re-emissions fetched again --
	// every extra response re-rendered/repopulated the form and could wipe an in-progress edit.
	__formInfo = this._logicalType.pipe(
		filter((val) => val.id !== '-1'),
		distinctUntilChanged((a, b) => a.id === b.id),
		debounceTime(500),
		switchMap((type) =>
			this.typesService.getLogicalTypeFormDetail(type.id)
		),
		tap((form) => {
			this.interfaceLogicalTypeValue.set(form.name);
			form.fields
				.filter((f) => !f.editable)
				.forEach((f) => {
					this.updateInnerPlatformTypeWithRaw(
						f.jsonPropertyName,
						f.defaultValue
					);
				});
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	platformType = model.required<PlatformType>();
	private interfaceLogicalType = writableSlice(
		this.platformType,
		'interfaceLogicalType'
	);
	private interfaceLogicalTypeValue = writableSlice(
		this.interfaceLogicalType,
		'value'
	);

	parentMatcher = new ParentErrorStateMatcher();

	protected isLogicalTypeFieldInfo<U extends keyof PlatformTypeAttr>(
		value: unknown
	): value is logicalTypeFieldInfo<U> {
		return (
			(value as logicalTypeFieldInfo<'name'>).jsonPropertyName !==
			undefined
		);
	}

	protected isGeneralEditable<
		U extends keyof Omit<
			DisplayablePlatformTypeProps,
			'id' | 'gammaId' | 'applicability' | 'enumSet'
		>,
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	>(value: logicalTypeFieldInfo<any>): value is logicalTypeFieldInfo<U> {
		return (
			value.jsonPropertyName !== 'id' &&
			value.jsonPropertyName !== 'gammaId' &&
			value.jsonPropertyName !== 'applicability' &&
			value.jsonPropertyName !== 'enumSet'
		);
	}
	protected isString(
		value:
			| string
			| boolean
			| enumerationSet
			| applic
			| undefined
			| attribute<unknown, ATTRIBUTETYPEID>
	): value is string {
		return typeof value === 'string';
	}
	updateInnerPlatformTypeWithRaw<
		T extends keyof PlatformTypeAttr = keyof PlatformTypeAttr,
		//I give up on this type for right now, it's complicated
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	>(key: T, value: any) {
		this.platformType.update((v) => {
			if (value && value.value) {
				v[key].value = value.value;
			} else {
				v[key].value = value;
			}
			return v;
		});
	}
	updateInnerPlatformType<
		T extends keyof PlatformTypeAttr = keyof PlatformTypeAttr,
		//I give up on this type for right now, it's complicated
		// eslint-disable-next-line @typescript-eslint/no-explicit-any
	>(key: T, value: any) {
		this.platformType.update((v) => {
			if (value && value.value) {
				v[key].value = value.value;
			} else {
				v[key].value = value;
			}
			return v;
		});
	}
}
export default NewPlatformTypeFormComponent;
