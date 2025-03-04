// @ts-nocheck
import React from "react";
import ReactDOM from "react-dom";
import "./Modal.css";

const Modal = ({ isOpen, title, contents, onClose, external, type,children }) => {
  if (!isOpen) return null;

  const handleOverlayClick = () => {
    if (external) {
      onClose();
    }
  };

  return ReactDOM.createPortal(
    <div className='modal-overlay' onClick={handleOverlayClick}>
      <div className={`modal ${type && type}`}>
        <div className='modal-header'>
          <h3 className='modal-info'>{title}</h3>
          <button className='close-btn' onClick={onClose}>
            <span className='close-icon'>X</span>
          </button>
        </div>
        <div className='modal-container'>
			{children}
        </div>
      </div>
    </div>,
    document.getElementById("modal-root")
  );
};

export default Modal;
