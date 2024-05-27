/*********************************************************************
 * Copyright (c) 2021 Boeing
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
	EventEmitter,
	Output,
	computed,
	input,
	model,
	inject,
} from '@angular/core';
import { MatSuffix } from '@angular/material/form-field';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { PersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle';
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { attribute } from '@osee/attributes/types';
import { LayoutNotifierService } from '@osee/layout/notification';
import {
	DisplayableElementProps,
	arrayIndexOrder,
	type PlatformType,
	type element,
	type structure,
} from '@osee/messaging/shared/types';
import { PersistedPlatformTypeRelationSelectorComponent } from '@osee/messaging/types/persisted-relation-selector';
import { PersistedUnitDropdownComponent } from '@osee/messaging/units/persisted-unit-dropdown';
import { writableSlice } from '@osee/shared/utils';
import { PersistedNumberAttributeInputComponent } from '@osee/attributes/persisted-number-attribute-input';
import { EnumLiteralsFieldComponent } from '../enum-literal-field/enum-literals-field.component';
import { SubElementTableNoEditFieldComponent } from '../sub-element-table-no-edit-field/sub-element-table-no-edit-field.component';
import { FormsModule } from '@angular/forms';
import { ElementImpactsValidatorDirective } from '../../element-impacts-validator.directive';

@Component({
	selector: 'osee-messaging-sub-element-table-field',
	templateUrl: './sub-element-table-field.component.html',
	styles: [],
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		SubElementTableNoEditFieldComponent,
		AsyncPipe,
		EnumLiteralsFieldComponent,
		AttributeToValuePipe,
		PersistedStringAttributeInputComponent,
		PersistedPlatformTypeRelationSelectorComponent,
		PersistedBooleanAttributeToggleComponent,
		PersistedNumberAttributeInputComponent,
		PersistedApplicabilityDropdownComponent,
		PersistedUnitDropdownComponent,
		MatSuffix,
		FormsModule,
		ElementImpactsValidatorDirective,
	],
})
export class SubElementTableFieldComponent {
	private layoutNotifier = inject(LayoutNotifierService);

	header = input.required<keyof DisplayableElementProps | 'rowControls'>();
	editMode = input.required<boolean>();

	element = model.required<element>();

	platformType = writableSlice(this.element, 'platformType');
	applicability = writableSlice(this.element, 'applicability');
	platformTypeApplicability = writableSlice(
		this.platformType,
		'applicability'
	);
	units = writableSlice(this.platformType, 'interfacePlatformTypeUnits');
	structure = input<structure>({
		id: '-1',
		gammaId: '-1',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: '',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: '',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		interfaceMaxSimultaneity: {
			id: '-1',
			typeId: '2455059983007225756',
			gammaId: '-1',
			value: '',
		},
		interfaceMinSimultaneity: {
			id: '-1',
			typeId: '2455059983007225755',
			gammaId: '-1',
			value: '',
		},
		interfaceTaskFileType: {
			id: '-1',
			typeId: '2455059983007225760',
			gammaId: '-1',
			value: 0,
		},
		interfaceStructureCategory: {
			id: '-1',
			typeId: '2455059983007225764',
			gammaId: '-1',
			value: '',
		},
		applicability: applicabilitySentinel,
		elements: [],
	});
	editableElementHeaders: (keyof DisplayableElementProps | 'rowControls')[] =
		[
			'name',
			'platformType',
			'interfaceElementAlterable',
			'description',
			'notes',
			'applicability',
			'interfacePlatformTypeUnits',
			'interfaceElementIndexStart',
			'interfaceElementIndexEnd',
			'enumLiteral',
			'interfaceDefaultValue',
		];

	protected headerWithoutRowControls = computed(() => {
		const _hdr = this.header();
		return _hdr !== 'rowControls' ? _hdr : 'name';
	});

	protected elementHeader = computed(() => {
		const _hdr = this.headerWithoutRowControls();
		return _hdr !== 'interfacePlatformTypeValidRangeDescription' &&
			_hdr !== 'interfacePlatformTypeUnits' &&
			_hdr !== 'interfacePlatformTypeMsbValue' &&
			_hdr !== 'interfacePlatformTypeMinval' &&
			_hdr !== 'interfacePlatformTypeMaxval' &&
			_hdr !== 'interfacePlatformTypeCompRate' &&
			_hdr !== 'interfacePlatformTypeBitsResolution' &&
			_hdr !== 'interfacePlatformTypeBitSize' &&
			_hdr !== 'interfacePlatformTypeAnalogAccuracy' &&
			_hdr !== 'interfacePlatformType2sComplement' &&
			_hdr !== 'interfaceLogicalType' &&
			_hdr !== 'enumSet'
			? _hdr
			: 'name';
	});

	protected editableElementHeader = computed(() => {
		const _hdr = this.headerWithoutRowControls();
		return _hdr !== 'interfacePlatformTypeValidRangeDescription' &&
			_hdr !== 'interfacePlatformTypeUnits' &&
			_hdr !== 'interfacePlatformTypeMsbValue' &&
			_hdr !== 'interfacePlatformTypeMinval' &&
			_hdr !== 'interfacePlatformTypeMaxval' &&
			_hdr !== 'interfacePlatformTypeCompRate' &&
			_hdr !== 'interfacePlatformTypeBitsResolution' &&
			_hdr !== 'interfacePlatformTypeBitSize' &&
			_hdr !== 'interfacePlatformTypeAnalogAccuracy' &&
			_hdr !== 'interfacePlatformType2sComplement' &&
			_hdr !== 'interfaceLogicalType' &&
			_hdr !== 'enumSet' &&
			_hdr !== 'endWord' &&
			_hdr !== 'endByte' &&
			_hdr !== 'elementSizeInBytes' &&
			_hdr !== 'elementSizeInBits' &&
			_hdr !== 'beginWord' &&
			_hdr !== 'beginByte' &&
			_hdr !== 'autogenerated'
			? _hdr
			: 'name';
	});

	protected editableElementStringHeader = computed(() => {
		const _hdr = this.editableElementHeader();
		return _hdr !== 'applicability' &&
			_hdr !== 'interfaceElementAlterable' &&
			_hdr !== 'interfaceElementArrayHeader' &&
			_hdr !== 'interfaceElementArrayIndexDelimiterOne' &&
			_hdr !== 'interfaceElementArrayIndexDelimiterTwo' &&
			_hdr !== 'interfaceElementBlockData' &&
			_hdr !== 'interfaceElementIndexStart' &&
			_hdr !== 'interfaceElementIndexEnd' &&
			_hdr !== 'interfaceElementWriteArrayHeaderName' &&
			_hdr !== 'platformType'
			? _hdr
			: 'name';
	});

	protected editableElementBooleanHeader = computed(() => {
		const _hdr = this.editableElementHeader();
		return _hdr !== 'applicability' &&
			_hdr !== 'description' &&
			_hdr !== 'enumLiteral' &&
			_hdr !== 'interfaceDefaultValue' &&
			_hdr !== 'interfaceElementIndexEnd' &&
			_hdr !== 'interfaceElementIndexStart' &&
			_hdr !== 'name' &&
			_hdr !== 'notes' &&
			_hdr !== 'platformType' &&
			_hdr !== 'interfaceElementArrayIndexOrder' &&
			_hdr !== 'interfaceElementArrayIndexDelimiterOne' &&
			_hdr !== 'interfaceElementArrayIndexDelimiterTwo'
			? _hdr
			: 'interfaceElementAlterable';
	});

	protected editableElementNumberHeader = computed(() => {
		const _hdr = this.editableElementHeader();
		return _hdr === 'interfaceElementIndexEnd' ||
			_hdr === 'interfaceElementIndexStart'
			? _hdr
			: 'interfaceElementIndexStart';
	});

	protected stringValue = computed(() => {
		const _hdr = this.editableElementStringHeader();
		const element = this.element();
		return element[_hdr];
	});

	protected booleanValue = computed(() => {
		const _hdr = this.editableElementBooleanHeader();
		const element = this.element();
		return element[_hdr];
	});

	protected numberValue = computed(() => {
		const _hdr = this.editableElementNumberHeader();
		const element = this.element();
		return element[_hdr];
	});
	protected editableHeader = computed(() => {
		const _hdr = this.headerWithoutRowControls();
		return _hdr !== 'enumSet' &&
			_hdr !== 'endWord' &&
			_hdr !== 'endByte' &&
			_hdr !== 'elementSizeInBytes' &&
			_hdr !== 'elementSizeInBits' &&
			_hdr !== 'beginWord' &&
			_hdr !== 'beginByte' &&
			_hdr !== 'autogenerated'
			? _hdr
			: 'name';
	});

	protected isDeleted = computed(() => {
		const el = this.element();
		if (this.hasChanges(el)) {
			return el.deleted;
		}
		return false;
	});

	filter = input<string>('');

	layout = this.layoutNotifier.layout;

	@Output() menu = new EventEmitter<{
		event: MouseEvent;
		element: element;
		field?:
			| string
			| number
			| boolean
			| applic
			| PlatformType
			| element[]
			| `${number}`
			| Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>
			| Required<
					attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
			  >
			| Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NOTES>>
			| Required<
					attribute<
						number,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTEND
					>
			  >
			| Required<
					attribute<
						number,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTSTART
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTALTERABLE
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTBLOCKDATA
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMLITERAL
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYHEADER
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTWRITEARRAYHEADERNAME
					>
			  >
			| Required<
					attribute<
						arrayIndexOrder,
						typeof ATTRIBUTETYPEIDENUM.INTERFACELEMENTARRAYINDEXORDER
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYINDEXDELIMITERONE
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYINDEXDELIMITERTWO
					>
			  >;
	}>();

	openGeneralMenu(
		event: MouseEvent,
		element: element,
		field?:
			| string
			| number
			| boolean
			| applic
			| PlatformType
			| element[]
			| `${number}`
			| Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>
			| Required<
					attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
			  >
			| Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NOTES>>
			| Required<
					attribute<
						number,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTEND
					>
			  >
			| Required<
					attribute<
						number,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTSTART
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTALTERABLE
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTBLOCKDATA
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEDEFAULTVAL
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMLITERAL
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYHEADER
					>
			  >
			| Required<
					attribute<
						boolean,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTWRITEARRAYHEADERNAME
					>
			  >
			| Required<
					attribute<
						arrayIndexOrder,
						typeof ATTRIBUTETYPEIDENUM.INTERFACELEMENTARRAYINDEXORDER
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYINDEXDELIMITERONE
					>
			  >
			| Required<
					attribute<
						string,
						typeof ATTRIBUTETYPEIDENUM.INTERFACEELEMENTARRAYINDEXDELIMITERTWO
					>
			  >
	) {
		this.menu.emit({ event, element, field });
	}

	hasChanges(v: element): v is Required<element> {
		return (
			(v as element).changes !== undefined ||
			(v as element).added !== undefined ||
			(v as element).deleted !== undefined
		);
	}
}
