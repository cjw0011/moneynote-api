package cn.biq.mn.security;

import cn.biq.mn.base.BaseEntity;
import cn.biq.mn.user.User;
import cn.biq.mn.validation.TimeField;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_refresh_token")
@Getter
@Setter
public class RefreshToken extends BaseEntity {

    @Column(length = 512, nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    @TimeField
    private Long expireTime;

    @Column(nullable = false)
    @TimeField
    private Long lastUsedTime;

}

