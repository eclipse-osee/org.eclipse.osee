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
import { A11yModule } from '@angular/cdk/a11y';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import {
	BehaviorSubject,
	combineLatest,
	iif,
	Observable,
	of,
	Subject,
} from 'rxjs';
import { map, scan, switchMap, tap } from 'rxjs/operators';
import { applic } from '../../../../../types/applicability/applic';
import { ApplicabilityListUIService } from '../../services/ui/applicability-list-ui.service';
import { EnumerationUIService } from '../../services/ui/enumeration-ui.service';
import { PreferencesUIService } from '../../services/ui/preferences-ui.service';
import { TypesUIService } from '../../services/ui/types-ui.service';
import { enumeration, enumerationSet } from '../../types/enum';
import { PlatformType } from '../../types/platformType';
import { PlatformTypeSentinel } from '../../types/PlatformTypeInstance';
import { EnumFormComponent } from '../enum-form/enum-form.component';

@Component({
	selector: 'osee-edit-enum-set-field',
	templateUrl: './edit-enum-set-field.component.html',
	styleUrls: ['./edit-enum-set-field.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		A11yModule,
		MatSelectModule,
		MatOptionModule,
		NgFor,
		AsyncPipe,
		MatTableModule,
		EnumFormComponent,
	],
})
export class EditEnumSetFieldComponent implements OnInit {
	applic = this.applicabilityService.applic;
	private _updateEnums = new BehaviorSubject<enumeration[]>([]);
	private _enumSetNameUpdate = new BehaviorSubject<string>('');
	private _enumSetDescriptionUpdate = new BehaviorSubject<string>('');
	private _enumSetApplicUpdate = new BehaviorSubject<applic>({
		id: '1',
		name: 'Base',
	});
	inEditMode = this.preferenceService.inEditMode;

	@Input() editable: boolean = false;
	_editable: Observable<boolean> | undefined = undefined;

	enumObs: Observable<enumerationSet> | undefined;
	_type: Observable<PlatformType> | undefined;
	//type enumset loading case 1: by id
	@Input() platformTypeId: string | undefined;

	//type enumset loading case 2: by type
	@Input() platformType: PlatformType | undefined;

	@Output() enumUpdated = new EventEmitter<enumerationSet | undefined>();

	@Output('unique') _unique = new Subject<boolean>();
	constructor(
		private enumSetService: EnumerationUIService,
		private applicabilityService: ApplicabilityListUIService,
		private preferenceService: PreferencesUIService,
		private typeService: TypesUIService
	) {}

	ngOnInit(): void {
		this._editable = of(this.editable);
		this._type = combineLatest([
			of(this.platformType),
			of(this.platformTypeId),
		]).pipe(
			switchMap(([_type, id]) =>
				iif(
					() => _type !== undefined,
					of(_type as PlatformType),
					iif(
						() => id !== undefined,
						this.typeService.getType(id as string),
						of(new PlatformTypeSentinel())
					)
				)
			)
		);
		this.enumObs = combineLatest([
			this._type,
			this._enumSetNameUpdate,
			this._enumSetDescriptionUpdate,
			this._enumSetApplicUpdate,
			this._updateEnums,
		]).pipe(
			switchMap(([type, name, description, applic, updateEnums]) =>
				this.enumSetService.getEnumSet(type?.id || '').pipe(
					switchMap((val) =>
						of(val).pipe(
							map((val) => {
								if (updateEnums.length !== 0) {
									val.enumerations = updateEnums;
								}
								if (name !== '' && val.name !== name) {
									val.name = name;
								}
								if (
									description !== '' &&
									val.description !== description
								) {
									val.description = description;
								}
								if (
									applic.id !== '-1' &&
									val.applicability.id !== applic.id
								) {
									val.applicability = applic;
								}
								return val;
							})
						)
					)
				)
			),
			tap((enumSet) => {
				this.updateEnumSet(enumSet);
			})
		);
	}

	setName(value: string) {
		this._enumSetNameUpdate.next(value);
	}
	setApplicability(value: applic) {
		this._enumSetApplicUpdate.next(value);
	}

	setDescription(value: string) {
		this._enumSetDescriptionUpdate.next(value);
	}

	compareApplics(o1: applic, o2: applic) {
		return o1?.id === o2?.id && o1?.name === o2?.name;
	}

	updateEnums(value: enumeration[]) {
		this._updateEnums.next(value);
	}

	updateEnumSet(value: enumerationSet | undefined) {
		this.enumUpdated.emit(value);
	}

	updateUnique(value: boolean) {
		this._unique.next(value);
	}
}
