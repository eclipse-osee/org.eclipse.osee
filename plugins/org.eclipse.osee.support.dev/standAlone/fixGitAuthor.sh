#!/bin/bash

oldNames=("afinkbein" "Andrew Finkbeiner" "andy.jury" "ddunne" "donald.g.dunne" "jmisinco" "john.r.misinco" "jphillips" "kaguilar" "Ken J. Aguilar" "Karol M. Wilk" "kwilk" "Mike Masterson" "mmasterso" "mtelles" "mward" "rbrooks" "rescobar" "rschmitt" "shawn.f.cook" "unknown")
newNames=("Andrew M. Finkbeiner" "Andrew M. Finkbeiner" "Andy Jury" "Donald G. Dunne" "Donald G. Dunne" "John R. Misinco" "John R. Misinco" "Jeff C. Phillips" "Ken J. Aguilar" "Ken J. Aguilar" "Karol M. Wilk" "Karol M. Wilk" "Michael P. Masterson" "Michael P. Masterson" "Megumi Telles" "Matt Ward" "Ryan D. Brooks" "Roberto E. Escobar" "Ryan Schmitt" "Shawn F. Cook" "Ryan D. Brooks")
newEmails=("andrew.m.finkbeiner@boeing.com" "andrew.m.finkbeiner@boeing.com" "andy.jury@boeing.com" "donald.g.dunne@boeing.com" "donald.g.dunne@boeing.com" "misinco@gmail.com" "misinco@gmail.com" "jeff.c.phillips@boeing.com" "ken.aguilar@gmail.com" "ken.aguilar@gmail.com" "karol.m.wilk@boeing.com" "karol.m.wilk@boeing.com" "michael.p.masterson@boeing.com" "michael.p.masterson@boeing.com" "megumi.telles@boeing.com" "matt.ward@eclipse.org" "ryan.d.brooks@boeing.com" "roberto.e.escobar@boeing.com" "ryan.schmitt@boeing.com" "shawn.f.cook@boeing.com" "ryan.d.brooks@boeing.com")

setNameEmail() {
	key=$1
	len=${#oldNames[@]}

	for ((i=0; i < $len; i++))
	do
		if [ "${oldNames[$i]}" = "$key" ]; then
			if [ $2 = "author" ]; then
				export GIT_AUTHOR_NAME="${newNames[$i]}"
				export GIT_AUTHOR_EMAIL="${newEmails[$i]}"
				echo '\nName:' "$GIT_AUTHOR_NAME"
			else
				export GIT_COMMITTER_NAME="${newNames[$i]}"
				export GIT_COMMITTER_EMAIL="${newEmails[$i]}"
			fi
		fi
	done
}

if [ "$GIT_AUTHOR_NAME" = "unknown" -a "$GIT_AUTHOR_EMAIL" = "donald.g.dunne@boeing.com" ]; then
	export GIT_AUTHOR_NAME="Donald G. Dunne"
fi

setNameEmail "$GIT_AUTHOR_NAME" "author"
setNameEmail "$GIT_COMMITTER_NAME" "comitter"