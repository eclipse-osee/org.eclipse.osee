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
import { NgIf, NgFor, AsyncPipe, TitleCasePipe } from '@angular/common';
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
import {
	BehaviorSubject,
	debounceTime,
	distinctUntilChanged,
	filter,
	scan,
	Subject,
	switchMap,
} from 'rxjs';
import { FirstLetterLowerPipe } from '../../../../../osee-utils/osee-string-utils/osee-string-utils-pipes/first-letter-lower.pipe';
import { ParentErrorStateMatcher } from '../../../../../shared-matchers/parent-error-state.matcher';
import { UniquePlatformTypeAttributesDirective } from '../../directives/validators/unique-platform-type-attributes.directive';
import { EnumsService } from '../../services/http/enums.service';
import { TypesService } from '../../services/http/types.service';
import { logicalType, logicalTypeFieldInfo } from '../../types/logicaltype';
import { PlatformType } from '../../types/platformType';
import { NewAttributeFormFieldComponent } from '../new-attribute-form-field/new-attribute-form-field.component';
/**
 * Form that handles the selection of platform type attributes for a new platform type based on it's logical type.
 */
@Component({
	selector: 'osee-new-platform-type-form',
	standalone: true,
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
		NewAttributeFormFieldComponent,
		UniquePlatformTypeAttributesDirective,
		FirstLetterLowerPipe,
	],
	templateUrl: './new-platform-type-form.component.html',
	styleUrls: ['./new-platform-type-form.component.sass'],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class NewPlatformTypeFormComponent implements OnChanges {
	/**
	 * Logical type to load needed attributes from
	 */
	@Input('logicalType') _logicalType: logicalType = {
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

	units = this.constantEnumService.units;
	_formInfo = this.logicalTypeSubject.pipe(
		filter((val) => val.id !== '' && val.id !== '-1'),
		distinctUntilChanged(),
		debounceTime(500),
		switchMap((type) => this.typesService.getLogicalTypeFormDetail(type.id))
	);
	private _latestFormInfo = new Subject<logicalTypeFieldInfo>();

	/**
	 * Partial Platform Type that contains all the values set by the user.
	 */
	@Output() attributes = this._latestFormInfo.pipe(
		scan((acc, curr) => {
			const attributeType: Uncapitalize<typeof curr.attributeType> =
				(curr.attributeType.charAt(0).toLowerCase() +
					curr.attributeType.slice(1)) as Uncapitalize<
					typeof curr.attributeType
				>;
			acc[attributeType] = curr.value;
			return acc;
		}, {} as Partial<PlatformType>)
	);

	parentMatcher = new ParentErrorStateMatcher();

	constructor(
		private typesService: TypesService,
		private constantEnumService: EnumsService
	) {}
	ngOnChanges(changes: SimpleChanges) {
		if (
			changes._logicalType.currentValue !==
			this.logicalTypeSubject.getValue()
		) {
			this.logicalTypeSubject.next(changes._logicalType.currentValue);
		}
	}
	updatedFormValue(event: logicalTypeFieldInfo) {
		this._latestFormInfo.next(event);
	}
}

export default NewPlatformTypeFormComponent;
