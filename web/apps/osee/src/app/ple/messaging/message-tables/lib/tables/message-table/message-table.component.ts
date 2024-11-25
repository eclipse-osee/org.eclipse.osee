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
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import {
	ChangeDetectionStrategy,
	Component,
	signal,
	inject,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {
	MatMenu,
	MatMenuContent,
	MatMenuItem,
	MatMenuTrigger,
} from '@angular/material/menu';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { RouterLink } from '@angular/router';
import { iif, of } from 'rxjs';
import {
	map,
	share,
	shareReplay,
	switchMap,
	take,
	takeUntil,
} from 'rxjs/operators';

import {
	CdkDrag,
	CdkDragDrop,
	CdkDragHandle,
	CdkDropList,
} from '@angular/cdk/drag-drop';
import { AsyncPipe, NgClass } from '@angular/common';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatFormField,
	MatHint,
	MatLabel,
	MatPrefix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatPaginator } from '@angular/material/paginator';
import { MatTooltip } from '@angular/material/tooltip';
import { PersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown';
import { applic } from '@osee/applicability/types';
import { PersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle';
import { PersistedNumberAttributeInputComponent } from '@osee/attributes/persisted-number-attribute-input';
import { PersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input';
import { PersistedMessagePeriodicityDropdownComponent } from '@osee/messaging/message-periodicity/persisted-message-periodicity-dropdown';
import { PersistedMessageTypeDropdownComponent } from '@osee/messaging/message-type/persisted-message-type-dropdown';
import { PersistedPublisherNodeDropdownComponent } from '@osee/messaging/nodes/persisted-publisher-node-dropdown';
import { PersistedSubscriberNodeDropdownComponent } from '@osee/messaging/nodes/persisted-subscriber-node-dropdown';
import { PersistedRateDropdownComponent } from '@osee/messaging/rate/persisted-rate-dropdown';
import {
	CurrentMessagesService,
	HeaderService,
} from '@osee/messaging/shared/services';
import type {
	_messageChanges,
	message,
	messageWithChanges,
	nodeData,
} from '@osee/messaging/shared/types';
import {
	HighlightFilteredTextDirective,
	writableSlice,
} from '@osee/shared/utils';
import { MessageMenuComponent } from '../../menus/message-menu/message-menu.component';
import { MessageImpactsValidatorDirective } from '../../message-impacts-validator.directive';
import { SubMessageTableComponent } from '../sub-message-table/sub-message-table.component';
import { CurrentViewSelectorComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-messaging-message-table',
	templateUrl: './message-table.component.html',
	styles: [],
	changeDetection: ChangeDetectionStrategy.OnPush,
	animations: [
		trigger('detailExpand', [
			state('collapsed', style({ maxHeight: '0vh' })),
			state('expanded', style({ maxHeight: '60vh' })),
			transition(
				'expanded <=> collapsed',
				animate('225ms cubic-bezier(0.42, 0.0, 0.58, 1)')
			),
		]),
		trigger('expandButton', [
			state('closed', style({ transform: 'rotate(0)' })),
			state('open', style({ transform: 'rotate(-180deg)' })),
			transition(
				'open => closed',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
			transition(
				'closed => open',
				animate('250ms cubic-bezier(0.4, 0.0, 0.2, 1)')
			),
		]),
	],
	imports: [
		AsyncPipe,
		RouterLink,
		FormsModule,
		NgClass,
		CdkDrag,
		CdkDragHandle,
		CdkDropList,
		MatFormField,
		MatLabel,
		MatInput,
		MatIcon,
		MatPrefix,
		MatHint,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatButton,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
		MatMenu,
		MatMenuContent,
		MatMenuItem,
		MatMenuTrigger,
		HighlightFilteredTextDirective,
		SubMessageTableComponent,
		PersistedApplicabilityDropdownComponent,
		PersistedNumberAttributeInputComponent,
		PersistedStringAttributeInputComponent,
		PersistedBooleanAttributeToggleComponent,
		PersistedMessagePeriodicityDropdownComponent,
		PersistedMessageTypeDropdownComponent,
		PersistedRateDropdownComponent,
		CurrentViewSelectorComponent,
		PersistedPublisherNodeDropdownComponent,
		PersistedSubscriberNodeDropdownComponent,
		MessageMenuComponent,
		MessageImpactsValidatorDirective,
	],
})
export class MessageTableComponent {
	private messageService = inject(CurrentMessagesService);
	dialog = inject(MatDialog);
	private headerService = inject(HeaderService);

	messageData = this.messageService.messages.pipe(
		switchMap((data) =>
			of(new MatTableDataSource<message | messageWithChanges>(data))
		),
		takeUntil(this.messageService.done)
	);

	headers = this.headerService.AllMessageHeaders.pipe(
		switchMap((headers) =>
			of(['rowControls', ...headers] as (keyof message | 'rowControls')[])
		)
	);
	nonEditableHeaders: (keyof message)[] = [];
	expandedElement = this.messageService.expandedRows;
	preferences = this.messageService.preferences.pipe(
		takeUntil(this.messageService.done)
	);
	inEditMode = this.preferences.pipe(
		map((r) => r.inEditMode),
		share(),
		shareReplay(1),
		takeUntil(this.messageService.done)
	);
	protected filter = this.messageService.messageFilter;
	private _inEditMode = toSignal(this.inEditMode, { initialValue: false });
	protected menuData = signal<{
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
	protected menuPositionX = writableSlice(this.menuData, 'x');
	protected menuPositionY = writableSlice(this.menuData, 'y');
	protected menuEditMode = writableSlice(this.menuData, 'editMode');
	protected menuOpen = writableSlice(this.menuData, 'open');
	protected menuHeader = writableSlice(this.menuData, 'header');
	protected menuMessage = writableSlice(this.menuData, 'message');

	inDiffMode = this.messageService.isInDiff.pipe(
		switchMap((val) => iif(() => val, of('true'), of('false')))
	);
	_connectionsRoute = this.messageService.connectionsRoute;
	messages = this.messageService.messages;
	connectionId = this.messageService.connectionIdSignal;

	rowIsExpanded(value: `${number}`) {
		return this.messageService
			.expandedRows()
			.map((s) => s.id)
			.includes(value);
	}
	expandRow(value: message | messageWithChanges) {
		this.messageService.addExpandedRow = value;
	}
	hideRow(value: message | messageWithChanges) {
		this.messageService.removeExpandedRow = value;
	}

	rowChange(value: message | messageWithChanges, type: boolean) {
		if (type) {
			this.expandRow(value);
		} else {
			this.hideRow(value);
		}
	}

	valueTracker(index: number, _item: keyof message | 'rowControls') {
		return index;
	}

	messageTracker(_index: number, item: message | messageWithChanges) {
		return item.id + item.subMessages.map((x) => x.id).join(':');
	}

	getNodeNames(nodes: nodeData[]) {
		return nodes.map((n) => n.name.value).join(', ');
	}

	headerIsChangable(
		value: message,
		header: keyof message
	): header is keyof _messageChanges {
		return (
			(value as Required<message>)[header as keyof _messageChanges] !==
			undefined
		);
	}
	openMenu(
		event: MouseEvent,
		message: message,
		_field: string | boolean | applic,
		header: keyof message | 'rowControls'
	) {
		if (
			header !== 'rowControls' &&
			this.headerIsChangable(message, header)
		) {
			event.preventDefault();
			this.menuPositionX.set(event.clientX + 'px');
			this.menuPositionY.set(event.clientY + 'px');
			this.menuEditMode.set(this._inEditMode());
			this.menuMessage.set(message);
			this.menuHeader.set(header);
			this.menuOpen.set(true);
		}
	}

	handleDragDrop(event: CdkDragDrop<unknown[]>) {
		if (event.currentIndex === event.previousIndex) {
			return;
		}

		this.messageData
			.pipe(
				take(1),
				switchMap((messages) => {
					// Rows not marked as draggable are not included in the index count,
					// so remove them from the list before checking index.
					const tableData =
						messages.data.filter((e) => e.id !== '-1') || [];
					const messageId = tableData[event.previousIndex].id;
					tableData.splice(event.previousIndex, 1);
					const newIndex = event.currentIndex - 1;
					const afterArtifactId =
						newIndex < 0 ? 'start' : tableData[newIndex].id;
					return this.messageService.connectionId.pipe(
						switchMap((connectionId) =>
							this.messageService.changeMessageRelationOrder(
								connectionId,
								messageId,
								afterArtifactId
							)
						)
					);
				})
			)
			.subscribe();
	}

	getHeaderByName(value: string) {
		return this.headerService.getHeaderByName(value, 'message');
	}
}
