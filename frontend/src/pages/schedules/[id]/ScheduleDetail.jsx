import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ScheduleService } from '../../../services/SheduleService';
import Alert from '../../../components/alert/Alert';
import DateTimeInput from '../../../components/dateTimeInput/DateTimeInput';
import Address from "../../../components/address/Address";
import './ScheduleDetail.css';

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

    const fetchScheduleDetail = async () => {
        try {
            const response = await ScheduleService.getScheduleDetail(scheduleId, classId);
            const detailData = response.data?.data;

            if (detailData) {
                setSchedule(detailData);

                // 기본값 설정
                setMeetingTitle(detailData.meetingTitle || '');
                setMeetingTime(detailData.meetingTime || '');

                // 주소 정보 설정
                const mainAddress = detailData.meetingPlace ? detailData.meetingPlace.split(' (')[0] : '';
                const extraAddressPart = detailData.meetingPlace && detailData.meetingPlace.includes('(') ?
                    `(${detailData.meetingPlace.split('(')[1]}` : '';

                setAddressInfo({
                    address: mainAddress,
                    detailAddress: '',
                    extraAddress: extraAddressPart,
                    postcode: '',
                    lat: detailData.lat || '',
                    lng: detailData.lng || ''
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

        const initialTitle = schedule.meetingTitle || '';
        const initialTime = schedule.meetingTime || '';
        const initialPlace = schedule.meetingPlace || '';
        const initialLat = parseFloat(schedule.lat) || '';
        const initialLng = parseFloat(schedule.lng) || '';

        // 현재 상태의 주소 정보 계산
        const currentPlace = addressInfo.address ?
            `${addressInfo.address} ${addressInfo.detailAddress || ''}`.trim() :
            '';
        const currentLat = parseFloat(addressInfo.lat) || '';
        const currentLng = parseFloat(addressInfo.lng) || '';

        return meetingTitle !== initialTitle ||
            meetingTime !== initialTime ||
            currentPlace !== initialPlace ||
            currentLat !== initialLat ||
            currentLng !== initialLng;
    };

    // 폼을 원래 값으로 리셋
    const resetFormToOriginal = () => {
        if (!schedule) return;

        setMeetingTitle(schedule.meetingTitle || '');
        setMeetingTime(schedule.meetingTime || '');

        const mainAddress = schedule.meetingPlace ? schedule.meetingPlace.split(' (')[0] : '';
        const extraAddressPart = schedule.meetingPlace && schedule.meetingPlace.includes('(') ?
            `(${schedule.meetingPlace.split('(')[1]}` : '';

        setAddressInfo({
            address: mainAddress,
            detailAddress: '',
            extraAddress: extraAddressPart,
            postcode: '',
            lat: schedule.lat || '',
            lng: schedule.lng || ''
        });
    };

    // 편집 모드 토글 핸들러
    const toggleEditMode = () => {
        if (isEditing && hasChanges()) {
            // 변경 사항이 있는데 취소를 누른 경우
            if (window.confirm('변경 사항이 있습니다. 취소하시겠습니까?')) {
                resetFormToOriginal();
                setIsEditing(false);
            }
        } else {
            // 편집 모드로 들어가거나, 변경 사항 없이 취소하는 경우
            setIsEditing(!isEditing);
            if (!isEditing) {
                // 편집 모드로 들어갈 때 현재 값으로 초기화
                resetFormToOriginal();
            }
        }
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
        const meetingPlace = addressInfo.address ?
            `${addressInfo.address} ${addressInfo.detailAddress || ''}`.trim() :
            '';

        // 주소가 변경되지 않았다면 기존 위치 정보 사용
        let finalLat, finalLng;

        if (addressInfo.address) {
            // 새 주소를 입력한 경우 addressInfo의 lat/lng 사용
            finalLat = parseFloat(addressInfo.lat);
            finalLng = parseFloat(addressInfo.lng);

            // 새 주소는 있는데 좌표가 없는 경우 (주소 검색은 했지만 좌표를 못 가져온 경우)
            if (isNaN(finalLat) || isNaN(finalLng)) {
                Alert("유효한 위치 정보가 필요합니다. 주소를 다시 검색해주세요.");
                return;
            }
        } else {
            // 주소를 변경하지 않은 경우 기존 좌표 사용
            finalLat = parseFloat(schedule.lat);
            finalLng = parseFloat(schedule.lng);

            // 기존 좌표도 유효하지 않은 경우
            if (isNaN(finalLat) || isNaN(finalLng)) {
                Alert("유효한 위치 정보가 필요합니다. 주소를 검색해주세요.");
                return;
            }
        }

        const body = {
            meetingTitle,
            meetingTime,
            meetingPlace: meetingPlace || schedule.meetingPlace, // 새 주소가 없으면 기존 주소 사용
            lat: finalLat || 37.5665, // 서울 중심부 좌표를 기본값으로 설정
            lng: finalLng || 126.9780 // 서울 중심부 좌표를 기본값으로 설정
        };

        try {
            // scheduleId와 classId를 함께 전달
            const response = await ScheduleService.putSchedulesLists(scheduleId, classId, body);
            if (response.status === 200) {
                Alert('일정이 수정되었습니다.', '', '', () => {
                    setIsEditing(false);
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
                            <DateTimeInput
                                onMeetingTimeChange={handleMeetingTimeChange}
                                initialDateTime={meetingTime}
                            />
                        </div>
                        <div className="form-group">
                            <label>모임 장소:</label>
                            <Address
                                onAddressSelect={handleAddressSelect}
                                initialAddress={addressInfo.address || ''}
                                initialLat={addressInfo.lat || ''}
                                initialLng={addressInfo.lng || ''}
                            />
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
                        {/* 위도/경도 정보는 필요하다면 여기에 추가 */}
                        {(schedule.lat && schedule.lng) && (
                            <div className="map-container">
                                {/* 여기에 카카오맵 미니뷰를 추가할 수 있습니다 */}
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