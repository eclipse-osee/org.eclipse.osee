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
import { WritableSignal, computed } from '@angular/core';

/**
 * Function which will grab a property off a writable signal and make a writable signal off that property
 * @param sourceSignal
 * @param property
 */
export function writableSlice<T, K extends keyof T>(
	sourceSignal: WritableSignal<T>,
	property: K
): WritableSignal<T[K]> {
	let _computed = computed(() => sourceSignal()[property]) as WritableSignal<
		T[K]
	>;
	let _set = function (value: T[K]) {
		let _newValue = { ...sourceSignal() };
		_newValue[property] = value;
		sourceSignal.set(_newValue);
	};
	//no-op for now?kinda challenging
	let _update = function (updateFn: (value: T[K]) => T[K]) {};
	_computed.set = _set;
	_computed.update = _update;
	return _computed;
}

export function writableArraySlice<T>(
	sourceSignal: WritableSignal<T[]>,
	index: number
): WritableSignal<T> {
	let _computed = computed(() => sourceSignal()[index]) as WritableSignal<T>;
	let _set = function (value: T) {
		let _newArr = [...sourceSignal()];
		_newArr[index] = value;
		sourceSignal.set(_newArr);
	};
	//no-op for now?kinda challenging
	let _update = function (updateFn: (value: T) => T) {};
	_computed.set = _set;
	_computed.update = _update;
	return _computed;
}
