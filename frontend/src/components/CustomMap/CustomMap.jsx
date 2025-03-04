import React, { useState } from "react";
import { Map, MapMarker, MarkerClusterer } from "react-kakao-maps-sdk";
import { useUserLocation } from "src/hooks/userLocation";

import MarkerModal from "./MarkerModal";
import SkeletonKakaoMap from "./SkeletonKakaoMap";
import "./CustomMap.css";

function CustomMap({ data }) {
  const { location, errorMessage } = useUserLocation();
  const [selectedMarker, setSelectedMarker] = useState(null);

  if (errorMessage) {
    return <span className="error-icon">⚠️{errorMessage}</span>;
  }

  if (!location) {
    return <SkeletonKakaoMap />;
  }

  return (
    <Map
      className="map-wrap"
      center={{ lat: location.latitude, lng: location.longitude }}
      level={3}
    >
      <MarkerClusterer averageCenter={true} minLevel={7}>
        {data.LOCATION.map((loc) => (
          <MapMarker
            key={loc.id}
            position={{ lat: loc.lat, lng: loc.lng }}
            title={loc.name}
            onClick={() => setSelectedMarker(loc)}
          />
        ))}
      </MarkerClusterer>
      {selectedMarker && <MarkerModal mark={selectedMarker} />}
    </Map>
  );
}

export default CustomMap;
