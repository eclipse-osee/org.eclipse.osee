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
import { Component } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { CheckboxContainerService } from '../../services/command-palette-services/checkbox-container.service';

@Component({
	selector: 'osee-checkbox-container',
	templateUrl: './checkbox-container.component.html',
	styleUrls: ['./checkbox-container.component.sass'],
})
export class CheckboxContainerComponent {
	checked: boolean = true;

	constructor(private checkboxContainerService: CheckboxContainerService) {}

	onChanged(event: MatCheckboxChange) {
		this.checkboxContainerService.updateClearIsChecked(event.checked);
	}
}
