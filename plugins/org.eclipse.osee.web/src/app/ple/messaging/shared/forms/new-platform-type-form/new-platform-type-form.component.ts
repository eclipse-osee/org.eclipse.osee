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
import {
	NgIf,
	NgFor,
	AsyncPipe,
	TitleCasePipe,
	NgTemplateOutlet,
} from '@angular/common';
import {
	Component,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { UniquePlatformTypeAttributesDirective } from '@osee/messaging/shared/directives';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { TypesService } from '@osee/messaging/shared/services';
import type {
	enumerationSet,
	logicalType,
	logicalTypeFieldInfo,
	PlatformType,
} from '@osee/messaging/shared/types';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { applic } from '@osee/shared/types/applicability';
import { FirstLetterLowerPipe } from '@osee/shared/utils';
import {
	BehaviorSubject,
	debounceTime,
	distinctUntilChanged,
	filter,
	Subject,
	switchMap,
	tap,
} from 'rxjs';
import { UnitDropdownComponent } from '../../dropdowns/unit-dropdown/unit-dropdown.component';
/**
 * Form that handles the selection of platform type attributes for a new platform type based on it's logical type.
 */
@Component({
	selector: 'osee-new-platform-type-form',
	standalone: true,
	templateUrl: './new-platform-type-form.component.html',
	styles: [],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		FormsModule,
		MatFormFieldModule,
		MatOptionModule,
		MatInputModule,
		MatSelectModule,
		TitleCasePipe,
		UniquePlatformTypeAttributesDirective,
		FirstLetterLowerPipe,
		NgTemplateOutlet,
		UnitDropdownComponent,
	],
})
export class NewPlatformTypeFormComponent implements OnChanges {
	/**
	 * Logical type to load needed attributes from
	 */
	@Input() logicalType: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};

	logicalTypeSubject = new BehaviorSubject<logicalType>({
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	});

	_formInfo = this.logicalTypeSubject.pipe(
		filter((val) => val.id !== '' && val.id !== '-1'),
		distinctUntilChanged(),
		debounceTime(500),
		switchMap((type) =>
			this.typesService.getLogicalTypeFormDetail(type.id)
		),
		tap((form) => {
			this._platformType.interfaceLogicalType = form.name;
			form.fields
				.filter((f) => !f.editable)
				.forEach((f) => {
					this._platformType[f.jsonPropertyName] = f.defaultValue;
				});
			this.updateField();
		})
	);

	protected _platformType: PlatformType = new PlatformTypeSentinel();
	@Output() protected platformType = new Subject<PlatformType>();

	parentMatcher = new ParentErrorStateMatcher();

	constructor(private typesService: TypesService) {
		//@ts-ignore
		delete this._platformType.id;
		//@ts-ignore
		delete this._platformType.enumSet;
	}
	ngOnChanges(changes: SimpleChanges) {
		if (
			changes.logicalType !== undefined &&
			changes.logicalType.currentValue !==
				this.logicalTypeSubject.getValue() &&
			changes.logicalType.currentValue !== undefined
		) {
			this.logicalTypeSubject.next(changes.logicalType.currentValue);
		}
	}
	protected isLogicalTypeFieldInfo(
		value: unknown
	): value is logicalTypeFieldInfo {
		return (value as any).jsonPropertyName !== undefined;
	}
	protected isString(
		value: string | boolean | enumerationSet | applic | undefined
	): value is string {
		return typeof value === 'string';
	}
	updateField() {
		this.platformType.next(this._platformType);
	}
}

export default NewPlatformTypeFormComponent;
