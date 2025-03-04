import React from "react";
import { Outlet } from "react-router-dom";
import "./Layout.css";
import Header from "src/components/header/Header";
import Footer from "src/components/footer/Footer";
import { useNavigate } from "react-router-dom";

const Layout = () => {
  const navigate = useNavigate();
  const goBack = () => {
    navigate(-1); // -1은 이전 페이지로 이동
  };

  return (
    <div className="layout">
      <Header />
      <main>
        <button className="back" onClick={goBack}>
          &lt; Back
        </button>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};

export default Layout;
