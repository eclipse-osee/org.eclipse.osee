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
import { TitleCasePipe } from '@angular/common';
import {
	Component,
	EventEmitter,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { TypesService } from '@osee/messaging/shared/services';
import type { logicalType } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { HasValidIdDirective } from '@osee/shared/validators';

@Component({
	selector: 'osee-logical-type-selector',
	standalone: true,
	imports: [
		TitleCasePipe,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MatOptionLoadingComponent,
		HasValidIdDirective,
	],
	templateUrl: './logical-type-selector.component.html',
	styles: [],
})
export class LogicalTypeSelectorComponent {
	@ViewChild('logicalTypeSelector')
	form!: NgForm;
	@Input() type: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};

	parentMatcher = new ParentErrorStateMatcher();
	@Output() typeChanged = new EventEmitter<logicalType>();
	logicalTypes = this.typesService.logicalTypes;

	constructor(private typesService: TypesService) {}
	setType(value: logicalType) {
		this.typeChanged.next(value);
	}

	compareIds<T extends { id: string }>(a: T, b: T) {
		if (a == null || b == null) {
			return false;
		}
		return a.id === b.id;
	}
}
