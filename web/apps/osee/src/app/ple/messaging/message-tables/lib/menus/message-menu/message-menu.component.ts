/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	ChangeDetectionStrategy,
	Component,
	computed,
	effect,
	inject,
	model,
	viewChild,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { applic } from '@osee/applicability/types';
import {
	CurrentMessagesService,
	HeaderService,
} from '@osee/messaging/shared/services';
import {
	EditViewFreeTextDialog,
	_messageChanges,
	message,
} from '@osee/messaging/shared/types';
import { difference } from '@osee/shared/types/change-report';
import { writableSlice } from '@osee/shared/utils';
import { filter, first, iif, map, of, switchMap, take } from 'rxjs';
import { AddMessageDialogComponent } from '../../dialogs/add-message-dialog/add-message-dialog.component';
import { DeleteMessageDialogComponent } from '../../dialogs/delete-message-dialog/delete-message-dialog.component';
import { RemoveMessageDialogComponent } from '../../dialogs/remove-message-dialog/remove-message-dialog.component';
import { MatTooltip } from '@angular/material/tooltip';
import { MatDivider } from '@angular/material/divider';
import { EditViewFreeTextFieldDialogComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-message-menu',
	standalone: true,
	imports: [
		MatMenu,
		MatIcon,
		MatMenuItem,
		MatMenuTrigger,
		MatTooltip,
		MatDivider,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<mat-menu
			#contextMenu="matMenu"
			(closed)="open.set(false)">
			<button
				mat-menu-item
				matTooltip="View/Edit description in an a larger view."
				(click)="openDescriptionDialog()"
				data-cy="message-description-btn">
				<mat-icon class="tw-text-osee-blue-9">description</mat-icon>Open
				Description
			</button>
			<div [matTooltip]="copyTooltip()">
				<button
					mat-menu-item
					(click)="copyMessageDialog()"
					[disabled]="inEditMode() === false || message().deleted"
					data-cy="message-copy-btn">
					<mat-icon class="tw-text-osee-green-9"> add </mat-icon>Copy
					message
				</button>
			</div>
			<div [matTooltip]="removeTooltip()">
				<button
					mat-menu-item
					(click)="removeMessage()"
					[disabled]="inEditMode() === false || message().deleted"
					data-cy="message-remove-btn">
					<mat-icon class="tw-text-osee-red-9"
						>remove_circle_outline</mat-icon
					>Remove message from connection
				</button>
			</div>
			<div [matTooltip]="deleteTooltip()">
				<button
					mat-menu-item
					(click)="deleteMessage()"
					[disabled]="
						inEditMode() === false ||
						message().deleted ||
						message().subMessages.length !== 0
					"
					data-cy="message-delete-btn">
					<mat-icon class="tw-text-osee-red-9"
						>delete_forever</mat-icon
					>Delete message globally
				</button>
			</div>
			<mat-divider />
			<div [matTooltip]="diffTooltip()">
				<button
					mat-menu-item
					[disabled]="
						(hasChanges(message()) &&
							!changeExists(message(), header())) ||
						!hasChanges(message())
					"
					(click)="
						viewDiff(
							true,
							$any(message()).changes[header()] || {
								previousValue: '',
								currentValue: '',
								transactionToken: {
									id: '-1',
									branchId: '-1',
								},
							},
							headerName()
						)
					"
					data-cy="message-diff-btn">
					<mat-icon
						class="tw-text-osee-yellow-10 dark:tw-text-osee-amber-9"
						>pageview</mat-icon
					>View Diff
				</button>
			</div>
		</mat-menu>

		<div
			style="visibility: hidden; position: fixed"
			[style.left]="menuPositionX()"
			[style.top]="menuPositionY()"
			[matMenuTriggerFor]="contextMenu"></div>`,
})
export class MessageMenuComponent {
	menuData = model<{
		x: string;
		y: string;
		open: boolean;
		message: message;
		header: keyof _messageChanges;
		editMode: boolean;
	}>({
		x: '0',
		y: '0',
		open: false,
		editMode: false,
		header: 'name',
		message: {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageNumber: {
				id: '-1',
				typeId: '2455059983007225768',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePeriodicity: {
				id: '-1',
				typeId: '3899709087455064789',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRate: {
				id: '-1',
				typeId: '2455059983007225763',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageType: {
				id: '-1',
				typeId: '2455059983007225770',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageWriteAccess: {
				id: '-1',
				typeId: '2455059983007225754',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageExclude: {
				id: '-1',
				typeId: '2455059983007225811',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageIoMode: {
				id: '-1',
				typeId: '2455059983007225813',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageModeCode: {
				id: '-1',
				typeId: '2455059983007225810',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePriority: {
				id: '-1',
				typeId: '2455059983007225806',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageProtocol: {
				id: '-1',
				typeId: '2455059983007225809',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRateVer: {
				id: '-1',
				typeId: '2455059983007225805',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptCmdWord: {
				id: '-1',
				typeId: '2455059983007225808',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptWordCount: {
				id: '-1',
				typeId: '2455059983007225807',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRunBeforeProc: {
				id: '-1',
				typeId: '2455059983007225812',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageVer: {
				id: '-1',
				typeId: '2455059983007225804',
				gammaId: '-1',
				value: '',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			publisherNodes: [],
			subscriberNodes: [],
			subMessages: [],
		},
	});
	protected message = computed(() => this.menuData().message);
	inEditMode = computed(() => this.menuData().editMode);
	header = computed(() => this.menuData().header);
	private _header$ = toObservable(this.header);

	private _headerName$ = this._header$.pipe(
		switchMap((h) => this.getHeaderByName(h)),
		map((x) => x.humanReadable)
	);
	protected headerName = toSignal(this._headerName$, {
		initialValue: 'Name',
	});
	private messageService = inject(CurrentMessagesService);
	private dialog = inject(MatDialog);

	private headerService = inject(HeaderService);
	protected open = writableSlice(this.menuData, 'open');
	protected menuPositionX = computed(() => this.menuData().x);
	protected menuPositionY = computed(() => this.menuData().y);
	protected matMenuTrigger = viewChild.required(MatMenuTrigger);
	private _openMenu = effect(
		() => {
			if (this.open()) {
				this.matMenuTrigger().openMenu();
			}
		},
		{ allowSignalWrites: true }
	);

	protected copyTooltip = computed(() => {
		if (this.inEditMode() === false) {
			return `Switch to edit mode to perform edit actions on ${
				this.message().name.value
			}.`;
		}
		if (this.message().deleted) {
			return `Cannot perform edits on deleted messages.`;
		}
		return `Create a copy of ${this.message().name.value}.`;
	});

	protected removeTooltip = computed(() => {
		if (this.inEditMode() === false) {
			return `Switch to edit mode to perform edit actions on ${
				this.message().name.value
			}.`;
		}
		if (this.message().deleted) {
			return `Cannot perform edits on deleted messages.`;
		}
		return `Remove ${this.message().name.value} from connection.`;
	});

	protected deleteTooltip = computed(() => {
		if (this.inEditMode() === false) {
			return `Switch to edit mode to perform edit actions on ${
				this.message().name.value
			}.`;
		}
		if (this.message().deleted) {
			return `Cannot perform edits on deleted messages.`;
		}
		if (
			this.message().subMessages.filter((submsg) => submsg.id !== '-1')
				.length !== 0
		) {
			return `Cannot delete messages which have submessages. # of submessages:${
				this.message().subMessages.filter(
					(submsg) => submsg.id !== '-1'
				).length
			}.
			Remove the following submessages to delete:
			${this.message()
				.subMessages.filter((submsg) => submsg.id !== '-1')
				.map((x) => x.name.value)}
			
			`;
		}
		return `Delete ${this.message().name.value}.`;
	});

	protected diffTooltip = computed(() => {
		const msg = this.message();
		const hdr = this.header();
		if (!this.hasChanges(msg)) {
			return `Message ${msg.name.value} has no changes. Make edits to this message or switch to show deltas mode in order to view differences.`;
		}
		if (!this.changeExists(msg, hdr)) {
			return `No ${hdr} changes for ${msg.name.value}. Make edits to this property in order to view differences.`;
		}
		return `View changes for ${hdr} on ${msg.name.value}`;
	});
	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'message');
	}
	hasChanges(value: message): value is Required<message> {
		return (value as message).changes !== undefined;
	}
	changeExists(
		value: message,
		header: keyof message
	): header is keyof _messageChanges {
		return (
			(value as Required<message>).changes[
				header as keyof _messageChanges
			] !== undefined
		);
	}
	viewDiff(open: boolean, value: difference, header: string) {
		this.messageService.sideNav = {
			opened: open,
			field: header,
			currentValue: value.currentValue as string | number | applic,
			previousValue: value.previousValue as
				| string
				| number
				| applic
				| undefined,
			transaction: value.transactionToken,
		};
	}
	copyMessageDialog() {
		const clonedMessage = structuredClone(this.message());
		clonedMessage.id = '-1';
		clonedMessage.gammaId = '-1';
		clonedMessage.name.id = '-1';
		clonedMessage.description.id = '-1';
		clonedMessage.interfaceMessageExclude.id = '-1';
		clonedMessage.interfaceMessageIoMode.id = '-1';
		clonedMessage.interfaceMessageModeCode.id = '-1';
		clonedMessage.interfaceMessageNumber.id = '-1';
		clonedMessage.interfaceMessagePeriodicity.id = '-1';
		clonedMessage.interfaceMessagePriority.id = '-1';
		clonedMessage.interfaceMessageProtocol.id = '-1';
		clonedMessage.interfaceMessageRate.id = '-1';
		clonedMessage.interfaceMessageRateVer.id = '-1';
		clonedMessage.interfaceMessageRptCmdWord.id = '-1';
		clonedMessage.interfaceMessageRptWordCount.id = '-1';
		clonedMessage.interfaceMessageRunBeforeProc.id = '-1';
		clonedMessage.interfaceMessageType.id = '-1';
		clonedMessage.interfaceMessageVer.id = '-1';
		clonedMessage.interfaceMessageWriteAccess.id = '-1';
		const dialogData = clonedMessage;
		const dialogRef = this.dialog.open(AddMessageDialogComponent, {
			data: dialogData,
		});
		dialogRef
			.afterClosed()
			.pipe(
				first(),
				filter((val) => val !== undefined),
				switchMap((val) => this.messageService.createMessage(val))
			)
			.subscribe();
	}
	deleteMessage() {
		this.dialog
			.open(DeleteMessageDialogComponent, {
				data: { message: this.message() },
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.messageService.deleteMessage(this.message().id),
						of()
					)
				)
			)
			.subscribe();
	}

	openDescriptionDialog() {
		const previous = structuredClone(this.message());
		this.dialog
			.open(EditViewFreeTextFieldDialogComponent, {
				data: {
					original: this.message().description.value,
					type: 'Description',
					return: this.message().description.value,
					editable: this.inEditMode(),
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
						this.messageService.partialUpdateMessage(
							{
								...this.message(),
								description: {
									id: this.message().description.id,
									typeId: this.message().description.typeId,
									gammaId: this.message().description.gammaId,
									value: (response as EditViewFreeTextDialog)
										.return,
								},
							},
							previous
						)
					)
				)
			)
			.subscribe();
	}
	removeMessage() {
		//open dialog, iif result ==='ok' messageservice. removemessage
		this.dialog
			.open(RemoveMessageDialogComponent, {
				data: { message: this.message() },
			})
			.afterClosed()
			.pipe(
				take(1),
				switchMap((dialogResult: string) =>
					iif(
						() => dialogResult === 'ok',
						this.messageService.removeMessage(this.message().id),
						of()
					)
				)
			)
			.subscribe();
	}
}
