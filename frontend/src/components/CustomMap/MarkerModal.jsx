import { CustomOverlayMap } from "react-kakao-maps-sdk";

const MarkerModal = ({ mark }) => {
  return (
    <CustomOverlayMap position={{ lat: mark.lat, lng: mark.lng }} yAnchor={1.5}>
      <div className='custom-overlay'>
        <h4>{mark.name}</h4>
        <p>{mark.description}</p>
        <button onClick={() => mark(null)}>Close</button>
      </div>
    </CustomOverlayMap>
  );
};

export default MarkerModal;
