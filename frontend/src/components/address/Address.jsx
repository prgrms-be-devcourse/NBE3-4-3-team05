import './Address.css';
import React, { useEffect, useState } from 'react';

const Address = ({ onAddressSelect }) => {
    const [postcode, setPostcode] = useState("");
    const [address, setAddress] = useState("");
    const [detailAddress, setDetailAddress] = useState("");
    const [extraAddress, setExtraAddress] = useState("");
    const [lat, setLat] = useState("");
    const [lng, setLng] = useState("");

    useEffect(() => {
        const script = document.createElement('script');
        script.src = '//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
        script.async = true;
        document.body.appendChild(script);
    }, []);

    const handleAddressSearch = () => {
        new window.daum.Postcode({
            oncomplete: function (data) {
                let addr = '';
                let extraAddr = '';

                if (data.userSelectedType === 'R') {
                    addr = data.roadAddress;
                } else {
                    addr = data.jibunAddress;
                }

                if (data.userSelectedType === 'R') {
                    if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                        extraAddr += data.bname;
                    }
                    if (data.buildingName !== '' && data.apartment === 'Y') {
                        extraAddr += extraAddr !== '' ? `, ${data.buildingName}` : data.buildingName;
                    }
                    if (extraAddr !== '') {
                        extraAddr = ` (${extraAddr})`;
                    }
                }
                setPostcode(data.zonecode);
                setAddress(addr);
                setExtraAddress(extraAddr);

                // 카카오맵 API를 사용하여 주소의 위도, 경도 가져오기
                const geocoder = new window.kakao.maps.services.Geocoder();
                geocoder.addressSearch(addr, function (result, status) {
                    if (status === window.kakao.maps.services.Status.OK) {
                        const newLat = result[0].y;
                        const newLng = result[0].x;
                        setLat(newLat);
                        setLng(newLng);
                        onAddressSelect({
                            postcode: data.zonecode,
                            address: addr,
                            extraAddress: extraAddr,
                            detailAddress: detailAddress,
                            lat: newLat,
                            lng: newLng,
                        });
                    } else {
                        alert("위도/경도 정보를 가져올 수 없습니다.");
                    }
                });
            },
        }).open();
    };

    // 상세주소 변경 시 부모 컴포넌트에 업데이트
    const handleDetailAddressChange = (e) => {
        const newDetail = e.target.value;
        setDetailAddress(newDetail);
        onAddressSelect({
            postcode,
            address,
            extraAddress,
            detailAddress: newDetail,
            lat,
            lng,
        });
    };

    return (
        <div className='addressSearch'>
            <input 
                type='text' 
                placeholder='우편번호' 
                value={postcode} 
                readOnly
            />
            <button className='custom-button' type='button' onClick={handleAddressSearch}>
                우편번호 찾기
            </button>
            <br />
            <input 
                type='text' 
                placeholder='주소' 
                value={address} 
                readOnly 
            />
            <br />
            <input 
                type='text' 
                placeholder='상세주소'
                value={detailAddress}
                onChange={handleDetailAddressChange}
            />
            <input 
                type='text' 
                placeholder='참고항목' 
                value={extraAddress} 
                readOnly 
            />
        </div>
    );
};

export default Address;