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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { message, subMessage } from '@osee/messaging/shared/types';
import { first, filter, switchMap, take, map, share, shareReplay } from 'rxjs';
import { AddMessageDialogComponent } from '../dialogs/add-message-dialog/add-message-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { AddSubMessageDialogComponent } from '../dialogs/add-sub-message-dialog/add-sub-message-dialog.component';
import { AddSubMessageDialog } from '../types/AddSubMessageDialog';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { MatBadge } from '@angular/material/badge';
import { MatFabButton, MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { applicabilitySentinel } from '@osee/applicability/types';

@Component({
	selector: 'osee-message-table-toolbar-add-actions',
	standalone: true,
	imports: [
		MatFabButton,
		MatIcon,
		MatTooltip,
		MatButton,
		MatMenu,
		MatMenuItem,
		MatMenuTrigger,
		MatBadge,
	],
	template: ` <div class="tw-flex tw-items-center tw-justify-between">
			<div
				[matTooltip]="
					isEditing() === false
						? 'Switch to edit mode to add submessages'
						: messages().length === 0
							? 'Expand a message to add submessages'
							: 'Add an submessage to an existing message'
				"
				class="tw-px-4">
				<button
					mat-stroked-button
					class="tertiary-badge"
					[matBadge]="messages().length"
					[matBadgeHidden]="
						isEditing() === false || messages().length === 0
					"
					[matMenuTriggerFor]="addSubmessageMenu"
					[disabled]="
						isEditing() === false || messages().length === 0
					">
					{{
						isEditing() === false || messages().length === 0
							? 'Submessage Creation Disabled'
							: 'Add Submessage to:'
					}}
				</button>
			</div>
			<span class="tw-flex-auto"></span>
			<div
				class="tw-pr-4"
				[matTooltip]="
					isEditing() === false
						? 'Switch to edit mode to add messages'
						: 'Add a message'
				">
				<button
					mat-fab
					class="tertiary-fab"
					[disabled]="isEditing() === false"
					(click)="openNewMessageDialog()">
					<mat-icon>add</mat-icon>
				</button>
			</div>
		</div>
		<mat-menu #addSubmessageMenu>
			@for (message of messages(); track message.id) {
				<button
					mat-menu-item
					(click)="createNewSubMessage(message)">
					<mat-icon class="tw-text-success">add_circle</mat-icon>
					{{ message.name.value }}
				</button>
			}
		</mat-menu>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MessageTableToolbarAddActionsComponent {
	private messageService = inject(CurrentMessagesService);
	private dialog = inject(MatDialog);
	protected messages = this.messageService.expandedRows;
	protected preferences = this.messageService.preferences;
	private _isEditing$ = this.preferences.pipe(
		map((x) => x.inEditMode),
		share(),
		shareReplay(1),
		takeUntilDestroyed()
	);
	protected isEditing = toSignal(this._isEditing$, { initialValue: false });
	openNewMessageDialog() {
		const dialogData: message = {
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
		};
		const dialogRef = this.dialog.open(AddMessageDialogComponent, {
			data: dialogData,
			minWidth: '80vw',
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
	createNewSubMessage(message: message) {
		const defaultSubMsg: subMessage = {
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
			interfaceSubMessageNumber: {
				id: '-1',
				typeId: '2455059983007225769',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
		};
		this.dialog
			.open(AddSubMessageDialogComponent, {
				minWidth: '60vw',
				minHeight: '60vh',
				data: {
					name: message.name.value,
					id: message.id,
					subMessage: defaultSubMsg,
				},
			})
			.afterClosed()
			.pipe(
				take(1),
				filter((val) => val !== undefined),
				switchMap((z: AddSubMessageDialog) =>
					z != undefined &&
					z.subMessage != undefined &&
					z.subMessage.id != undefined &&
					z?.subMessage?.id.length > 0 &&
					z.subMessage.id !== '-1'
						? this.messageService.relateSubMessage(
								z.id,
								z?.subMessage?.id || '-1'
							)
						: this.messageService.createSubMessage(
								z.subMessage,
								z.id
							)
				)
			)
			.subscribe();
	}
}
