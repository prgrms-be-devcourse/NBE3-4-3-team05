import React from "react";
import CustomMap from "src/components/CustomMap/CustomMap";
import SimpleKakaoMap from "src/components/CustomMap/SimpleKakaoMap";

const KakaoMap = ({ lat, lng, place, height }) => {
    // 좌표가 제공된 경우 심플 지도 컴포넌트 사용
    if (lat && lng) {
        return (
            <SimpleKakaoMap
                lat={lat}
                lng={lng}
                place={place}
                height={height || '250px'}
            />
        );
    }

    // 그렇지 않으면 원래 CustomMap 사용
    return (
        <div>
            <CustomMap />
        </div>
    );
};

export default KakaoMap;
