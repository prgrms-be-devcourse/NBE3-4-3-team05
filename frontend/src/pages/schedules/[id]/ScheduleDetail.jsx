import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ScheduleService } from '../../../services/SheduleService';
import Alert from '../../../components/alert/Alert';
import DateTimeInput from '../../../components/dateTimeInput/DateTimeInput';
import './ScheduleDetail.css';

const ScheduleDetail = () => {
    const { scheduleId, classId } = useParams();
    const navigate = useNavigate();
    const [schedule, setSchedule] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [meetingTitle, setMeetingTitle] = useState('');
    const [meetingTime, setMeetingTime] = useState('');

    const fetchScheduleDetail = async () => {
        try {
            const response = await ScheduleService.getScheduleDetail(scheduleId, classId);
            const detailData = response.data?.data;
            setSchedule(detailData);
            setMeetingTitle(detailData.meetingTitle);
            setMeetingTime(detailData.meetingTime);
        } catch (error) {
            console.error('일정 상세 조회 오류:', error);
            Alert(error.response?.data?.message || '일정 상세 조회에 실패했습니다.', '', '', () => navigate(`/classes/${classId}`));
        }
    };

    const handleMeetingTimeChange = useCallback((formattedDateTime) => {
        setMeetingTime(formattedDateTime);
    }, []);

    const handleBackButton = () => {
        // 수정 모드가 아닐 때만 클래스 페이지로 이동
        navigate(`/classes/${classId}`);
    };

    const handleEditSubmit = async () => {
        // 수정 모드로 전환시 초기 값 설정
        const initialTitle = schedule.meetingTitle;
        const initialTime = schedule.meetingTime;

        // 값이 변경되었는지 확인
        if (meetingTitle === initialTitle && meetingTime === initialTime) {
            Alert("변경된 내용이 없습니다.");
            return;
        }

        if (!meetingTitle.trim()) {
            Alert("일정 제목을 입력해주세요.");
            return;
        }

        if (!meetingTime) {
            Alert("날짜를 선택해주세요.");
            return;
        }

        const body = {
            meetingTitle,
            meetingTime,
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
                    <button className="custom-button" onClick={() => setIsEditing(!isEditing)}>
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
                        <button className="custom-button submit" onClick={handleEditSubmit}>
                            저장하기
                        </button>
                    </div>
                ) : (
                    <div className="schedule-info">
                        <h3>{schedule.meetingTitle}</h3>
                        <p className="meeting-time">{schedule.meetingTime}</p>
                        {/* 추가적인 일정 정보가 있다면 여기에 표시 */}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ScheduleDetail;