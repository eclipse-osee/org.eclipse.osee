import { test, expect } from '@ngx-playwright/test';

const branchName = 'New Artifact Branch';

test('test', async ({ page }) => {
	//Go to Artifact Explorer page
	await page.goto('http://localhost:4200/ple/artifact/explorer');

	// Search & Pick product line branch then look for its change report
	await page.getByRole('radio', { name: 'Product Line' }).check();
	await page.goto('http://localhost:4200/ple/artifact/explorer/baseline');
	await page.getByRole('combobox', { name: 'Select a Branch' }).click();
	await page.getByText('SAW_Bld_1').click();
	await page.getByRole('button').filter({ hasText: 'differences' }).click();
	await page
		.getByRole('tab', { name: 'Change Report - SAW_Bld_1' })
		.getByRole('button')
		.click();

	//Create an action: create a branch
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByRole('textbox', { name: 'Title' }).click();
	await page.getByRole('textbox', { name: 'Title' }).fill(branchName);
	await page.getByRole('combobox', { name: 'Actionable Item' }).click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('Req');
	await page.getByText('SAW PL Requirements').click();
	await page.getByRole('textbox', { name: 'Description' }).click();
	await page.getByRole('textbox', { name: 'Description' }).fill('test');
	await page
		.getByRole('combobox', { name: 'Change Type' })
		.locator('svg')
		.click();
	await page.getByText('Improvement').click();
	await page
		.getByRole('combobox', { name: 'Targeted Version' })
		.locator('path')
		.click();
	await page.getByText('SAW Product Line').click();
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByText('Analyzeexpand_more').click();
	await page
		.getByRole('menuitem', { name: 'Transition to Cancelled' })
		.click();
	await page.getByText('Cancelledexpand_more').click();
	await page
		.getByRole('menuitem', { name: 'Transition to Implement' })
		.click();
	await page.getByRole('button', { name: 'Create Branch' }).click();
	await expect(page.getByText(branchName)).toBeVisible();

	//Add an child in Software Requirement Markdown
	const child_name = 'Add New Software Req';
	await page.goto('http://localhost:4200/ple/artifact/explorer');
	await page.getByRole('radio', { name: 'Working' }).check();
	await page.goto('http://localhost:4200/ple/artifact/explorer/working');
	await page.getByText('Select a Branch').click();
	await page
		.getByRole('option', { name: 'TW1 - Test Create Action' })
		.locator('span')
		.click();
	await page.getByRole('button', { name: 'Software Requirements -' }).click({
		button: 'right',
	});
	await page.getByRole('menuitem', { name: 'Create Child Artifact' }).click();
	await page.getByRole('textbox', { name: 'Enter a Name:' }).click();
	await page.getByRole('textbox', { name: 'Enter a Name:' }).fill(child_name);
	await page.getByText('arrow_drop_down').click();
	await page.getByRole('combobox', { name: 'Select a Type:' }).fill('Mark');
	await page.getByText('Software Requirement -').click();
	await page
		.locator('osee-attributes-editor div')
		.filter({ hasText: 'IDAL: arrow_drop_down' })
		.locator('mat-icon')
		.click();
	await page.getByRole('option', { name: 'A' }).click();
	await page.locator('#mat-input-15').click();
	await page.locator('#mat-input-15').fill('test');
	await page
		.getByRole('textbox', { name: 'Start typing Markdown text...' })
		.click();
	await page
		.getByRole('textbox', { name: 'Start typing Markdown text...' })
		.fill('line one');
	await page
		.locator('osee-attributes-editor div')
		.filter({ hasText: 'Qualification Method: arrow_drop_down' })
		.locator('mat-icon')
		.click();
	await page.getByText('Test', { exact: true }).click();
	await page
		.locator('osee-attributes-editor div')
		.filter({ hasText: 'Subsystem: arrow_drop_down' })
		.locator('mat-icon')
		.click();
	await page.getByText('Chassis').click();
	await page
		.locator('osee-attributes-editor div')
		.filter({ hasText: 'Partition: arrow_drop_down' })
		.locator('mat-icon')
		.click();
	await page.getByRole('option', { name: 'Navigation' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await expect(page.getByText(child_name)).toBeVisible();

	//Open new created child, modify: adding new text, then save and close.
	await page.getByRole('button', { name: child_name }).dblclick();
	await page
		.getByRole('textbox', { name: 'Start typing Markdown text...' })
		.click();
	await page
		.getByRole('textbox', { name: 'Start typing Markdown text...' })
		.fill('line one, line two');
	await page.getByRole('button').filter({ hasText: 'save' }).click();
	await page
		.getByRole('tab', { name: child_name })
		.getByRole('button')
		.click();

	//Open Team Workflow to Commit the branch
	await page
		.locator('osee-artifact-hierarchy-panel')
		.getByRole('button')
		.filter({ hasText: 'assignment' })
		.click();
	await page.getByRole('button', { name: 'Open Commit Manager' }).click();
	await page.getByRole('button', { name: 'Commit Branch' }).click();
	await page.getByRole('button', { name: 'Close' }).click();

	//Check the baseline branch to verify the commitment
	await page.goto('http://localhost:4200/ple/artifact/explorer/baseline');
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();
	await page
		.locator('#cdk-drop-list-2 div')
		.filter({
			hasText: 'chevron_right folder Software Requirements - Markdown',
		})
		.getByRole('button')
		.first()
		.click();
	await page.getByRole('button', { name: child_name }).dblclick();
	await expect(page.getByText(child_name)).toBeVisible();
});
