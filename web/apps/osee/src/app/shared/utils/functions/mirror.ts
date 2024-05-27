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
import { Signal, computed, signal, untracked } from '@angular/core';

export type MirrorSignal<T> = {
	// [SIGNAL]: T; //this currently doesn't work in certain contexts
	(): T;
	set(value: T): void;
	update(updateFn: (value: T) => T): void;
	asReadOnly(): Signal<T>;
};

export function mirror<T>(expression: () => T): MirrorSignal<T> {
	const inner = computed(() => signal(expression()));
	const setFn = (value: T) => {
		const newS = untracked(inner);
		return newS.set(value);
	};
	const update = (updateFn: (value: T) => T) => {
		const newS = untracked(inner);
		return newS.update(updateFn);
	};
	const readOnly = () => {
		return inner();
	};
	const mirror: MirrorSignal<T> = () => inner()();

	mirror.set = setFn;
	mirror.update = update;
	mirror.asReadOnly = readOnly;
	//note this freezes up occasionally, so leaving it commented out until we can figure out the bugs
	// mirror[SIGNAL] = inner()();
	return mirror;
}
