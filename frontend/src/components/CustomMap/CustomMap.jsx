import React, { useState, useEffect, useRef } from "react";
import { Map, MapMarker, MarkerClusterer } from "react-kakao-maps-sdk";
import { useUserLocation } from "src/hooks/userLocation";
import { KakaoMapService } from 'src/services/KakaoMapService';

import MarkerModal from "./MarkerModal";
import SkeletonKakaoMap from "./SkeletonKakaoMap";
import "./CustomMap.css";

function CustomMap() {
  const { location, errorMessage } = useUserLocation();
  const [selectedMarker, setSelectedMarker] = useState(null);
  const [mapCenter, setMapCenter] = useState(null);
  const [mapLevel, setMapLevel] = useState(5); // 현재 줌 레벨 상태
  const [locations, setLocations] = useState([]); // API에서 받아온 데이터 저장
  const mapRef = useRef(null); // Map 컴포넌트 ref
  const debounceTimer = useRef(null); // 디바운스 타이머
  const [forceUpdate, setForceUpdate] = useState(0); // 강제 리렌더링을 위한 상태
  const [isMapLoaded, setIsMapLoaded] = useState(false); // 지도 로딩 상태 추가
  const initialLoadDone = useRef(false); // 초기 데이터 로드 여부 추적
  const currentLevelRef = useRef(5); // 현재 맵 레벨을 ref로 추적
  const [filterType, setFilterType] = useState("FAVORITE"); // 필터 타입 상태 추가

  // 최초 location 업데이트 시 mapCenter를 설정합니다.
  useEffect(() => {
    if (location && !mapCenter) {
      setMapCenter(location);
    }
  }, [location, mapCenter]);

  // 첫 로드에만 데이터 불러오기
  useEffect(() => {
    if (mapCenter && isMapLoaded && mapRef.current && !initialLoadDone.current) {
      handleSearch();
      initialLoadDone.current = true; // 초기 로드 완료 표시
    }
  }, [mapCenter, isMapLoaded]);

  // 지도의 중심 변경 시 디바운싱을 적용하여 상태 업데이트 (위치 추적만)
  const handleCenterChanged = (map) => {
    if (debounceTimer.current) {
      clearTimeout(debounceTimer.current);
    }
    debounceTimer.current = setTimeout(() => {
      const center = map.getCenter();
      setMapCenter({
        latitude: center.getLat(),
        longitude: center.getLng(),
      });
    }, 300);
  };

  // 지도의 줌 레벨 변경 시 현재 레벨 저장
  const handleZoomChanged = (map) => {
    const level = map.getLevel();
    currentLevelRef.current = level;
    setMapLevel(level);
  };

  // 내 위치 버튼 클릭 시 현재 위치로 이동하고 줌 레벨 5로 설정
  const moveToMyLocation = () => {
    if (!location) return;
    
    if (mapRef.current && window.kakao && window.kakao.maps) {
      const newCenter = new window.kakao.maps.LatLng(location.latitude, location.longitude);
      mapRef.current.setCenter(newCenter);
      mapRef.current.setLevel(5);
      currentLevelRef.current = 5;
      setMapLevel(5);
    } else {
      console.warn("Kakao Maps API가 로드되지 않았습니다.");
    }
    setMapCenter(location);
  };

  // 필터 타입 변경 핸들러
  const handleFilterChange = (e) => {
    setFilterType(e.target.value);
  };

  // 현재 지도 영역에 있는 데이터 검색 (버튼 클릭 또는 초기 로드시에만 실행)
  const handleSearch = async () => {
    if (mapRef.current && window.kakao && window.kakao.maps) {
      // 현재 맵 레벨 저장
      const currentLevel = mapRef.current.getLevel();
      currentLevelRef.current = currentLevel;
      setMapLevel(currentLevel);
      
      const bounds = mapRef.current.getBounds();
      const northEast = bounds.getNorthEast();
      const southWest = bounds.getSouthWest();
  
      const bottomLeft = { 
        lat: southWest.getLat(), 
        lng: southWest.getLng() 
      };
      const topRight = { 
        lat: northEast.getLat(), 
        lng: northEast.getLng() 
      };
  
      try {
        const response = await KakaoMapService.getLocationInfo(filterType, bottomLeft, topRight);
        // 응답에 데이터가 있는지 확인하고 설정
        if (response && response.data && Array.isArray(response.data.data)) {
          setLocations(response.data.data);
        } else {
          console.warn("API에서 위치 데이터를 받지 못했습니다.");
          setLocations([]);
        }
        
        // 마커가 즉시 표시되도록 강제 리렌더링 트리거
        setForceUpdate(prev => prev + 1);
      } catch (error) {
        console.error("API 호출 중 오류 발생:", error);
        setLocations([]);
      }
    }
  };

  if (errorMessage) {
    return <span className="error-icon">⚠️{errorMessage}</span>;
  }

  if (!mapCenter) {
    return <SkeletonKakaoMap />;
  }
  
  return (
    <div className="map-container">
      <div className="filter-container">
        <select 
          value={filterType} 
          onChange={handleFilterChange}
          className="filter-select"
        >
          <option value="FAVORITE">관심사</option>
          <option value="ALL">전체</option>
        </select>
      </div>
      <Map
        className="map-wrap"
        center={{ lat: mapCenter.latitude, lng: mapCenter.longitude }}
        level={currentLevelRef.current} // ref에서 현재 레벨 사용
        onCenterChanged={handleCenterChanged}
        onZoomChanged={handleZoomChanged} // 줌 변경 이벤트 핸들러 추가
        onCreate={(map) => {
          mapRef.current = map;
          setIsMapLoaded(true);
        }}
        ref={mapRef}
        key={`map-${forceUpdate}`} // forceUpdate가 변경될 때 Map 컴포넌트를 리렌더링
      >
        {/* 내 현재 위치 마커 */}
        {location && (
          <MapMarker
            position={{ lat: location.latitude, lng: location.longitude }}
            title="내 위치"
            image={{
              src: `data:image/svg+xml;utf8,${encodeURIComponent(
                `<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 32 32">
                  <!-- 중앙 빨간색 원 -->
                  <circle cx="16" cy="16" r="8" fill="red" />
                  <!-- 바깥 빨간 테두리 -->
                  <circle cx="16" cy="16" r="12" fill="none" stroke="red" strokeWidth="2" />
                </svg>`
              )}`,
              size: { width: 16, height: 16 }
            }}
          />
        )}
        
        {/* 위치 데이터 마커 */}
        <MarkerClusterer 
          averageCenter={true} 
          minLevel={7}
          key={`cluster-${forceUpdate}`} // forceUpdate가 변경될 때 MarkerClusterer도 리렌더링
        >
          {locations.map((loc, index) => (
            <MapMarker
              key={loc.id ? `${loc.id}-${index}` : `marker-${index}`}
              position={{ lat: loc.lat, lng: loc.lng }}
              title={loc.name}
              onClick={() => setSelectedMarker(loc)}
            />
          ))}
        </MarkerClusterer>
        
        {/* 내 위치 이동 버튼 */}
        <div className="btn-map-wrap">
          <button onClick={handleSearch}>
            현 위치 검색
          </button>
          <button className="btn-location" onClick={moveToMyLocation}>
            <svg width="16" height="16" viewBox="0 0 64 64" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="20" fill="none" stroke="#000" strokeWidth="6"/>
              <circle cx="32" cy="32" r="10" fill="#000"/>
              <circle cx="32" cy="32" r="4" fill="#fff"/>
              <path fill="none" stroke="#000" strokeWidth="6" d="M32 2v10M32 52v10M2 32h10M52 32h10"/>
            </svg>
          </button>
        </div>
        {selectedMarker && <MarkerModal mark={selectedMarker} onClose={() => setSelectedMarker(null)} />}
      </Map>
    </div>
  );
}

export default CustomMap;