import React, { useEffect, useState } from "react";
import CustomList from "src/components/customList/CustomList";
import { ClassService } from "src/services/ClassService";
import { useParams } from "react-router-dom";
import Alert from "src/components/alert/Alert";
import "./MemberList.css";

const MemberList = () => {
  const { id } = useParams();
  const [members, setMembers] = useState([]);
  const [masterName, setMasterName] = useState("");

  const result = () => {
    setTimeout(() => {
      window.location.reload();
    }, 100);
  };

  // 모임 회원 조회
  useEffect(() => {
    const fetchMemberList = async () => {
      try {
        const response = await ClassService.memberListByClass(id);
        if (!response) {
          throw new Error("fetch 실패");
        }
        const jsonData = response.data;
        const classDetailInfo = jsonData?.data || [];

        setMembers(classDetailInfo);

        const master = classDetailInfo?.userList.find(
          (user) => user.userId === classDetailInfo.masterId,
        );
        setMasterName(master ? master.nickName : "no master");
      } catch (error) {
        console.error("Error fetching members:", error);
      }
    };

    fetchMemberList();
  }, [id]);

  // 권한 위임
  const handlerTransferMaster = async (userId) => {
    try {
      const response = await ClassService.transferMaster(id, userId);
      if (response.status === 200) {
        Alert("권한이 성공적으로 위임되었습니다.", "", "", () => result());
      } else {
        Alert("권한 위임에 실패했습니다.");
      }
    } catch (error) {
      console.error("권한 위임 오류:", error);
      Alert(error.response.data.message, "", "", () => result());
    }
  };

  // 강퇴
  const handlerKickOut = async (userId) => {
    try {
      const response = await ClassService.kickOut(id, userId);
      if (response.status === 200) {
        Alert("강퇴했습니다.", "", "", () => result());
      } else {
        Alert("강퇴 실패했습니다.");
      }
    } catch (error) {
      console.error("강퇴 오류:", error);
      if (error.response.data.code === 3006) {
        Alert("권한이 없습니다.", "", "", () => result());
      }
      Alert(error.response.data.message, "", "", () => result());
    }
  };

  if (members.length === 0) {
    return <p>회원 정보가 없습니다.</p>;
  }

  return (
    <div className="member-container">
      <p>모임 이름 : {members.name}</p>
      <p>모임장 : {masterName}</p>
      <div className="list-container">
        <ul className="list">
          {members?.userList.map((user) => (
            <CustomList
              data1={user.userId}
              data2={user.nickName}
              title="회원 목록"
              description="모임에 속한 회원 목록"
              check={true}
              button1="권한 위임"
              button2="강퇴"
              onClick1={() => handlerTransferMaster(user.userId)}
              onClick2={() => handlerKickOut(user.userId)}
            />
          ))}
        </ul>
      </div>
    </div>
  );
};

export default MemberList;
