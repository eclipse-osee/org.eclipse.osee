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
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	CurrentMessagesService,
	TransportTypeUiService,
} from '@osee/messaging/shared/services';
import { ConnectionNode, message } from '@osee/messaging/shared/types';
import { FormsModule, ValidationErrors } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	of,
	shareReplay,
	Subject,
	switchMap,
	tap,
} from 'rxjs';
import { MessageNodesCountDirective } from '@osee/messaging/shared/directives';

@Component({
	selector: 'osee-edit-message-nodes-field',
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		CommonModule,
		FormsModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MessageNodesCountDirective,
	],
	templateUrl: './edit-message-nodes-field.component.html',
})
export class EditMessageNodesFieldComponent {
	@Input() message!: message;
	@Input() header!: keyof message;
	@Input() value: ConnectionNode[] = [];

	constructor(
		private currentMessageService: CurrentMessagesService,
		private transportTypeService: TransportTypeUiService
	) {
		this._updateNodes.subscribe();
	}

	nodes = this.currentMessageService.connectionNodes;
	transportType = this.transportTypeService.currentTransportType;

	min = this.transportType.pipe(
		switchMap((type) => {
			// If min is 0, return 1. 0 represents no limit.
			return this.header === 'publisherNodes'
				? of(Math.max(type.minimumPublisherMultiplicity, 1))
				: of(Math.max(type.minimumSubscriberMultiplicity, 1));
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	max = this.transportType.pipe(
		switchMap((type) => {
			return this.header === 'publisherNodes'
				? of(type.maximumPublisherMultiplicity)
				: of(type.maximumSubscriberMultiplicity);
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	multiple = this.max.pipe(
		switchMap((max) => {
			return of(max !== 1);
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	_value = this.multiple.pipe(
		switchMap((mult) => {
			if (mult) {
				return of(this.value);
			}
			return this.value.length === 0 ? of(undefined) : of(this.value[0]);
		})
	);

	_selected = new BehaviorSubject<ConnectionNode[]>([]);
	_sendTx = new Subject<boolean>();

	_updateNodes = combineLatest([this._selected, this._sendTx]).pipe(
		filter(([selected, _]) => selected.length > 0),
		switchMap(([selected, _]) =>
			this.currentMessageService
				.updateMessageNodeRelations(
					this.message,
					selected,
					this.header === 'publisherNodes'
				)
				.pipe(tap(() => this._selected.next([])))
		)
	);

	compareNodes(node1: ConnectionNode, node2: ConnectionNode) {
		return node1 && node2 ? node1.id === node2.id : false;
	}

	sendTx(errors: ValidationErrors | null) {
		if (!errors) {
			this._sendTx.next(true);
		}
	}

	updateNodes(val: ConnectionNode[] | ConnectionNode) {
		if (Array.isArray(val)) {
			this._selected.next(val);
		} else {
			this._selected.next([val]);
		}
	}
}
