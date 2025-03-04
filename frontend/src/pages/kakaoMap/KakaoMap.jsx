import React from "react";
import { dummy } from "src/components/CustomMap/dummy";
import CustomMap from "src/components/CustomMap/CustomMap";

const KakaoMap = () => {
  const DUMMY = dummy;
  return (
    <div>
      <CustomMap data={DUMMY} />
    </div>
  );
};

export default KakaoMap;
