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
const _TWCLASSES = [
	'',
	'tw-text-primary-50',
	'tw-text-primary-100',
	'tw-text-primary-200',
	'tw-text-primary-300',
	'tw-text-primary-400',
	'tw-text-primary-500',
	'tw-text-primary-600',
	'tw-text-primary-700',
	'tw-text-primary-800',
	'tw-text-primary-900',
	'tw-text-warning-50',
	'tw-text-warning-100',
	'tw-text-warning-200',
	'tw-text-warning-300',
	'tw-text-warning-400',
	'tw-text-warning-500',
	'tw-text-warning-600',
	'tw-text-warning-700',
	'tw-text-warning-800',
	'tw-text-warning-900',
	'tw-text-success-50',
	'tw-text-success-100',
	'tw-text-success-200',
	'tw-text-success-300',
	'tw-text-success-400',
	'tw-text-success-500',
	'tw-text-success-600',
	'tw-text-success-700',
	'tw-text-success-800',
	'tw-text-success-900',
	'tw-text-accent-50',
	'tw-text-accent-100',
	'tw-text-accent-200',
	'tw-text-accent-300',
	'tw-text-accent-400',
	'tw-text-accent-500',
	'tw-text-accent-600',
	'tw-text-accent-700',
	'tw-text-accent-800',
	'tw-text-accent-900',
] as const;

const _TWCOLORS = ['', 'primary', 'accent', 'warning', 'success'] as const;
const _TWSHADES = [
	'',
	'50',
	'100',
	'200',
	'300',
	'400',
	'500',
	'600',
	'700',
	'800',
	'900',
] as const;
const _ICONVARIANTS = ['', 'outlined', 'round', 'sharp', 'two-tone'] as const;

export type twColor = (typeof _TWCOLORS)[number];

export type twShade = (typeof _TWSHADES)[number];

export type iconVariant = (typeof _ICONVARIANTS)[number];

type twClasses = (typeof _TWCLASSES)[number];

export type twColorClasses = `` | twClasses | `${twClasses} dark:${twClasses}`;
