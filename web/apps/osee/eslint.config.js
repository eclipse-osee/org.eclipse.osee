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
import { default as eslint } from '@eslint/js';
import { default as angular } from 'angular-eslint';
import { default as tseslint } from 'typescript-eslint';
import tailwind from 'eslint-plugin-tailwindcss';
import eslintConfigPrettier from 'eslint-config-prettier';

export default tseslint.config(
	{
		files: ['**/*.ts'],
		ignores: [
			'projects/**/*',
			'**/tailwind.config.js',
			'**/pnpm-lock.yaml',
			'**/*.mock.ts',
			'src/test.ts',
		],
		languageOptions: {
			parserOptions: {
				project: [
					'tsconfig.app.editor.json',
					'tsconfig.spec.editor.json',
					'cypress/tsconfig.json',
				],
			},
		},
		extends: [
			eslint.configs.recommended,
			...tseslint.configs.recommended,
			...tseslint.configs.stylistic,
			...angular.configs.tsRecommended,
			eslintConfigPrettier,
			...tailwind.configs['flat/recommended'],
		],
		processor: angular.processInlineTemplates,
		rules: {
			'@angular-eslint/directive-selector': [
				'error',
				{
					type: 'attribute',
					prefix: 'osee',
					style: 'camelCase',
				},
			],
			'@angular-eslint/component-selector': [
				'error',
				{
					type: 'element',
					prefix: 'osee',
					style: 'kebab-case',
				},
			],
			'@angular-eslint/no-input-rename': ['off'],
			'@angular-eslint/no-output-rename': ['off'],
			'@typescript-eslint/consistent-type-definitions': ['error', 'type'],
			'@typescript-eslint/no-unused-vars': [
				'error',
				{
					args: 'all',
					argsIgnorePattern: '^_',
					caughtErrors: 'all',
					caughtErrorsIgnorePattern: '^_',
					destructuredArrayIgnorePattern: '^_',
					varsIgnorePattern: '^_',
					ignoreRestSiblings: true,
				},
			],
			'no-restricted-imports': 'off',
			'@typescript-eslint/no-restricted-imports': [
				'error',
				{
					patterns: ['!./*', '!../*', 'src/*'],
				},
			],
		},
	},
	{
		files: ['**/*.html'],
		extends: [
			...angular.configs.templateRecommended,
			...angular.configs.templateAccessibility,
			eslintConfigPrettier,
			...tailwind.configs['flat/recommended'],
		],
		rules: {
			'tailwindcss/no-custom-classname': ['off'],
			'tailwindcss/migration-from-tailwind-2': ['off'],
		},
	}
);
