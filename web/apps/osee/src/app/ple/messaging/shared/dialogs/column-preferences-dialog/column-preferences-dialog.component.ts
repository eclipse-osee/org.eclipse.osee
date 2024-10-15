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
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import {
	defaultEditElementProfile,
	defaultEditStructureProfile,
	defaultViewElementProfile,
	defaultViewStructureProfile,
} from '@osee/messaging/shared/constants';
import {
	EditAuthService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type {
	PlatformType,
	element,
	settingsDialogData,
	structure,
} from '@osee/messaging/shared/types';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
	selector: 'osee-messaging-column-preferences-dialog',
	templateUrl: './column-preferences-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogTitle,
		MatDialogContent,
		MatCheckbox,
		MatLabel,
		MatTooltip,
		MatButton,
		MatSelectionList,
		MatListOption,
		MatDialogActions,
		MatDialogClose,
	],
})
export class ColumnPreferencesDialogComponent {
	dialogRef =
		inject<MatDialogRef<ColumnPreferencesDialogComponent>>(MatDialogRef);
	data = inject<settingsDialogData>(MAT_DIALOG_DATA);
	private editAuthService = inject(EditAuthService);
	private _headerService = inject(HeaderService);

	editability: Observable<boolean> =
		this.editAuthService.branchEditability.pipe(map((x) => x?.editable));

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		const data = this.data;

		this.editAuthService.BranchIdString = data.branchId;
	}

	onNoClick() {
		this.dialogRef.close();
	}

	getHeaderByName(
		value:
			| (
					| keyof structure
					| (
							| 'txRate'
							| 'publisher'
							| 'subscriber'
							| 'messageNumber'
							| 'messagePeriodicity'
							| ' '
					  )
			  )
			| keyof (element & PlatformType),
		type: 'structure' | 'element'
	) {
		return this._headerService.getHeaderByName(value, type);
	}

	resetToDefaultHeaders(_event: MouseEvent) {
		if (this.data.editable) {
			this.data.allowedHeaders1 = defaultEditStructureProfile;
			this.data.allowedHeaders2 = defaultEditElementProfile;
			return;
		}
		this.data.allowedHeaders1 = defaultViewStructureProfile;
		this.data.allowedHeaders2 = defaultViewElementProfile;
	}

	/**
	 * solely for generating test attributes for integration tests, do not use elsewhere
	 */
	/* istanbul ignore next */
	isChecked<T extends 0 | 1>(
		columnNumber: T,
		preference: T extends 0
			?
					| Exclude<keyof structure, number>
					| (
							| 'txRate'
							| 'publisher'
							| 'subscriber'
							| 'messageNumber'
							| 'messagePeriodicity'
							| ' '
					  )
			: Exclude<keyof (element & PlatformType), number>
	) {
		const headerList = this.getHeaderList(columnNumber);
		//typescript being dumb here
		//@ts-expect-error need to be smarter with type narrowing
		return headerList.includes(preference);
	}
	/**
	 * solely for generating test attributes for integration tests, do not use elsewhere
	 */
	/* istanbul ignore next */
	getHeaderList<T extends 0 | 1>(
		columnNumber: T
	): T extends 0
		? (
				| keyof structure
				| (
						| 'txRate'
						| 'publisher'
						| 'subscriber'
						| 'messageNumber'
						| 'messagePeriodicity'
						| ' '
				  )
			)[]
		: (keyof (element & PlatformType))[] {
		if (columnNumber) {
			//typescript doesn't understand class functions well..
			//@ts-expect-error need to be smarter with type narrowing
			return this.data.allowedHeaders2;
		}
		//typescript doesn't understand class functions well..
		//@ts-expect-error need to be smarter with type narrowing
		return this.data.allowedHeaders1;
	}

	protected isString(value: unknown): value is string {
		return typeof value === 'string';
	}
}
