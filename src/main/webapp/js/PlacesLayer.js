var placesIcon = function (cluster) {
    return new tomtom.L.divIcon({
        iconUrl: "img/reports.png",
        iconSize: [45, 46],
        html: '<div style="margin-top: 15px;color: white;"><span>'
        + cluster.getChildCount() + '</span></div>',
        className: 'custom-text-icon'
    });
}

var placeIcon = new tomtom.L.icon({
    iconUrl: "img/report.png",
    iconSize: [34, 34]
});

var markerGroup = tomtom.L.markerClusterGroup({
    iconCreateFunction: function (cluster) {
        return placesIcon(cluster);
    }
});
//var tourSelection = [];
var addToTour = function (lat, lon) {
    locations.push({
        lat: parseFloat(lat),
        lon: parseFloat(lon)
    });
    //alert(locations);
// 	if(locations.length > 1){
//
// 	}
}

var listenWikiText = function (wikitext) {
    var msg = new SpeechSynthesisUtterance(wikitext);
    window.speechSynthesis.speak(msg);
}

var createPopup = function (feature) {
    //alert(JSON.stringify(feature));
    var popup = "<table>	<tr><td><b>Name</b></td><td>" +
        feature.properties.name +
        "</td></tr>    " +
        "	<tr><td><b>Phone Number</b></td><td>" +
        feature.properties.phone +
        "</td></tr>   	" +
        "	<tr><td><b>Rating</b></td><td>" +
        getRating(feature.properties.rating) +
        "</td></tr>" +
        "     	<tr><td><b>Open Now</b></td><td>" +
        feature.properties.open_now +
        "</td></tr>" +
        " 	<tr><td colspan =2 ><img style='width:200px;height:200px;' src=\"" +
        feature.properties.photos +
        "\"</img></td></tr>" +
        "<tr><td> <button type='button' onclick='addToTour(" + feature.properties.entryPoints[0] + "," + feature.properties.entryPoints[1] + ");'>Add to Tour</button>" +
        //"<tr><td> <button type='button' onclick='addToTour("+52.056999+","+4.902938+");'>Add to Tour!</button>" +

        "</td>" +
        "<td> <button type='button' class='btn btn-info' onclick=\"listenWikiText('" + feature.properties.wikitext + "');\">Listen Wiki</button>" +
        "</td></tr>" +
        "    </table>";
    return popup;
}

var getRating = function (rating) {
    if (rating) {
        var starRating = '<span class="fa fa-star star-checked"></span>';
        var result = '';
        for (var i = 1; i <= parseInt(rating); i++) {
            result = result + starRating;
        }
        return result;
    }
    return 'NA';
}
var plotPlaces = function (geoJson) {
    var geoJsonLayer = tomtom.L.geoJSON(geoJson, {
        pointToLayer: function (feature, latlng) {
            return L.marker(latlng, {
                icon: placeIcon
            });
        },
        onEachFeature: function (feature, layer) {
            var popupText = createPopup(feature);
            layer.bindPopup(popupText);
        }

    });

    markerGroup.addLayer(geoJsonLayer);
    map.addLayer(markerGroup);
}

