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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { combineLatest, of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { HeaderService } from '../../../shared/services/ui/header.service';

@Component({
	selector: 'osee-messaging-help-column-descriptions',
	templateUrl: './column-descriptions-message-help.component.html',
	styleUrls: ['./column-descriptions-message-help.component.sass'],
	standalone: true,
	imports: [NgIf, NgFor, AsyncPipe, MatTableModule, MatListModule],
})
export class ColumnDescriptionsMessageHelpComponent {
	list = combineLatest([
		this.headerService.AllElements,
		this.headerService.AllStructures,
		this.headerService.AllMessages,
		this.headerService.AllSubMessages,
	]).pipe(
		switchMap(
			([
				elementheaders,
				structureheaders,
				messageheaders,
				submessageheaders,
			]) =>
				of([
					{
						elements: elementheaders,
						structures: structureheaders,
						submessages: submessageheaders,
						messages: messageheaders,
					},
				])
		)
	);
	constructor(private headerService: HeaderService) {}
}
export default ColumnDescriptionsMessageHelpComponent;
