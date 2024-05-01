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
import { Component, Input, Output, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import {
	MatActionList,
	MatListItem,
	MatListItemIcon,
} from '@angular/material/list';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { EditViewFreeTextFieldDialogComponent } from '@osee/messaging/shared/dialogs/free-text';
import { NamedId } from '@osee/shared/types';
import { Subject, filter, take, tap } from 'rxjs';

@Component({
	selector: 'osee-named-id-list-editor',
	standalone: true,
	imports: [
		FormsModule,
		MatFormField,
		MatInput,
		MatActionList,
		MatListItem,
		MatDivider,
		MatIcon,
		MatListItemIcon,
		MatPaginator,
	],
	templateUrl: './named-id-list-editor.component.html',
	styles: [],
})
export class NamedIdListEditorComponent {
	@Input() name: string = '';
	@Input() dataToDisplay: NamedId[] = [];
	@Input() count: number = 0;

	@Input() pageSize: number = 10;

	@Input() pageIndex: number = 1;
	@Input() allowedToEdit: boolean = false;

	@Output() pageEvent = new Subject<PageEvent>();

	filter: string = '';
	_filterChange = new Subject<string>();
	@Output() filterChange = this._filterChange;

	@Output() namedIdEdit = new Subject<NamedId>();

	@Output() createNew = new Subject<string>();
	_dialog = inject(MatDialog);

	openNameDialog(namedIdToModify: NamedId) {
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: namedIdToModify.name,
					type: 'Name',
					return: namedIdToModify.name,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.namedIdEdit.next({
						id: namedIdToModify.id,
						name: v.return,
					});
				})
			)
			.subscribe();
	}

	updateFilter(f: string) {
		this._filterChange.next(f);
	}

	addNamedId() {
		const returnVal = '';
		this._dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: '',
					type: 'Name',
					return: returnVal,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((v) => v !== 'ok' && v !== 'cancel' && v !== undefined),
				tap((v) => {
					this.createNew.next(v.return);
				})
			)
			.subscribe();
	}
}
