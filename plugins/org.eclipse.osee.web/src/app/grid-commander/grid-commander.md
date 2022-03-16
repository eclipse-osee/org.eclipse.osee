Install Node version manager		https://github.com/nvm-sh/nvm
	curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
	restart console (install automatically updated .bashrc)

Install Node
	vm install 16

Install Node Packages
	npm install -g @angular/cli

Create Project
	ng new grid-commander --style scss --routing false

cd grid-commander
code .

VS Code install Angular Language Service
	VS Code > Crtl+Shift+P > Extensions: Install Extensions > Angular Language Service
	Ctrl + ` to open terminal
VS Code > Terminal > Get-ExecutionPolicy
	Windows PowerShell > Run as Administrator > Set-ExecutionPolicy Bypass

git config --global http.proxy ""
disconnect from the VPN

Install Angular Material
	ng add @angular/material --skip-confirmation

Install Angular Gird Library
	npm install --save ag-grid-community ag-grid-angular

ng serve --open
ctrl-c to kill server




git reset --hard origin/dev
git checkout -b grid
cd /c/Tools/git/org.eclipse.osee/plugins/org.eclipse.osee.web/src/app
ng generate module --module app grid-commander --route gc
cd grid-commander
ng serve


Backwards and forward navigation via browser built-in navigation
Command history with timestamps
Show command provides filtering or use filter keyword
command icon shows in history, command pallet, and help
commands can use row and column selections done via mouse

the default value for the next parameter is shown dimmed. space bar will select it. The previous
arrow keys move the selected row and column. The shift modifier combined with arrows does range selection.
Commands issued via the command pallet or GUI are added into the command history

Every grid has
	Name
	Url
	Description
	Icon
	Type
	Permissions
	Column definitions
	Rows maybe empty

Look up CSS setup needed for angular grid and maybe material



Command prefix precedes a command
no prefix sends results to current tab
& send results to a new tab and don't switch to it
+ send results to a new tab and switch to it
? Show help for command
- suppress output of command
/ find command in history.  By default failed commands are excluded from search results
@<destination> sends command text to user in new tab if <destination> is a <username>. If <destination> is 
=<command_name> adds command named <command_name>  Allow defining a command as a sequence of existing commands
| passes the results of the previous command to the next command.  If there is no result or the next command doesn't take input then effect is just to separate the two commands and run them sequentially
Number in front of a command repeats the command that number of times
0 prefix dryruns the command by validating it


add
	add <grid_name>:  adds a grid named <grid_name>
	add row: 
	add column:
find
get
	get <grid_name> <primary_key>
help
	help:  show the full command definitions included user defined commands
hide
	hide row
	hide column
remove
set
	set: shows settings grid
	set key <key_name> <value>:  sets the value of<key_name> to <value>.  If <value> is not provided the empty string is used
	set color:  any cell, row, or column (applied in that order of precedence)
	set size:  any cell, row, or column (applied in that order of precedence)
	set default <command_name>:  resets the parameters of <command> to their defaults
	set default grid <grid_name>:  sets the grid named <grid_name> used for 'show' with no arguments
	set <grid_name> <primary_key>
show
	show:  shows default grid which is initially the command history grid
	show <grid_name>:  shows the grid with the name <grid_name>
	show <url>:  show url
sort
	sort <column_name>+


	