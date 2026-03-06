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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import {
	Component,
	WritableSignal,
	computed,
	input,
	output,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatSelect } from '@angular/material/select';
import { MatSlider, MatSliderThumb } from '@angular/material/slider';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import {
	PlatformTypeQuery,
	andBitSizeQuery,
	andDefaultValQuery,
	andDescriptionQuery,
	andLogicalTypeQuery,
	andMaxValQuery,
	andMinValQuery,
	andMsbValQuery,
	andNameQuery,
	andQuery,
	andUnitQuery,
} from '@osee/messaging/shared/query';
import type { PlatformType } from '@osee/messaging/shared/types';
import { UnitDropdownComponent } from '@osee/messaging/units/dropdown';

@Component({
	selector: 'osee-messaging-platform-type-query',
	templateUrl: './platform-type-query.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatLabel,
		MatFormField,
		MatInput,
		MatSlider,
		MatSuffix,
		MatSliderThumb,
		MatIconButton,
		MatIcon,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		CdkTextareaAutosize,
		MatSelect,
		UnitDropdownComponent,
	],
})
export class PlatformTypeQueryComponent {
	platformTypes = input.required<PlatformType[]>();
	bitSizeSliderStepSize = input(0.05);
	unit: WritableSignal<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS>
	> = signal({
		id: '-1',
		typeId: '4026643196432874344',
		gammaId: '-1',
		value: '',
	});
	logicalType = signal('');
	defaultVal = signal('');
	maximumValue = signal('');
	minimumValue = signal('');
	msbValue = signal('');
	bitSize = signal(0);
	name = signal('');
	description = signal('');
	returnQuery = output<PlatformTypeQuery>({ alias: 'query' });
	logicalTypes = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.interfaceLogicalType.value)
			.filter((v, i, a) => a.indexOf(v) === i);
	});
	bitSizes = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => Number(type.interfacePlatformTypeBitSize.value))
			.filter((v, i, a) => a.indexOf(v) === i);
	});
	maxBitSize = computed(() => Math.max(...this.bitSizes()));
	minBitSize = computed(() => Math.min(...this.bitSizes()));
	defaultValues = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase())
			)
			.map((type) => type.interfaceDefaultValue.value)
			.filter((v, i, a) => a.indexOf(v) === i);
	});

	maxValues = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.interfacePlatformTypeMaxval.value)
			.filter((v, i, a) => a.indexOf(v) === i);
	});
	minValues = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.interfacePlatformTypeMinval.value)
			.filter((v, i, a) => a.indexOf(v) === i);
	});
	msbValues = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.interfacePlatformTypeMsbValue.value)
			.filter((v, i, a) => a.indexOf(v) === i);
	});
	types = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.name.value)
			.filter((v, i, a) => a.indexOf(v) === i)
			.filter((value) =>
				value.toLowerCase().includes(this.name().toLowerCase())
			);
	});
	descriptions = computed(() => {
		return this.platformTypes()
			.filter(
				(value) =>
					value.interfaceLogicalType.value
						.toLowerCase()
						.includes(this.logicalType().toLowerCase()) &&
					value.interfacePlatformTypeMsbValue.value
						.toLowerCase()
						.includes(this.msbValue().toLowerCase()) &&
					value.interfaceDefaultValue.value
						.toLowerCase()
						.includes(this.defaultVal().toLowerCase()) &&
					value.interfacePlatformTypeMaxval.value
						.toLowerCase()
						.includes(this.maximumValue().toLowerCase()) &&
					value.interfacePlatformTypeMinval.value
						.toLowerCase()
						.includes(this.minimumValue().toLowerCase()) &&
					value.description.value
						.toLowerCase()
						.includes(this.description().toLowerCase())
			)
			.map((type) => type.description.value)
			.filter((v, i, a) => a.indexOf(v) === i)
			.filter((value) =>
				value.toLowerCase().includes(this.name().toLowerCase())
			);
	});

	bitSizeDisplay(value: number) {
		return value.toPrecision(4);
	}
	resultingQuery = computed(() => {
		const queries: andQuery[] = [];
		if (this.unit().id !== '-1')
			queries.push(new andUnitQuery(this.unit().value));
		if (this.bitSize() !== 0)
			queries.push(new andBitSizeQuery(this.bitSize().toString()));
		if (this.logicalType() !== '')
			queries.push(new andLogicalTypeQuery(this.logicalType()));
		if (this.minimumValue() !== '')
			queries.push(new andMinValQuery(this.minimumValue()));
		if (this.maximumValue() !== '')
			queries.push(new andMaxValQuery(this.maximumValue()));
		if (this.defaultVal() !== '')
			queries.push(new andDefaultValQuery(this.defaultVal()));
		if (this.msbValue() !== '')
			queries.push(new andMsbValQuery(this.msbValue()));
		if (this.name() !== '') queries.push(new andNameQuery(this.name()));
		if (this.description() !== '')
			queries.push(new andDescriptionQuery(this.description()));
		return new PlatformTypeQuery(undefined, queries);
	});

	queryTypes() {
		this.returnQuery.emit(this.resultingQuery());
	}
}
