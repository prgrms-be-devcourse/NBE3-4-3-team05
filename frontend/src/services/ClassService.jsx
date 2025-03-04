// @ts-nocheck
import axiosInstance from "src/constants/axiosInstance";
import { Project } from "src/constants/project";

// 모임 리스트 가져오기
const getClassLists = async () => {
  const response = await axiosInstance.get(`${Project.API_URL}/login`, {
    withCredentials: true,
  });
  return response;
};

// 모임 생성
const postClassLists = async (body) => {
  const response = await axiosInstance.post(
    `${Project.API_URL}/classes`,
    body,
    { withCredentials: true },
  );
  return response;
};

// 모임 입장 (모임 정보 조회)
const getClassInfo = async (classId) => {
  const response = await axiosInstance.get(
    `${Project.API_URL}/classes/${classId}`,
    { withCredentials: true },
  );
  return response;
};

// 모임 수정
const putClassLists = async (body, classId) => {
  const response = await axiosInstance.patch(
    `${Project.API_URL}/classes/${classId}`,
    body,
    { withCredentials: true },
  );
  return response;
};

// 모임 삭제
const deleteClassLists = async (classId) => {
  const response = await axiosInstance.delete(
    `${Project.API_URL}/classes/${classId}`,
    { withCredentials: true },
  );
  return response;
};

// 모임 권한 위임
const transferMaster = async (classId, userId) => {
  const response = await axiosInstance.patch(
    `${Project.API_URL}/classes/${classId}/users/${userId}/role`,
    { withCredentials: true },
  );
  return response;
};

// 모임 회원 강퇴
const kickOut = async (classId, userId) => {
  const response = await axiosInstance.delete(
    `${Project.API_URL}/classes/${classId}/users/${userId}`,
    { withCredentials: true },
  );
  return response;
};

// 모임 가입
const joinClass = async (classId) => {
  const response = await axiosInstance.post(
    `${Project.API_URL}/classes/${classId}/membership`,
    { withCredentials: true },
  );
  return response;
};

// 모임 탈퇴
const resignClass = async (classId) => {
  const response = await axiosInstance.delete(
    `${Project.API_URL}/classes/${classId}/membership`,
    { withCredentials: true },
  );
  return response;
};

// 가입한 회원 목록 조회
const memberListByClass = async (classId) => {
  const response = await axiosInstance.get(
    `${Project.API_URL}/classes/${classId}/memberList`,
    { withCredentials: true },
  );
  return response;
};

// 재가입 확인 (가입한 회원)
const checkMember = async (classId) => {
  const response = await axiosInstance.get(
    `${Project.API_URL}/classes/${classId}/checkMember`,
    { withCredentials: true },
  );
  return response;
};

// 재가입 확인 (강퇴된 회원)
const checkBlackList = async (classId) => {
  const response = await axiosInstance.get(
    `${Project.API_URL}/classes/${classId}/checkBlackList`,
    { withCredentials: true },
  );
  return response;
};

const ClassService = {
  getClassLists,
  postClassLists,
  getClassInfo,
  putClassLists,
  deleteClassLists,
  transferMaster,
  kickOut,
  joinClass,
  resignClass,
  memberListByClass,
  checkMember,
  checkBlackList,
};

export { ClassService };
