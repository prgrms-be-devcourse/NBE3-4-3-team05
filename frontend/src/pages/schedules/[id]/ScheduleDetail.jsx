import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ScheduleService } from '../../../services/SheduleService';
import Alert from '../../../components/alert/Alert';
import DateTimeInput from '../../../components/dateTimeInput/DateTimeInput';
import Address from "../../../components/address/Address";
import './ScheduleDetail.css';
import KakaoMap from "../../kakaoMap/KakaoMap";

const ScheduleDetail = () => {
    const { scheduleId, classId } = useParams();
    const navigate = useNavigate();
    const [schedule, setSchedule] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [meetingTitle, setMeetingTitle] = useState('');
    const [meetingTime, setMeetingTime] = useState('');
    const [addressInfo, setAddressInfo] = useState({
        address: '',
        detailAddress: '',
        extraAddress: '',
        postcode: '',
        lat: '',
        lng: ''
    });
    const [detailAddressInput, setDetailAddressInput] = useState('');
    const [addressSearchMode, setAddressSearchMode] = useState(true);
    const [initialValuesSet, setInitialValuesSet] = useState(false);

    // 컴포넌트 마운트 시와 schedule 데이터 로드 시 초기값 설정
    useEffect(() => {
        if (schedule) {
            setMeetingTitle(schedule.meetingTitle || '');

            // 명시적으로 schedule.meetingTime을 설정
            if (schedule.meetingTime) {
                setMeetingTime(schedule.meetingTime);
            }

            setInitialValuesSet(true);
        }
    }, [schedule]);

    const fetchScheduleDetail = async () => {
        try {
            const response = await ScheduleService.getScheduleDetail(scheduleId, classId);
            const detailData = response.data?.data;

            if (detailData) {
                setSchedule(detailData);
                setMeetingTitle(detailData.meetingTitle || '');
                setMeetingTime(detailData.meetingTime || '');

                // 주소 정보 설정
                const mainAddress = detailData.meetingPlace ? detailData.meetingPlace.split(' (')[0] : '';
                const extraAddressPart = detailData.meetingPlace && detailData.meetingPlace.includes('(') ?
                    `(${detailData.meetingPlace.split('(')[1]}` : '';

                // 좌표 정보 변환 - 명시적으로 파싱
                const latValue = detailData.lat ? parseFloat(detailData.lat) : '';
                const lngValue = detailData.lng ? parseFloat(detailData.lng) : '';

                setAddressInfo({
                    address: mainAddress,
                    detailAddress: '',
                    extraAddress: extraAddressPart,
                    postcode: '',
                    lat: latValue,
                    lng: lngValue
                });
            } else {
                console.error('일정 데이터가 없습니다:', response);
                Alert('일정 정보를 찾을 수 없습니다.', '', '', () => navigate(`/classes/${classId}`));
            }
        } catch (error) {
            console.error('일정 상세 조회 오류:', error);
            Alert(error.response?.data?.message || '일정 상세 조회에 실패했습니다.', '', '', () => navigate(`/classes/${classId}`));
        }
    };

    const handleMeetingTimeChange = useCallback((formattedDateTime) => {
        setMeetingTime(formattedDateTime);
    }, []);

    // 주소 선택 핸들러
    const handleAddressSelect = (data) => {
        setAddressInfo(data);
    };

    const handleBackButton = () => {
        // 변경 사항이 있는 경우 확인
        if (isEditing && hasChanges()) {
            if (window.confirm('변경 사항이 있습니다. 취소하시겠습니까?')) {
                resetFormToOriginal();
                setIsEditing(false);
            }
            return;
        }

        // 수정 모드가 아닐 때만 클래스 페이지로 이동
        navigate(`/classes/${classId}`);
    };

    // 변경 사항이 있는지 확인하는 함수
    const hasChanges = () => {
        if (!schedule) return false;

        const titleChanged = meetingTitle !== (schedule.meetingTitle || '');
        const timeChanged = meetingTime !== (schedule.meetingTime || '');
        const addressChanged = addressInfo.address !== '' &&
            addressInfo.address !== (schedule.meetingPlace ? schedule.meetingPlace.split(' (')[0] : '');
        const detailChanged = detailAddressInput !== '';

        return titleChanged || timeChanged || addressChanged || detailChanged;
    };

    // 폼을 원래 값으로 리셋
    const resetFormToOriginal = () => {
        if (!schedule) return;

        setMeetingTitle(schedule.meetingTitle || '');
        setMeetingTime(schedule.meetingTime || '');
        setDetailAddressInput('');
        setAddressSearchMode(true);

        const mainAddress = schedule.meetingPlace ? schedule.meetingPlace.split(' (')[0] : '';
        const extraAddressPart = schedule.meetingPlace && schedule.meetingPlace.includes('(') ?
            `(${schedule.meetingPlace.split('(')[1]}` : '';

        setAddressInfo({
            address: '',
            detailAddress: '',
            extraAddress: extraAddressPart,
            postcode: '',
            lat: schedule.lat || '',
            lng: schedule.lng || ''
        });
    };

    // 편집 모드 토글 핸들러
    const toggleEditMode = () => {
        if (isEditing) {
            if (hasChanges()) {
                if (window.confirm('변경 사항이 있습니다. 취소하시겠습니까?')) {
                    resetFormToOriginal();
                    setIsEditing(false);
                }
            } else {
                setIsEditing(false);
            }
        } else {
            setIsEditing(true);
            resetFormToOriginal();
        }
    };

    // 상세주소 처리 개선을 위한 함수 추가
    const extractAddressParts = (fullAddress) => {
        // 기본 결과 객체
        const result = {
            mainAddress: '',
            detailAddress: '',
            extraAddress: ''
        };

        if (!fullAddress) return result;

        // 참고항목 분리 (괄호로 시작하는 부분)
        let mainPart = fullAddress;
        if (fullAddress.includes('(')) {
            const parts = fullAddress.split(' (');
            mainPart = parts[0].trim();
            if (parts.length > 1) {
                result.extraAddress = `(${parts[1]}`;
            }
        }

        // 주소 패턴 분석
        const words = mainPart.split(' ');

        // 1. 도로명 주소 패턴 찾기 ("로", "길" 다음에 오는 숫자)
        let mainAddressEndIndex = -1;

        for (let i = 0; i < words.length; i++) {
            if (i > 0 &&
                (words[i-1].endsWith('로') || words[i-1].endsWith('길')) &&
                /^\d+$/.test(words[i])) {
                mainAddressEndIndex = i;
                break;
            }
        }

        // 2. 지하철역이나 지하 번호 패턴 찾기
        if (mainAddressEndIndex === -1) {
            for (let i = 0; i < words.length; i++) {
                // 숫자 뒤에 "번"이 있거나, "지하" 뒤에 숫자가 있는 패턴 찾기
                if (/^\d+$/.test(words[i]) ||
                    (i > 0 && words[i-1] === '지하' && /^\d+$/.test(words[i])) ||
                    words[i].includes('지하')) {
                    mainAddressEndIndex = i;
                }
            }
        }

        // 도로명 주소 패턴을 찾았다면
        if (mainAddressEndIndex >= 0) {
            result.mainAddress = words.slice(0, mainAddressEndIndex + 1).join(' ');
            result.detailAddress = words.slice(mainAddressEndIndex + 1).join(' ');
        } else {
            // 도로명 주소 패턴을 찾지 못했을 경우 대체 로직
            // 가능하면 메인 주소의 마지막 부분이 숫자인지 확인
            let lastNumberIndex = -1;
            for (let i = 0; i < words.length; i++) {
                if (/\d/.test(words[i])) {
                    lastNumberIndex = i;
                }
            }

            if (lastNumberIndex >= 0) {
                result.mainAddress = words.slice(0, lastNumberIndex + 1).join(' ');
                result.detailAddress = words.slice(lastNumberIndex + 1).join(' ');
            } else {
                // 숫자를 찾지 못한 경우, 기본 분리 (60% 지점)
                const splitPoint = Math.floor(words.length * 0.6);
                result.mainAddress = words.slice(0, splitPoint).join(' ');
                result.detailAddress = words.slice(splitPoint).join(' ');
            }
        }

        return result;
    };

    const handleEditSubmit = async () => {
        if (!meetingTitle.trim()) {
            Alert("일정 제목을 입력해주세요.");
            return;
        }

        if (!meetingTime) {
            Alert("날짜를 선택해주세요.");
            return;
        }

        // 변경된 내용이 없는 경우 체크
        if (!hasChanges()) {
            Alert("변경된 내용이 없습니다.");
            return;
        }

        // 주소 정보 생성
        let meetingPlace, finalLat, finalLng;

        // 새 주소 검색 모드인지 확인
        if (addressSearchMode && addressInfo.address) {
            // 새로 검색한 주소 사용
            meetingPlace = addressInfo.address;

            // Address 컴포넌트의 상세주소 사용
            if (addressInfo.detailAddress) {
                meetingPlace += ` ${addressInfo.detailAddress}`;
            }

            // 참고항목이 있으면 추가
            if (addressInfo.extraAddress) {
                meetingPlace += ` ${addressInfo.extraAddress}`;
            }

            finalLat = parseFloat(addressInfo.lat);
            finalLng = parseFloat(addressInfo.lng);
        } else if (!addressSearchMode && detailAddressInput) {
            // 상세주소만 변경 모드

            // 기존 주소에서 각 부분 분리
            const addressParts = extractAddressParts(schedule.meetingPlace);

            // 새 주소 구성: 메인 주소 + 새 상세주소(입력값으로 완전 교체) + 참고항목
            meetingPlace = addressParts.mainAddress;

            // 새 상세주소 추가 (기존 상세주소를 완전히 대체)
            if (detailAddressInput) {
                meetingPlace += ` ${detailAddressInput}`;
            }

            // 참고항목 추가
            if (addressParts.extraAddress) {
                meetingPlace += ` ${addressParts.extraAddress}`;
            }

            // 기존 좌표 사용
            finalLat = parseFloat(schedule.lat);
            finalLng = parseFloat(schedule.lng);
        } else {
            // 아무 주소 변경 없음
            meetingPlace = schedule.meetingPlace;
            finalLat = parseFloat(schedule.lat);
            finalLng = parseFloat(schedule.lng);
        }

        // 유효한 좌표인지 확인
        if (isNaN(finalLat) || isNaN(finalLng)) {
            Alert("유효한 위치 정보가 필요합니다. 주소를 검색해주세요.");
            return;
        }

        // 변경된 내용이 있는지 확인
        const isTitleChanged = meetingTitle.trim() !== (schedule.meetingTitle || '').trim();
        const isTimeChanged = meetingTime !== (schedule.meetingTime || '');
        const isPlaceChanged = meetingPlace.trim() !== (schedule.meetingPlace || '').trim();

        if (!isTitleChanged && !isTimeChanged && !isPlaceChanged) {
            Alert("변경된 내용이 없습니다.");
            return;
        }

        const body = {
            meetingTitle: meetingTitle.trim(),
            meetingTime,
            meetingPlace: meetingPlace.trim(),
            lat: finalLat,
            lng: finalLng
        };

        try {
            // scheduleId와 classId를 함께 전달
            const response = await ScheduleService.putSchedulesLists(scheduleId, classId, body);
            if (response.status === 200) {
                Alert('일정이 수정되었습니다.', '', '', () => {
                    setIsEditing(false);
                    setDetailAddressInput('');
                    fetchScheduleDetail(); // 수정된 데이터 다시 불러오기
                });
            }
        } catch (error) {
            console.error('일정 수정 오류:', error);
            if (error.response?.data?.code === 4005) {
                Alert("과거 날짜는 설정할 수 없습니다.");
            } else if (error.response?.status === 403) {
                Alert("모임장만 일정을 수정할 수 있습니다.", "", "", () => {
                    setIsEditing(false);
                    fetchScheduleDetail();
                });
            } else {
                Alert(error.response?.data?.message || '일정 수정에 실패했습니다.');
            }
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('정말로 이 일정을 삭제하시겠습니까?')) {
            return;
        }

        try {
            const response = await ScheduleService.deleteSchedulesLists(scheduleId, classId);
            if (response.status === 200) {
                Alert('일정이 삭제되었습니다.', '', '', () => navigate(`/classes/${classId}`));
            }
        } catch (error) {
            console.error('일정 삭제 오류:', error);
            if (error.response?.status === 403) {
                Alert("모임장만 일정을 삭제할 수 있습니다.");
            } else {
                Alert(error.response?.data?.message || '일정 삭제에 실패했습니다.');
            }
        }
    };

    useEffect(() => {
        fetchScheduleDetail();
    }, [scheduleId, classId]);

    // 데이터가 없는 경우 로딩 UI 표시
    if (!schedule) {
        return <div className="loading">로딩 중...</div>;
    }

    return (
        <div className="schedule-detail-container">
            <div className="schedule-detail-header">
                <h2>일정 상세 정보</h2>
                <div className="button-group">
                    {!isEditing && (
                        <button className="custom-button" onClick={handleBackButton}>
                            뒤로 가기
                        </button>
                    )}
                    <button className="custom-button" onClick={toggleEditMode}>
                        {isEditing ? '수정 취소' : '수정'}
                    </button>
                    <button className="custom-button warning" onClick={handleDelete}>
                        삭제
                    </button>
                </div>
            </div>

            <div className="schedule-detail-content">
                {isEditing ? (
                    <div className="edit-form">
                        <div className="form-group">
                            <label>일정 제목:</label>
                            <input
                                type="text"
                                value={meetingTitle}
                                onChange={(e) => setMeetingTitle(e.target.value)}
                                placeholder="일정 제목을 입력하세요"
                            />
                        </div>
                        <div className="form-group">
                            <label>일정 시간:</label>
                            {initialValuesSet && (
                                <DateTimeInput
                                    key={`date-input-${schedule.meetingTime || Date.now()}`} // 고유 키 생성
                                    onMeetingTimeChange={handleMeetingTimeChange}
                                    initialDateTime={schedule.meetingTime}
                                />
                            )}
                        </div>
                        <div className="form-group">
                            <label>모임 장소:</label>
                            <p className="current-address">현재 주소: {schedule.meetingPlace}</p>

                            {/* 주소 검색 */}
                            <div className="address-options">
                                <button
                                    className={`custom-button ${addressSearchMode ? 'active' : ''}`}
                                    type="button"
                                    onClick={() => setAddressSearchMode(true)}
                                >
                                    새 주소 검색하기
                                </button>
                                <button
                                    className={`custom-button ${!addressSearchMode ? 'active' : ''}`}
                                    type="button"
                                    onClick={() => setAddressSearchMode(false)}
                                >
                                    상세주소만 변경하기
                                </button>
                            </div>

                            {addressSearchMode ? (
                                // 새 주소 검색 모드
                                <div className="address-search-section">
                                    <Address
                                        onAddressSelect={handleAddressSelect}
                                        initialAddress=""
                                        initialLat=""
                                        initialLng=""
                                    />
                                </div>
                            ) : (
                                // 상세주소만 변경 모드
                                <div className="detail-address-only">
                                    <input
                                        type="text"
                                        value={detailAddressInput}
                                        onChange={(e) => setDetailAddressInput(e.target.value)}
                                        placeholder="상세주소만 입력 (예: 101호, 이디아카페 등)"
                                    />
                                    <p className="help-text">기존 주소의 상세주소 부분만 변경됩니다.</p>
                                </div>
                            )}

                            {/* 주소 선택 후 선택된 위치 지도 표시 */}
                            {addressInfo.lat && addressInfo.lng && addressInfo.address && (
                                <div className="map-preview">
                                    <h4>새로 선택한 위치</h4>
                                    <KakaoMap
                                        key={`edit-map-${Date.now()}-${addressInfo.lat}-${addressInfo.lng}`}
                                        lat={addressInfo.lat}
                                        lng={addressInfo.lng}
                                        place={`${addressInfo.address} ${addressInfo.detailAddress || ''}`.trim()}
                                        height="250px"
                                    />
                                </div>
                            )}
                        </div>
                        <button className="custom-button submit" onClick={handleEditSubmit}>
                            저장하기
                        </button>
                    </div>
                ) : (
                    <div className="schedule-info">
                        <h3>{schedule.meetingTitle}</h3>
                        <p className="meeting-time">{schedule.meetingTime}</p>
                        {schedule.meetingPlace && (
                            <p className="meeting-place">장소: {schedule.meetingPlace}</p>
                        )}

                        {/* 지도 표시 */}
                        {(schedule.lat && schedule.lng) && (
                            <div className="map-container">
                            <KakaoMap
                                    key={`view-map-${scheduleId}-${schedule.lat}-${schedule.lng}`}
                                    lat={schedule.lat}
                                    lng={schedule.lng}
                                    place={schedule.meetingPlace}
                                    height="300px"
                                />
                                <p>위치 정보: {schedule.lat}, {schedule.lng}</p>
                            </div>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ScheduleDetail;