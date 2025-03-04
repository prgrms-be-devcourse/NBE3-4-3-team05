import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Form from "src/components/form/Form";
import Title from "src/components/title/Title";
import Alert from "src/components/alert/Alert";

import { Element } from "src/constants/element";
import { UserService } from "src/services/UserService";
import { FavoriteService } from "src/services/FavoriteService";
import Validate from "src/hooks/validate";

const SignUp = () => {
  const router = useNavigate();
  const [body, setBody] = useState({ nickname: "", loginId: "", password: "", favorite: [] });
  const [isLoading, setIsLoading] = useState(false);
  const [favoriteOptions, setFavoriteOptions] = useState([]);
  
  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        const response = await FavoriteService.getFavoriteList();
        console.log("Favorite list response:", response);
        if (response.data?.isSuccess) {
          setFavoriteOptions(response.data.data);
        }
      } catch (error) {
        console.error("Error fetching favorite list:", error);
      }
    };

    fetchFavorites();
  }, []);

  const handleFavoriteSelect = (favoriteName) => {
    setBody((prev) => {
      const updatedFavorites = prev.favorite.includes(favoriteName)
        ? prev.favorite.filter((item) => item !== favoriteName)
        : [...prev.favorite, favoriteName];
      return { ...prev, favorite: updatedFavorites };
    });
  };

  const onChangeInput = (name = "", e) => {
    setBody((prev) => ({ ...prev, [name]: e }));
  };

  const result = () => {
    console.log("회원가입 완료");
    setIsLoading(false);
    router("/login");
  };

  const onClickSignUp = async (e) => {
    e.preventDefault();

    if (!Validate(body)) return;
    try {
      setIsLoading(true);
      const res = await UserService.SignUp(body);
      if (!res) {
        Alert("회원가입에 실패했습니다. \n 다시 시도해주세요.", () =>
          setIsLoading(false)
        );
      } else {
        Alert("회원가입 성공!", () => result());
      }
    } catch (error) {
      Alert("회원가입 중 오류가 발생했습니다.", "", "", () =>
        setIsLoading(false)
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section>
      <Title type="title" text="회원가입" />
      <Form
        title="sign-up"
        element={Element.SIGN_UP_FORM}
        onSubmit={onClickSignUp}
        onChange={onChangeInput}
        disabled={isLoading}
      />

      <div>
        <h2>관심사 선택</h2>
        <div className="favorite-checkboxes">
          {favoriteOptions.map((favorite) => (
            <label key={favorite.id} style={{ display: "block", margin: "5px 0" }}>
              <input
                type="checkbox"
                value={favorite.favoriteName}
                checked={body.favorite.includes(favorite.favoriteName)}
                onChange={() => handleFavoriteSelect(favorite.favoriteName)}
              />
              {favorite.favoriteName}
            </label>
          ))}
        </div>
      </div>
    </section>
  );
};

export default SignUp;

