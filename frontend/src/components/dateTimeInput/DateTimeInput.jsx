import React, { useState, useEffect } from "react";
import "./DateTimeInput.css";

function DateTimeInput({ onMeetingTimeChange }) {
  const today = new Date();

  const [year, setYear] = useState("");
  const [month, setMonth] = useState("");
  const [day, setDay] = useState("");

  useEffect(() => {
    if (year && month && day) {
      const dateTime = new Date(year, month - 1, day);
      // 형식을 'YYYY-MM-DD HH:mm:ss'로 변경하고 시간은 00:00:00으로 설정
      const formattedDateTime = `${dateTime.getFullYear()}-${String(dateTime.getMonth() + 1).padStart(2, "0")}-${String(dateTime.getDate()).padStart(2, "0")}`;

      // 날짜 형식이 어떻게 전송되는지 확인
      console.log('Formatted Date:', formattedDateTime);
      onMeetingTimeChange(formattedDateTime);
    }
  }, [year, month, day, onMeetingTimeChange]);

  const generateOptions = (start, end, type) => {
    let options = [];
    for (let i = start; i <= end; i++) {
      options.push(i);
    }

    return options.map((i) => {
      let isDisabled = false;
      if (type === "year" && i < today.getFullYear()) isDisabled = true;
      if (
        type === "month" &&
        year === today.getFullYear() &&
        i < today.getMonth() + 1
      )
        isDisabled = true;
      if (
        type === "day" &&
        year === today.getFullYear() &&
        month === today.getMonth() + 1 &&
        i < today.getDate()
      )
        isDisabled = true;

      return (
        <option key={i} value={i} disabled={isDisabled}>
          {i}
        </option>
      );
    });
  };

  return (
    <form className="datetime-form">
      <div className="input-group">
        <label>년:</label>
        <select value={year} onChange={(e) => setYear(e.target.value)}>
          <option value="">선택</option>
          {generateOptions(2025, today.getFullYear(), "year")}
        </select>
      </div>
      <div className="input-group">
        <label>월:</label>
        <select
          value={month}
          onChange={(e) => setMonth(e.target.value)}
          disabled={!year}
        >
          <option value="">선택</option>
          {generateOptions(1, 12, "month")}
        </select>
      </div>
      <div className="input-group">
        <label>일:</label>
        <select
          value={day}
          onChange={(e) => setDay(e.target.value)}
          disabled={!month}
        >
          <option value="">선택</option>
          {generateOptions(1, 31, "day")}
        </select>
      </div>
    </form>
  );
}

export default DateTimeInput;
