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
import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import type { message, messageWithChanges } from '@osee/messaging/shared/types';
import { Observable, takeUntil } from 'rxjs';
/**
 * Not in use yet
 */
export class messageDataSource extends DataSource<
	message | messageWithChanges
> {
	constructor(private messageService: CurrentMessagesService) {
		super();
	}
	connect(
		collectionViewer: CollectionViewer
	): Observable<readonly (message | messageWithChanges)[]> {
		return this.messageService.messages.pipe(
			takeUntil(this.messageService.done)
		);
	}

	disconnect(collectionViewer: CollectionViewer): void {}
}
