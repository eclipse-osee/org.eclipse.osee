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
import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BuildNum, version } from './version';

@Component({
	selector: 'osee-about',
	templateUrl: './about.component.html',
	styles: [],
	standalone: true,
	imports: [MatFormFieldModule],
})
export class AboutComponent {
	buildNumber = BuildNum;
	version = version;
	constructor() {}
}

export default AboutComponent;
