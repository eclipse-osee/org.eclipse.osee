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
import { Optional } from '@angular/core';
import { ControlContainer, NgForm, NgModelGroup } from '@angular/forms';

function controlContainerFactory(controlContainer?: ControlContainer) {
	return controlContainer;
}
export function provideOptionalControlContainerNgForm() {
	return {
		provide: ControlContainer,
		useFactory: controlContainerFactory,
		deps: [[new Optional(), NgForm]],
	};
}

export function provideOptionalControlContainerNgModelGroup() {
	return {
		provide: ControlContainer,
		useFactory: controlContainerFactory,
		deps: [[new Optional(), NgModelGroup]],
	};
}
