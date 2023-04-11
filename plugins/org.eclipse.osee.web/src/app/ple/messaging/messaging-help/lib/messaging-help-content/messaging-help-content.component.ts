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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarkdownModule } from 'ngx-markdown';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, filter, of, switchMap } from 'rxjs';
import { HelpService } from '@osee/shared/services/help';

@Component({
	selector: 'osee-messaging-help-content',
	standalone: true,
	imports: [CommonModule, MarkdownModule],
	templateUrl: './messaging-help-content.component.html',
	styleUrls: ['./messaging-help-content.component.scss'],
})
export class MessagingHelpContentComponent {
	constructor(
		private route: ActivatedRoute,
		private helpService: HelpService
	) {
		this.route.paramMap.subscribe((params) => {
			this.id.next(params.get('id') || '');
		});
	}

	id = new BehaviorSubject<string>('');

	markdown = this.id.pipe(
		filter((id) => id !== '' && id !== '-1'),
		switchMap((id) =>
			this.helpService
				.getHelpPage(id)
				.pipe(switchMap((page) => of(page.markdownContent)))
		)
	);
}

export default MessagingHelpContentComponent;
