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
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MarkdownModule } from 'ngx-markdown';
import { ActivatedRoute } from '@angular/router';

@Component({
	selector: 'osee-messaging-help-static-content',
	standalone: true,
	imports: [CommonModule, MarkdownModule],
	templateUrl: './messaging-help-static-content.component.html',
	styleUrls: ['./messaging-help-static-content.component.scss'],
})
export class MessagingHelpStaticContentComponent implements OnInit {
	constructor(private route: ActivatedRoute) {}

	markdown: string | null = null;

	ngOnInit(): void {
		this.route.paramMap.subscribe((params) => {
			let name = params.get('helpPage');
			if (name) {
				this.markdown = 'assets/help/mim/' + name + '.md';
			}
		});
	}
}

export default MessagingHelpStaticContentComponent;
