import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Form from "src/components/form/Form";
import Title from "src/components/title/Title";
import Alert from "src/components/alert/Alert";

import { Element } from "src/constants/element";
import { UserService } from "src/services/UserService";
import { FavoriteService } from "src/services/FavoriteService";

const Mypage = () => {
  const router = useNavigate();
  const [body, setBody] = useState({ nickname: "", favorite: [] });
  const [isLoading, setIsLoading] = useState(false);
  const [favoriteOptions, setFavoriteOptions] = useState([]);

  useEffect(() => {
    const fetchFavorites = async () => {
      try {
        const response = await FavoriteService.getFavoriteList();
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

  const onChangeInput = (name, value) => {
    setBody((prev) => ({ ...prev, [name]: value }));
  };

  const handleModifyUserInfo = async (e) => {
    e.preventDefault();
    if (!body.nickname.trim()) {
      Alert("닉네임을 입력해 주세요.");
      return;
    }

    try {
      setIsLoading(true);
      const response = await UserService.ModifyUserInfo(body);
      if (response.data?.isSuccess) {
        Alert("회원 정보 수정 성공!");
      } else {
        Alert("회원 정보 수정에 실패했습니다.");
      }
    } catch (error) {
      Alert("회원 정보 수정 중 오류가 발생했습니다.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <section>
      <Title type="title" text="마이페이지 수정" />

      {/* Form 컴포넌트는 입력 필드에만 집중 */}
      <Form
        title="modify-info"
        element={Element.MODIFY_FORM}
        onSubmit={handleModifyUserInfo}
        onChange={onChangeInput}
        disabled={isLoading}
      >
        <div>
          <label>닉네임</label>
          <input
            type="text"
            value={body.nickname}
            onChange={(e) => onChangeInput("nickname", e.target.value)}
            placeholder="닉네임을 입력해 주세요"
          />
        </div>
        <button type="submit" disabled={isLoading}>
          정보 수정
        </button>
      </Form>

      {/* 관심사 선택 컴포넌트 분리 */}
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

export default Mypage;
