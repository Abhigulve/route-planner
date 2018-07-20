reportsIcon = function(cluster){
	return new tomtom.L.divIcon({
        iconUrl: "img/reports.png",
        iconSize: [45, 46],
        html:'<div style="margin-top: 15px;color: white;"><span>' + cluster.getChildCount() + '</span></div>',
        className: 'custom-text-icon'
    });
}

var reportIcon = new tomtom.L.icon({
    iconUrl: "img/report.png",
    iconSize: [34, 34]
});

var markerGroup = tomtom.L.markerClusterGroup({
	iconCreateFunction: function(cluster) {
			return reportsIcon(cluster);
	}
});
createPopup = function(feature){
	var popup = "<table><tr><th>TYPE</th><th>"+ feature.properties.Type +"</th></tr></table>";
	return popup;
}
var geoJsonLayer = tomtom.L.geoJSON(reportsGeoJson,{
	pointToLayer: function(feature, latlng) {
         return L.marker(latlng,{icon:reportIcon});
     },
	onEachFeature: function (feature, layer) {
		var popupText = createPopup(feature);
		layer.bindPopup(popupText);
	}
	
});

markerGroup.addLayer(geoJsonLayer);
map.addLayer(markerGroup);