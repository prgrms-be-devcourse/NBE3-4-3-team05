import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserService } from "src/services/UserService";
import "./Mypage.css"; // 스타일 파일 import

const Mypage = () => {
  const [userInfo, setUserInfo] = useState(null);
  const [userClassInfo, setUserClassInfo] = useState(null);
  const [userScheduleInfo, setUserScheduleInfo] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await UserService.getUserInfo();
        if (response.data?.isSuccess) {
          console.log("회원 정보 조회 성공", response.data.data);
          setUserInfo(response.data.data);
        }
      } catch (error) {
        console.error("회원 정보 조회 실패", error);
      }
    };

    const fetchUserClassInfo = async () => {
      try {
        const response = await UserService.getUserClassInfo();
        if (response.data?.isSuccess) {
          console.log("클래스 정보 조회 성공", response.data.data);
          setUserClassInfo(response.data.data.classInfo);
        }
      } catch (error) {
        console.error("클래스 정보 조회 실패", error);
      }
    };

    const fetchUserScheduleInfo = async () => {
      try {
        const response = await UserService.getUserScheduleInfo();
        if (response.data?.isSuccess) {
			console.log("회원 스케줄 리스트 조회 성공", response.data.data);
			setUserScheduleInfo(response.data.data.schedule);
		}
      } catch (error) {
        console.error("회원 스케줄 리스트 조회 실패", error);
      }
    };

    fetchUserInfo();
    fetchUserClassInfo();
    fetchUserScheduleInfo();
  }, []);

  const handleModifyClick = () => {
    navigate("/modify");
  };

  const handleClassClick = (classId) => {
    navigate(`/classes/${classId}`);
  };

  const handleScheduleClick = (classId) => {
    navigate(`/classes/${classId}`);
  };

  if (!userInfo || !userClassInfo || !userScheduleInfo) return <p>회원 정보를 불러오는 중입니다...</p>;

  return (
    <section className="mypage">
      <h1>마이페이지</h1>

      {/* 회원 정보 카드 */}
      <div className="info-card">
        <h2>닉네임</h2>
        <p>{userInfo.nickname}</p>

        <h2>관심사 목록</h2>
        <div className="favorite-list">
          {userInfo.favorite.map((item, index) => (
            <div key={index} className="favorite-item">
              {item}
            </div>
          ))}
        </div>

        <h2>회원 정보</h2>
        <p><strong>회원 타입:</strong> {userInfo.type}</p>
        <p><strong>가입일:</strong> {userInfo.createdAt}</p>
      </div>

      <button
        onClick={handleModifyClick}
        className="button modify-button"
      >
        정보 수정
      </button>

      {/* 클래스 정보 섹션 */}
      <section className="class-section">
        <h2>참여한 클래스</h2>
        {userClassInfo.map((classItem) => (
          <div key={classItem.classId} className="class-item">
            <h3>{classItem.name}</h3>
            <p>{classItem.description}</p>
            <p><strong>관심사 :</strong> {classItem.favorite}</p>
            <button
              onClick={() => handleClassClick(classItem.classId)}
            >
              방 이동하기
            </button>
          </div>
        ))}
      </section>

      {/* 스케줄 정보 섹션 */}
      <section className="schedule-section">
        <h2>회원 모임 일정</h2>
        {userScheduleInfo.map((scheduleItem) => (
          <div key={scheduleItem.classId} className="schedule-item">
            <h3>{scheduleItem.classTitle}</h3>
            <p><strong>모임 시간 :</strong> {scheduleItem.meetingTime}</p>
			<p><strong>모임 장소 :</strong> {scheduleItem.meetingPlace || '위치 정보 없음'}</p>
            <button
              onClick={() => handleScheduleClick(scheduleItem.classId)}
            >
              방 이동하기
            </button>
          </div>
        ))}
      </section>
    </section>
  );
};

export default Mypage;
