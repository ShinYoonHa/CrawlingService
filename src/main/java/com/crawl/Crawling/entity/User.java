package com.crawl.Crawling.entity;

import com.crawl.Crawling.constant.Role;
import com.crawl.Crawling.constant.Social;
import com.crawl.Crawling.dto.UserDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Column(unique = true)
    private String email;
    private String password;
    private String tel;
    private String gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Social social;

    //연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Likes> likes;

    //연관관계 매핑
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RecentView> recentViews;

    public static User createUser(UserDto userDto, PasswordEncoder passwordEncoder) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        String pw = passwordEncoder.encode(userDto.getPassword());
        user.setPassword(pw);
        user.setTel(userDto.getTel());
        user.setGender(userDto.getGender());
        user.setRole(Role.ADMIN);
        user.setSocial(Social.LOCAL);

        return user;
    }
}
