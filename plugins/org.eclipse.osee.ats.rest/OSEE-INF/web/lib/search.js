
function searchButton() {
	$ids = document.getElementById('searchId').value;
	if ($ids == null || $ids == "") {
		alert("Must enter search ids.");
	}else {
	   $url = "/ats/ui/action/" + $ids;
	   window.open($url);
    }
}

var button = document.getElementById("searchButton");
button.addEventListener("click", searchButton);

function checkForReturn(e) {
    var key = e.keyCode;
    if (key == 13) { // 13 is enter
      searchButton();
    }
};
