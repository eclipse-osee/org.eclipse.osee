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
import {
	Component,
	EventEmitter,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
	ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Router, RouterLink } from '@angular/router';
import { combineLatest, iif, of } from 'rxjs';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';
import { applic } from '../../../../../../types/applicability/applic';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { DeleteSubmessageDialogComponent } from '../../dialogs/delete-submessage-dialog/delete-submessage-dialog.component';
import { RemoveSubmessageDialogComponent } from '../../dialogs/remove-submessage-dialog/remove-submessage-dialog.component';
import { AddSubMessageDialogComponent } from '../../dialogs/add-sub-message-dialog/add-sub-message-dialog.component';
import { difference } from 'src/app/types/change-report/change-report';
import { UiService } from '../../../../../../ple-services/ui/ui.service';
import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { MatTooltipModule } from '@angular/material/tooltip';
import { EditSubMessageFieldComponent } from '../../fields/edit-sub-message-field/edit-sub-message-field.component';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
	subMessage,
	message,
	messageWithChanges,
	subMessageWithChanges,
	EditViewFreeTextDialog,
	EditViewFreeTextFieldDialogComponent,
	CurrentMessagesService,
	HeaderService,
} from '@osee/messaging/shared';
import {
	HighlightFilteredTextDirective,
	DisplayTruncatedStringWithFieldOverflowPipe,
} from '@osee/shared/utils';

@Component({
	selector: 'osee-messaging-sub-message-table',
	templateUrl: './sub-message-table.component.html',
	styleUrls: ['./sub-message-table.component.sass'],
	standalone: true,
	imports: [
		MatTableModule,
		MatButtonModule,
		MatTooltipModule,
		NgFor,
		AsyncPipe,
		NgClass,
		NgIf,
		RouterLink,
		DeleteSubmessageDialogComponent,
		RemoveSubmessageDialogComponent,
		AddSubMessageDialogComponent,
		HighlightFilteredTextDirective,
		DisplayTruncatedStringWithFieldOverflowPipe,
		EditSubMessageFieldComponent,
		MatMenuModule,
		MatIconModule,
		FormsModule,
		MatFormFieldModule,
	],
})
export class SubMessageTableComponent implements OnChanges {
	@Input() data: subMessage[] = [];
	@Input() dataSource: MatTableDataSource<subMessage> =
		new MatTableDataSource<subMessage>();
	@Input() filter: string = '';

	@Input() element!: message;
	@Input() editMode: boolean = false;
	@Output() expandRow = new EventEmitter();
	headers = this.headerService.AllSubMessageHeaders.pipe(
		switchMap(([name, description, number, applicability]) =>
			of<(keyof subMessage | ' ')[]>([
				name,
				description,
				number,
				' ',
				applicability,
			])
		)
	);
	_messageRoute = combineLatest([
		this.messageService.initialRoute,
		this.messageService.endOfRoute,
	]).pipe(
		switchMap(([initial, end]) => of({ beginning: initial, end: end }))
	);
	menuPosition = {
		x: '0',
		y: '0',
	};
	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;
	constructor(
		public dialog: MatDialog,
		private router: Router,
		private messageService: CurrentMessagesService,
		private headerService: HeaderService,
		private _ui: UiService
	) {
		this.dataSource.data = this.data;
	}

	ngOnChanges(changes: SimpleChanges): void {
		this.dataSource.data = this.data;
		if (this.filter !== '') {
			if (this.dataSource.filteredData.length > 0) {
				this.expandRow.emit(this.element);
			}
		}
	}

	valueTracker(index: any, item: any) {
		return index;
	}

