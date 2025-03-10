import { CustomOverlayMap } from "react-kakao-maps-sdk";
import { useNavigate } from "react-router-dom";
import Alert from "src/components/alert/Alert";
import { ClassService } from 'src/services/ClassService';

const MarkerModal = ({ mark, onClose }) => {
  const navigate = useNavigate();

  const result = (id) => {
    navigate(`/classes/${id}`);
  }

  const handlerJoin = async (id) => {
    const isMember = await checkMember(id);
    const isBlackList = await checkBlackList(id);
    if (isBlackList) {
      Alert("강퇴당한 회원은 재가입 하실 수 없습니다.")
    } else if (isMember) {
      result(id);
    } else {
      Alert("가입된 회원이 아닙니다. <br>가입 하시겠습니까?", "취소", "가입", (e) => {if (e) {joinClass(id)}});
    }
  }

  // 모임 가입 확인
  const checkMember = async (id) => {
    try {
      const response = await ClassService.checkMember(id);
      const checkData = response.data;
      const getData = checkData?.data || [];
      const isMember = getData.member;
      return isMember;
    } catch (error) {
      console.error("checkMember:", error);
    }
  };

  // 블랙리스트 확인
  const checkBlackList = async (id) => {
    try {
      const response = await ClassService.checkBlackList(id);
      const checkData = response.data;
      const getData = checkData?.data || [];
      const isBlackList = getData.blackListed;
      return isBlackList;
    } catch (error) {
      console.error("checkBlackList:", error);
    }
  };

  // 모임 가입
  const joinClass = async (id) => {
    try {
      const response = await ClassService.joinClass(id);
      if (response.status === 200) {
        Alert("모임에 가입되었습니다.", "", "", () => result(id));
      } else {
        Alert("가입에 실패했습니다");
      }
    } catch (error) {
      console.error("join error:", error);
      Alert(error.response.data.message);
    }
  }

  return (
    <CustomOverlayMap position={{ lat: mark.lat, lng: mark.lng }} yAnchor={1.5}>
      <div className="custom-overlay">
        <h4>{mark.classTitle}</h4>
        <p>날짜: {mark.date}</p>
        <p>관심사: {mark.favorite}</p>
        <button className='enter' onClick={() => handlerJoin(mark.id)}>입장</button>
        <button onClick={onClose}>Close</button>
      </div>
    </CustomOverlayMap>
  );
};

export default MarkerModal;