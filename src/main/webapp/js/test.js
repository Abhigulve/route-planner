function getSelectedCategory() {
	var form = document.getElementById('form');
	var inputs = form.querySelectorAll('input[name=category]:checked');
	var selectedCats = [];
	for(var i=0; i<inputs.length;i++){
		selectedCats[i] = inputs[i].value;
	}
	return selectedCats.join();
};

function getPlacesForSelectedCity(lat, lon){
	var selectedCats = getSelectedCategory();
	return performAjaxCall(lat,lon,selectedCats)
}

function performAjaxCall(lat,lon,selectedCats) {
	return $.ajax({
		async: false,
		dataType: "json",
		url:"http://localhost:8080/getpoi/"+selectedCats+"?&radius=10000&limit=25&lat="+lat+"&lon="+lon,
//		url : "https://api.tomtom.com/search/2/poiSearch/hotel.json?key=aIbT4MtEJDnC5GisO79KzEOjSuPB6pXI&language=en-GB&radius=10000&limit=25&lat="+ lat +"&lon=" + lon,
		success : function(ajaxResult) {
			return ajaxResult;
		}
	});
}


