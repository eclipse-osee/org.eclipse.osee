//import { test, expect } from '@playwright/test';
import { test, expect } from '@ngx-playwright/test';

test('test', async ({ page }) => {
	//Go to Artifact Explorer page
	await page.goto('http://localhost:4200/ple/artifact/explorer');

	//Choose a working branch, view the artifact
	await page.getByRole('radio', { name: 'Working' }).check();
	await page.goto('http://localhost:4200/ple/artifact/explorer/working');
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW PL Working Branch').click();
	await page
		.locator('#cdk-drop-list-1 div')
		.filter({ hasText: 'chevron_right folder Product' })
		.getByRole('button')
		.first()
		.click();
	await page.locator('div:nth-child(2) > button').first().click();
	await page.getByRole('button', { name: 'ROBOT_ARM_LIGHT' }).click();
	expect(page.getByText('ROBOT_ARM_LIGHT')).toBeVisible();
	await page
		.getByRole('tab', { name: 'ROBOT_ARM_LIGHT' })
		.getByRole('button')
		.click();

	//Choose a baseline branch, view the artifact
	await page.goto('http://localhost:4200/ple/artifact/explorer/baseline');
	await page.getByRole('combobox', { name: 'Select a Branch' }).click();
	await page.getByText('Common').click();
	await page.getByRole('button', { name: 'OSEE Configuration' }).click();
	await page.getByRole('button', { name: 'Artifact Info' }).click();
	expect(page.getByText('OSEE Configuration')).toBeVisible();
});
