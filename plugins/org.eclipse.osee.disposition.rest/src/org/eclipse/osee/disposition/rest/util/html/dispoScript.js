<script>
    function getSetTable(setSelect) {
        var setSelect = document.getElementById("setSelect");
        var selectedSetIndex = setSelect.selectedIndex;
        var setId = setSelect.options[selectedSetIndex].value;

        var programSelect = document.getElementById("programSelect");
        var selectedProgramIndex = programSelect.selectedIndex;
        var programId = programSelect.options[selectedProgramIndex].value;

        var path = "http://localhost:8089/dispo/program/";
        var fullPath = path.concat(programId, "/set/", setId, "/item/");
        get(fullPath, setTableStatusHandler, setSelect);
    }
</script>
<script>
    function keyPress(event, selectedElement) {
        // look for window.event in case event isn't passed in
        if (typeof event == 'undefined' && window.event) {
            event = window.event;
        }
        if (event.keyCode == 13) {
            handleAnnotationEnter(selectedElement);
        }
    }
</script>
<script>
    function handleAnnotationEnter(selectedElement) {
        if (selectedElement.parentNode.id == null) {
            selectdElement.style.backgroundcolor = "red";
        } else {
            selectdElement.style.backgroundcolor = "blue";
        }
    }
</script>
<script>
    function getPrograms(programSelect) {
        if (programSelect.length < 2) {
            var path = "http://localhost:8089/dispo/program/";
            get(path, getAllProgramsStatusHandler, programSelect);
        }
    }
</script>
<script>
    function getProgramDetails(programSelect) {
        var selectedIndex = programSelect.selectedIndex;
        var programId = programSelect.options[selectedIndex].value;

        var path = "http://localhost:8089/dispo/program/";
        var fullPath = path.concat(programId, "/set/");
        get(fullPath, getProgramStatusHandler, programSelect);
    }
</script>
<script>
    function showAnnotations(x) {
        var currentRow = document.getElementById(x);
        var dispoTable = document.getElementById("dispoTable");
        var currentRowIndex = currentRow.rowIndex;
        var nextRow = dispoTable.rows[currentRowIndex + 1];

        if (nextRow.className == "containerRow") {
            if (nextRow.style.display == "none") {
                nextRow.style.display = "";
            } else {
                nextRow.style.display = "none";
            }
        } else {
            var itemId = currentRow.id;
            getSubTableHTML(currentRow, itemId);
        }
    }
</script>
<script>
    function expandSubTable(currentRowIndex, htmlToAdd) {
        var newRow = dispoTable.insertRow(currentRowIndex + 1);
        newRow.className = "containerRow";
        newRow.innerHTML = htmlToAdd;
    }
</script>
<script>
    function getSubTableHTML(currentRow, itemId) {
        var programSelect = document.getElementById("programSelect");
        var selectedProgramIndex = programSelect.selectedIndex;
        var programId = programSelect.options[selectedProgramIndex].value;

        var setSelect = document.getElementById("setSelect");
        var selectedSetIndex = setSelect.selectedIndex;
        var setId = setSelect.options[selectedSetIndex].value;

        var itemId = currentRow.id;
        var currentRowIndex = currentRow.rowIndex;

        var path = "http://localhost:8089/dispo/program/";
        var fullPath = path.concat(programId, "/set/", setId, "/item/", itemId, "/annotation/");

        get(fullPath, subTableStatusHandler, currentRowIndex);
    }
</script>
<script>
    function setTableStatusHandler(setSelectEl) {
        return function() {
            if (this.readyState == 4) {
                var responseHTML = this.responseText;
                var dispoTableEl = document.getElementById("dispoTableBody");
                dispoTableEl.innerHTML = responseHTML;
            }
        }
    }
</script>
<script>
    function subTableStatusHandler(currentRowIndex) {
        return function() {
            if (this.readyState == 4) {
                var responseHTML = this.responseText;
                expandSubTable(currentRowIndex, responseHTML);
            }
        }
    }
</script>
<script>
    function getAllProgramsStatusHandler(programSelectEl) {
        return function() {
            if (this.readyState == 4) {
                var responseHTML = this.responseText;
                programSelectEl.innerHTML = responseHTML;
            }
        }
    }
</script>
<script>
    function getProgramStatusHandler(setSelectEl) {
        return function() {
            if (this.readyState == 4) {
                var responseHTML = this.responseText;
                var setSelectEl1 = document.getElementById("setSelect");
                var setSelectEl2 = document.getElementById("setSelect2");
                setSelectEl1.innerHTML = responseHTML;
                setSelectEl2.innerHTML = responseHTML;
            }
        }
    }
</script>
<script>
    function get(url, statusHandler, callbackParam) {
        httpRequest("GET", url, statusHandler, callbackParam);
    }
</script>
<script>
    function httpRequest(httpVerb, url, statusHandler, callbackParam) {
        var httpRequest = new XMLHttpRequest()
        httpRequest.prevDataLength = 0;
        httpRequest.onreadystatechange = statusHandler(callbackParam);
        httpRequest.open(httpVerb, url);
        httpRequest.send();
    }
</script>