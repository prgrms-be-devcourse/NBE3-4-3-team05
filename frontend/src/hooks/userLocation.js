import { useState, useEffect } from "react";

export const useUserLocation = () => {
  const [location, setLocation] = useState(null);
  const [errorMessage, setErrorMessage] = useState(null);
  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(success, error, {
        timeout: 10000,
      });
    } else {
      setErrorMessage("Geolocation is not supported by this browser.");
    }

    function success(position) {
      console.log("Location success:", position);
      setLocation({
        latitude: position.coords.latitude,
        longitude: position.coords.longitude,
      });
    }

    function error(err) {
      console.log("Location error:", err);
      setErrorMessage("Failed to get location. Using default location.");
      setLocation({
        latitude: 37.483034,
        longitude: 126.902435,
      });
    }
  }, []);

  return { location, errorMessage };
};
