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
import { Injectable, signal } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { PlatformType } from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class PlMessagingTypesUIService {
	private _filter = signal('');
	private _singleLineAdjustment: BehaviorSubject<number> =
		new BehaviorSubject<number>(0);

	private _columnCount: BehaviorSubject<number> = new BehaviorSubject<number>(
		0
	);

	//TODO : migrate this to array once multi select options are thought out
	private _selected = signal<PlatformType>({
		id: '',
		description: '',
		interfaceLogicalType: '',
		interfacePlatformType2sComplement: false,
		interfacePlatformTypeAnalogAccuracy: '',
		interfacePlatformTypeBitsResolution: '',
		interfacePlatformTypeBitSize: '',
		interfacePlatformTypeCompRate: '',
		interfaceDefaultValue: '',
		enumSet: {
			name: '',
			applicability: {
				id: '1',
				name: 'Base',
			},
			description: '',
		},
		interfacePlatformTypeMaxval: '',
		interfacePlatformTypeMinval: '',
		interfacePlatformTypeMsbValue: '',
		interfacePlatformTypeUnits: '',
		interfacePlatformTypeValidRangeDescription: '',
		name: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	});
	constructor(private ui: UiService) {}

	get filter() {
		return this._filter;
	}

	set filterString(filter: string) {
		this._filter.set(filter);
	}

	get singleLineAdjustment() {
		return this._singleLineAdjustment;
	}

	set singleLineAdjustmentNumber(value: number) {
		if (value !== this._singleLineAdjustment.getValue()) {
			this._singleLineAdjustment.next(value);
		}
	}

	get columnCount() {
		return this._columnCount;
	}

	set columnCountNumber(value: number) {
		if (value !== this._columnCount.getValue()) {
			this._columnCount.next(value);
		}
	}

	get typeUpdateRequired() {
		return this.ui.update;
	}

	set updateTypes(value: boolean) {
		this.ui.updated = value;
	}

	get BranchId() {
		return this.ui.id;
	}

	set BranchIdString(value: string) {
		this.ui.idValue = value;
	}

	set branchType(value: 'working' | 'baseline' | '') {
		this.ui.typeValue = value;
	}

	get selected() {
		return this._selected;
	}
	//TODO : migrate this to array once multi select options are thought out
	select(value: PlatformType) {
		this._selected.update((v) =>
			v.id !== value.id
				? value
				: {
						id: '',
						description: '',
						interfaceLogicalType: '',
						interfacePlatformType2sComplement: false,
						interfacePlatformTypeAnalogAccuracy: '',
						interfacePlatformTypeBitsResolution: '',
						interfacePlatformTypeBitSize: '',
						interfacePlatformTypeCompRate: '',
						interfaceDefaultValue: '',
						enumSet: {
							name: '',
							applicability: {
								id: '1',
								name: 'Base',
							},
							description: '',
						},
						interfacePlatformTypeMaxval: '',
						interfacePlatformTypeMinval: '',
						interfacePlatformTypeMsbValue: '',
						interfacePlatformTypeUnits: '',
						interfacePlatformTypeValidRangeDescription: '',
						name: '',
						applicability: {
							id: '1',
							name: 'Base',
						},
				  }
		);
	}
}
