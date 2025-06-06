/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, Input } from '@angular/core';
import { MatFabButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

/**
 * To use, add the following anchor tag to the top of the page this component is scrolling on.
 *
 * <a id="page-top" aria-hidden="true"></a>
 */
@Component({
	selector: 'osee-scroll-to-top-button',
	templateUrl: './scroll-to-top-button.component.html',
	styles: [],
	imports: [MatFabButton, MatIcon],
})
export class ScrollToTopButtonComponent {
	@Input() scrollId = 'page-top';

	scrollToTop() {
		document.getElementById(this.scrollId)?.scrollIntoView({
			behavior: 'smooth',
			block: 'start',
		});
	}
}