	openMenu(
		event: MouseEvent,
		message: message | messageWithChanges,
		submessage: subMessage | subMessageWithChanges,
		location: string,
		field: string | applic,
		header: keyof subMessage | ' '
	) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			message: message,
			submessage: submessage,
			location: location,
			field: field,
			header: header,
			change:
				(submessage as subMessageWithChanges).changes !== undefined &&
				header !== ' ' &&
				(submessage as subMessageWithChanges).changes[header] !==
					undefined
					? (submessage as subMessageWithChanges).changes[header]
					: undefined,
		};
		this.matMenuTrigger.menuData = this.matMenuTrigger.menuData as {
			message: message | messageWithChanges;
			submessage: subMessage | subMessageWithChanges;
			location: string;
			field: string | applic;
			header:
				| 'name'
				| 'description'
				| 'interfaceSubMessageNumber'
				| 'applicability'
				| ' ';
			change: difference | undefined;
		};
		this.matMenuTrigger.openMenu();
	}

	removeSubMessage(submessage: subMessage, message: message) {
		this.dialog
			.open(RemoveSubmessageDialogComponent, {
				data: { submessage: submessage, message: message },
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.messageService.removeSubMessage(
							submessage?.id || '',
							message.id
						),
						of()
					)
				)
			)
			.subscribe();
	}
	deleteSubMessage(submessage: subMessage) {
		this.dialog
			.open(DeleteSubmessageDialogComponent, {
				data: { submessage: submessage },
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.messageService.deleteSubMessage(
							submessage.id || ''
						),
						of()
					)
				)
			)
			.subscribe();
	}

	insertSubMessage(message: message, afterSubMessage?: string) {
		this.dialog
			.open(AddSubMessageDialogComponent, {
				minWidth: '80%',
				data: {
					name: message.name,
					id: message.id,
					subMessage: {
						name: '',
						description: '',
						interfaceSubMessageNumber: '',
					},
				},
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				switchMap((z: AddSubMessageDialog) =>
					iif(
						() =>
							z != undefined &&
							z.subMessage != undefined &&
							z.subMessage.id != undefined &&
							z?.subMessage?.id.length > 0 &&
							z.subMessage.id !== '-1',
						this.messageService
							.relateSubMessage(
								z.id,
								z?.subMessage?.id || '-1',
								afterSubMessage || 'end'
							)
							.pipe(
								switchMap((transaction) =>
									combineLatest([
										this._ui.isLoading,
										of(transaction),
									]).pipe(
										filter(
											([loading, transaction]) =>
												loading !== 'false'
										),
										take(1),
										map(([loading, transaction]) => {
											this.router.navigate([], {
												fragment: 'a' + z.subMessage.id,
											});
										})
									)
								)
							),
						this.messageService
							.createSubMessage(
								z.subMessage,
								z.id,
								afterSubMessage
							)
							.pipe(
								switchMap((transaction) =>
									combineLatest([
										this._ui.isLoading,
										of(transaction),
									]).pipe(
										filter(
											([loading, transaction]) =>
												loading !== 'false'
										),
										take(1),
										map(([loading, transaction]) => {
											this.router.navigate([], {
												fragment:
													'a' +
													(transaction.results
														.ids[0] ||
														afterSubMessage ||
														''),
											});
										})
									)
								)
							)
					)
				)
			)
			.subscribe();
	}

	openDescriptionDialog(
		description: string,
		submessageId: string,
		messageId: string
	) {
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: JSON.parse(JSON.stringify(description)) as string,
					type: 'Description',
					return: description,
				},
				minHeight: '60%',
				minWidth: '60%',
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((response: EditViewFreeTextDialog | string) =>
					iif(
						() =>
							response === 'ok' ||
							response === 'cancel' ||
							response === undefined,
						//do nothing
						of(),
						//change description
						this.messageService.partialUpdateSubMessage(
							{
								id: submessageId,
								description: (
									response as EditViewFreeTextDialog
								).return,
							},
							messageId
						)
					)
				)
			)
			.subscribe();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'submessage');
	}

	viewDiff(open: boolean, value: difference, header: string) {
		this.messageService.sideNav = {
			opened: open,
			field: header,
			currentValue: value?.currentValue || '',
			previousValue: value?.previousValue || '',
			transaction: value.transactionToken,
		};
	}
	hasChanges(
		value: subMessage | subMessageWithChanges,
		header:
			| 'name'
			| 'description'
			| 'interfaceSubMessageNumber'
			| 'applicability'
			| ' '
	): value is subMessageWithChanges {
		return (
			(value as subMessageWithChanges).changes !== undefined &&
			header !== ' ' &&
			(value as subMessageWithChanges).changes[header] !== undefined
		);
	}
	isDifference(value: difference | undefined): value is difference {
		return (value as difference) !== undefined;
	}
}
