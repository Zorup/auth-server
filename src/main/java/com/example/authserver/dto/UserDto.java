package com.example.authserver.dto;

import com.example.authserver.domain.Role;
import com.example.authserver.domain.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@AllArgsConstructor // 빌더패턴에 사용
@Getter
@Setter
@Builder
public class UserDto {
    private Long userId;

    private Role role;

    private String name;

    private String loginId;

    private String password;

    private String position;

    private String department;

    private String email;

    private byte[] image;

    public UserDto(User u){
        this.userId = u.getUserId();
        this.role = u.getRole();
        this.name = u.getName();
    }

    public static UserDto of(User user) {   // @Builder로 생성한 빌더클래스 활용, Entity to Dto 전환용. UserDto.of(user) 로 사용 가능
        return new UserDtoBuilder()
                .userId(user.getUserId())
                .role(user.getRole())
                .name(user.getName())
                .loginId(user.getLoginId())
                .password(user.getPassword())
                .position(user.getPosition())
                .department(user.getDepartment())
                .email(user.getEmail())
                .image(user.getImage())
                .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////
    // sub DTOs

    @NoArgsConstructor(access = AccessLevel.PRIVATE)    // 기본생성자 사용 차단
    @Getter
    @Setter
    public static class ForLogin {  // Controller에서 login request 받을때에 파라미터 매핑용으로만 사용
        private String loginId;
        private String password;
    }

    @AllArgsConstructor // 빌더패턴에 사용
    @NoArgsConstructor(access = AccessLevel.PRIVATE)    // for redis deserialize
    @Getter
    @Setter
    @Builder
    public static class ForRedis {
        public final static long accessTokenValidTime = 3 * 60;
        public final static long refreshTokenValidTime = 5 * 60; // 테스트 위해 5분으로 해둠

        private Long userId;
        private String loginId;
        private Role role;
        private String refreshToken;
        private String clientIp;

        public static ForRedis of(User user){   // @Builder로 생성한 빌더클래스 활용, Entity to Dto 전환용. UserRedisDto.of(user) 로 사용 가능
            return new ForRedisBuilder()
                    .userId(user.getUserId())
                    .loginId(user.getLoginId())
                    .role(user.getRole())
                    .build();
        }
    }

    @AllArgsConstructor // 빌더패턴에 사용
    @Getter
    @Setter
    @Builder
    public static class ForResponse {
        private Long userId;

        private Role role;

        private String name;

        private String loginId;

        private String position;

        private String department;

        private String email;

        private byte[] image;

        private String refreshToken;

        public static ForResponse of(User user){
            return new ForResponseBuilder()
                    .userId(user.getUserId())
                    .role(user.getRole())
                    .name(user.getName())
                    .loginId(user.getLoginId())
                    .position(user.getPosition())
                    .department(user.getDepartment())
                    .email(user.getEmail())
                    .image(user.getImage())
                    .build();
        }
    }


}
