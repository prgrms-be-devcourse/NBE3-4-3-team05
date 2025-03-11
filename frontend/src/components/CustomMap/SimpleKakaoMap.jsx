import React, { useEffect, useRef } from 'react';

const SimpleKakaoMap = ({ lat, lng, place, height = '250px' }) => {
    const mapRef = useRef(null);
    const mapInstanceRef = useRef(null);

    useEffect(() => {
        // 좌표 유효성 검사
        if (!lat || !lng || isNaN(parseFloat(lat)) || isNaN(parseFloat(lng))) {
            console.error('Invalid coordinates:', { lat, lng });
            return;
        }

        const parsedLat = parseFloat(lat);
        const parsedLng = parseFloat(lng);

        const loadKakaoMap = () => {
            // 카카오맵 API가 로드되었는지 확인
            if (window.kakao && window.kakao.maps) {
                initializeMap();
            } else {
                console.warn("Kakao Maps API not loaded, cannot display map");
            }
        };

        const initializeMap = () => {
            if (!mapRef.current) return;

            try {
                const container = mapRef.current;
                const options = {
                    center: new window.kakao.maps.LatLng(parsedLat, parsedLng),
                    level: 3
                };

                // 지도 생성
                const map = new window.kakao.maps.Map(container, options);
                mapInstanceRef.current = map;

                // 마커 생성
                const marker = new window.kakao.maps.Marker({
                    position: new window.kakao.maps.LatLng(parsedLat, parsedLng),
                    map: map
                });

                // 인포윈도우 생성
                if (place) {
                    const infowindow = new window.kakao.maps.InfoWindow({
                        content: `<div style="padding:5px;font-size:12px;">${place}</div>`
                    });
                    infowindow.open(map, marker);
                }
            } catch (error) {
                console.error('Error initializing Kakao Map:', error);
            }
        };

        loadKakaoMap();

        // 컴포넌트 언마운트 시 정리
        return () => {
            mapInstanceRef.current = null;
        };
    }, [lat, lng, place]);

    return (
        <div
            ref={mapRef}
            style={{
                width: '100%',
                height: height,
                borderRadius: '8px',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
        />
    );
};

export default SimpleKakaoMap;