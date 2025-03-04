import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import ReactDOM from "react-dom/client";
import Home from "./pages/home/Home";
import Login from "./pages/login/Login";
import SignUp from "./pages/signUp/SignUp";
import Class from "./pages/class/[id]/Class";
import KakaoMap from "./pages/kakaoMap/KakaoMap";
import Mypage from "./pages/mypage/Mypage";
import Modify from "./pages/mypage/Modify";
import KakaoCallback from "./components/kakao/KakaoCallback";
import MemberList from "./pages/class/[id]/MemberList";
import Layout from "./pages/layout/Layout";
import ScheduleDetail from "./pages/schedules/[id]/ScheduleDetail";

const App = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/kakao/callback" element={<KakaoCallback />} />
          <Route path="/modify" element={<Modify />} />
          <Route path="/join" element={<SignUp />} />
          <Route path="/mypage" element={<Mypage />} />
          <Route path="/classes/:id" element={<Class />} />
          <Route path="/kakaomap" element={<KakaoMap />} />
          <Route path="/memberList/:id" element={<MemberList />} />
          <Route path="/schedules/:scheduleId/classes/:classId" element={<ScheduleDetail />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};

// @ts-ignore
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
