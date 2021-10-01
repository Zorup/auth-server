package com.example.authserver.domain;


import com.example.authserver.dto.UserDto;
import lombok.*;

import javax.persistence.*;


// entity는 public or protected 기본생성자 필요, protected 설정함으로써 불완전한 객체 생성 방지, 이 생성자만으로는 builder 생성 불가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // builder 생성 위해
@Data   // getter, setter, embeddedId 대비 hashcode와 equals (중복삽입 방지)
@Builder(builderClassName = "UserBuilder")
@Entity
@Table(name = "USER_TB")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String name;

    @Column
    private String loginId;

    @Column
    private String password;

    @Column
    private String position;

    @Column
    private String department;

    @Column
    private String email;

    @Lob    // byte[] -> BLOB
    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

//    @Column
//    private String pushToken;     // redis에만 저장 예정

    public static User of(UserDto userDto){   // dto to entity 변환용, User.of(userDto) 로 사용 가능
        return new UserBuilder()
                .userId(userDto.getUserId())
                .role(userDto.getRole())
                .name(userDto.getName())
                .loginId(userDto.getLoginId())
                .password(userDto.getPassword())
                .position(userDto.getPosition())
                .department(userDto.getDepartment())
                .email(userDto.getEmail())
                .image(userDto.getImage())
                .build();
    }




}
