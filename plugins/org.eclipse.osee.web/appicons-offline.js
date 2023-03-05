const https = require('https');
const fs = require('fs');
const path = require('path');
const { create } = require('domain');
const { run } = require('node:test');

var endValue = '';
var count = 0;
var countName = 0;
var newDir = './src/assets/iconassets';
const fileToRemove =
	'../org.eclipse.osee.web/src/assets/iconassets/original-material-icons.css';

const urlAllCss =
	'https://fonts.googleapis.com/css?family=Material+Icons' +
	'|Material+Icons+Outlined|Material+Icons+Round' +
	'|Material+Icons+Two+Tone|Material+Icons+Sharp';

const myArrayUrlLinks = [];
const myArrayName = [
	'material-icons-filed.woff2',
	'material-icons-outlined.woff2',
	'material-icons-round.woff2',
	'material-icons-sharp.woff2',
	'material-icons-twotone.woff2',
];

const endValueFiled =
	'src:url("../../assets/iconassets/material-icons-filed.woff2") format("woff2")';
const endValueOutlined =
	'src:url("../../assets/iconassets/material-icons-outlined.woff2") format("woff2")';
const endValueRound =
	'src:url("../../assets/iconassets/material-icons-round.woff2") format("woff2")';
const endValueSharp =
	'src:url("../../assets/iconassets/material-icons-sharp.woff2") format("woff2")';
const endValueTwoTone =
	'src:url("../../assets/iconassets/material-icons-twotone.woff2") format("woff2")';

const myArrayLocalPath = [
	endValueFiled,
	endValueOutlined,
	endValueRound,
	endValueSharp,
	endValueTwoTone,
];

const myMap = new Map();

function createMap(myArrayUrlLinks, myArrayName) {
	for (let i = 0; i < myArrayUrlLinks.length; i++) {
		myMap.set(myArrayUrlLinks[i], myArrayName[i]);
	}
	return myMap;
}

function createFolder() {
	if (!fs.existsSync(newDir)) {
		console.log(
			'Folder iconassets does not exist yet. Creating this folder.'
		);
		fs.mkdirSync(newDir, { recursive: true });
		console.log('Created the iconassets folder');
	} else {
		console.log('The iconassets folder already exists.');
	}
}

function downloadIconFiles(url, filename) {
	const req = https.get(url, function (res) {
		const storeToFolder =
			'../org.eclipse.osee.web/src/assets/iconassets/' + filename;
		const fileStream = fs.createWriteStream(storeToFolder);
		res.pipe(fileStream);

		fileStream.on('error', function (err) {
			console.log('Error writing to the stream');
			console.log(err);
		});

		fileStream.on('finish', function () {
			fileStream.close();
			console.log('Done downloading a file: ' + myArrayName[countName]);
			countName = countName + 1;
		});
	});

	req.on('error', function (err) {
		console.log('Error downloading the file.');
		console.log(err);
	});
}

function downloadCssFile(url) {
	const req = https.get(url, function (res) {
		const storeToFolder =
			'../org.eclipse.osee.web/src/assets/iconassets/original-material-icons.css';
		const fileStream = fs.createWriteStream(storeToFolder);
		res.pipe(fileStream);

		fileStream.on('error', function (err) {
			console.log('Error writing to the stream');
			console.log(err);
		});

		fileStream.on('finish', function () {
			fileStream.close();
			console.log('Done downloading original css file.');
			updateIconassetsFolder();
		});
	});

	req.on('error', function (err) {
		console.log('Error downloading the file.');
		console.log(err);
	});
}

function runLoop() {
	for (const [myArrayUrlLinks, myArrayName] of myMap) {
		downloadIconFiles(myArrayUrlLinks, myArrayName);
	}
}

function urlForFiles(myLine) {
	const myLineHelper = myLine.substring(
		myLine.indexOf('h'),
		myLine.indexOf(')')
	);
	myArrayUrlLinks.push(myLineHelper);
}

function updateIconassetsFolder() {
	const lineReader = require('readline').createInterface({
		input: require('fs').createReadStream(
			'../org.eclipse.osee.web/src/assets/iconassets/original-material-icons.css'
		),
	});

	try {
		lineReader.on('line', (line) => {
			if (line.includes('src')) {
				endValue = myArrayLocalPath[count];
				urlForFiles(line);
				line = line.replace(line, endValue);
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					'\t'
				);
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					'\t'
				);
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					line
				);
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					'\r'
				);
				count = count + 1;
			} else {
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					line
				);
				fs.appendFileSync(
					'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
					'\r'
				);
			}
		});
	} catch (err) {
		console.log('Error reading from the original .css-file.');
		console.log(err);
	}

	try {
		lineReader.on('close', () => {
			fs.appendFileSync(
				'../org.eclipse.osee.web/src/assets/iconassets/material-icons.css',
				'\n'
			);
			removeFile();
			createMap(myArrayUrlLinks, myArrayName);
			runLoop();
		});
	} catch (err) {
		console.log('Error writing to the new .css-file.');
		console.log(err);
	}
}

function removeFile() {
	console.log(
		'Finished creating a new css-file with the local path.' +
			'Removing the original css-file.'
	);
	try {
		fs.unlinkSync(fileToRemove);
	} catch (err) {
		console.log('Error in removing file.');
	}
}

createFolder();
downloadCssFile(urlAllCss);
